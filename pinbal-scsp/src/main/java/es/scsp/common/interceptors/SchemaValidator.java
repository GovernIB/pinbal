package es.scsp.common.interceptors;

import es.caib.pinbal.scsp.SchemaValidatorVfsCompat;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.exceptions.ScspClientInterceptorException;
import es.scsp.common.utils.Mensaje;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.context.MessageContext;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Override de SchemaValidator per compatibilitat amb JBoss EAP VFS.
 *
 * El codi original té un bug: quan getResource() retorna una URL VFS
 * (file:/content/pinbal.ear/lib/...jar/schemas/...) el File resultant no
 * existeix (perquè /content/ és virtual), però el flag useFolder queda a true
 * i crida validateSchemaUsingFolder amb un fitxer inexistent → error Xerces.
 *
 * Fix: si el File derivat de la URL VFS no existeix, useFolder es manté a
 * false i s'usa validateSchemaUsingResolver, que carrega l'esquema via
 * getResourceAsStream i resol els imports relatius (xs:import/xs:include) amb
 * un LSResourceResolver propi basat en el directori classpath de l'esquema.
 */
public class SchemaValidator extends ScspClientHandler {

    private static final Log log = LogFactory.getLog(SchemaValidator.class);

    private final Map<String, String> fileMapping;

    public SchemaValidator(ApplicationContext ctx) {
        super(ctx);
        this.fileMapping = new HashMap<>();
        fileMapping.put(Mensaje.Peticion.toString(), "peticion.xsd");
        fileMapping.put(Mensaje.Respuesta.toString(), "respuesta.xsd");
        fileMapping.put(Mensaje.ConfirmacionPeticion.toString(), "confirmacion-peticion.xsd");
        fileMapping.put(Mensaje.SolicitudRespuesta.toString(), "solicitud-respuesta.xsd");
    }

    private void validateSchemaUsingFolder(File schemaFile, Element element) throws IOException, SAXException {
        log.info(" using folder");
        DOMSource domSource = new DOMSource(element);
        StreamSource streamSource = new StreamSource(schemaFile);
        SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = sf.newSchema(streamSource);
        schema.newValidator().validate(domSource);
    }

    /**
     * Carrega l'esquema des del classpath i usa un LSResourceResolver que
     * resol imports/includes relatius des del directori base de l'esquema.
     * Reemplaça la crida a ClasspathResolver d'OpenSAML que no gestiona
     * paths relatius.
     */
    private void validateSchemaUsingResolver(String serviceCode, String schemaPath, Element element)
            throws SAXException, IOException {
        log.info(" using resolver");
        String baseDir = schemaPath.contains("/") ?
                schemaPath.substring(0, schemaPath.lastIndexOf('/') + 1) : "";

        InputStream is = this.getClass().getResourceAsStream(schemaPath);
        if (is == null) {
            log.debug("No localizado en resource de primer nivel");
            String pathNoSlash = schemaPath.startsWith("/") ? schemaPath.substring(1) : schemaPath;
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathNoSlash);
        }
        if (is == null) {
            log.error("No se encuentran los esquemas ni en segundo nivel...");
        }

        StreamSource streamSource = new StreamSource(is);
        SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        try {
            sf.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", true);
        } catch (Exception e) {
            log.warn("Error al establecer la propiedad " +
                    "\"http://apache.org/xml/features/honour-all-schemaLocations\" a true.");
        }
        sf.setResourceResolver(new SchemaValidatorVfsCompat.ClasspathDirectoryResolver(baseDir));
        sf.newSchema(streamSource).newValidator().validate(new DOMSource(element));
    }

    private boolean validateSchema(MessageContext messageContext, Element element, boolean isRequest)
            throws ScspClientInterceptorException {
        log.debug("Iniciando modulo de validacion de esquema.");
        if (!isModuleActive(messageContext, "ValidarEsquema", isRequest)) {
            return true;
        }

        Servicio servicio = (Servicio) messageContext.getProperty("servicio");
        if (servicio == null) {
            throw new RuntimeException(
                    "No se ha encontrado la configuración del servicio en el contexto SOAP");
        }
        if (servicio.getEsquemas() == null || servicio.getEsquemas().isEmpty()) {
            log.error(String.format(
                    "El servicio %s no tiene definido ningun valor de validacion de esquema. " +
                    "No se realiza la validacion.", servicio.getCodCertificado()));
            return true;
        }

        log.debug(String.format(
                "Iniciando validacion de esquema del mensaje utilizando los esquemas definidos en '%s'.",
                servicio.getEsquemas()));

        Element firstChild = getScspFirstChild(element);
        String messageType = firstChild.getLocalName();
        if (!fileMapping.containsKey(messageType)) {
            throw new RuntimeException("Tipo de mensaje no conocido:" + messageType);
        }

        String originalEsquemas = servicio.getEsquemas();
        String esquemas = originalEsquemas.endsWith("/") ? originalEsquemas : originalEsquemas + "/";
        String schemaPath = esquemas + fileMapping.get(messageType);

        log.debug("Se utilizara el recurso" + schemaPath + " para la validacion");

        // Pas 1: prova path de sistema de fitxers directe (per entorns no-VFS)
        File file = new File(schemaPath);
        boolean useFolder = true;
        if (!file.exists()) {
            useFolder = false;
            URL url = this.getClass().getResource(schemaPath);
            if (url == null) {
                throw new RuntimeException(String.format(
                        "Error al obtener el esquema de validacion para el certificado %s. " +
                        "No se puede obtener el recurso '%s'.", servicio.getCodCertificado(), schemaPath));
            }
            File fileFromUrl;
            try {
                fileFromUrl = new File(URLDecoder.decode(url.getFile(), "ISO-8859-1"));
            } catch (java.io.UnsupportedEncodingException e) {
                fileFromUrl = new File(url.getFile());
            }
            if (fileFromUrl.exists()) {
                // Fitxer real (entorn sense VFS) — usa ruta del sistema de fitxers
                file = fileFromUrl;
                useFolder = true;
            } else {
                // Path VFS o dins JAR: el fitxer no existeix en disc.
                // Verifica que el recurs és accessible via classpath.
                // useFolder es manté a false → usarà validateSchemaUsingResolver.
                URL url2 = this.getClass().getResource(schemaPath);
                if (url2 == null && this.getClass().getResourceAsStream(schemaPath) == null) {
                    throw new RuntimeException(String.format(
                            "Error al obtener el esquema de validacion para el certificado %s. " +
                            "No se puede obtener el recurso '%s'.",
                            servicio.getCodCertificado(), schemaPath));
                }
            }
        }

        log.debug(" file xsd" + file.getAbsolutePath());

        try {
            if (useFolder) {
                validateSchemaUsingFolder(file, firstChild);
            } else {
                validateSchemaUsingResolver(originalEsquemas, schemaPath, firstChild);
            }
        } catch (Exception e) {
            String errorMsg = String.format(
                    "Error durante la validacion del esquema '%s'. %s", schemaPath, e.getMessage());
            String codCertificado = servicio.getCodCertificado();
            String idPeticion = getElementValueXpath(element,
                    String.format("//*[local-name()='%s']", "IdPeticion"));
            String[] details = {errorMsg};
            if (idPeticion != null && !idPeticion.isEmpty()) {
                throw ScspClientInterceptorException.getScspException(
                        null, "0401", details, idPeticion, codCertificado);
            } else {
                String noIdMsg = "No se ha podido encontrar un nodo <IdPeticion> en el mensaje, " +
                        "por lo que no podrá actualizarse el estado en base de datos";
                log.error(noIdMsg);
                throw ScspClientInterceptorException.getScspException("0401", new String[]{noIdMsg});
            }
        }
        return true;
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        Element element = getBodyContentRequest(messageContext);
        return validateSchema(messageContext, element, isEmisor(messageContext));
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
        Element element = getBodyContentResponse(messageContext);
        return validateSchema(messageContext, element, isRequirente(messageContext));
    }

    @Override
    public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception ex)
            throws WebServiceClientException {
    }

    @Override
    public boolean handleRequest(MessageContext messageContext, Object endpoint) throws Exception {
        return handleRequest(messageContext);
    }

    @Override
    public boolean handleResponse(MessageContext messageContext, Object endpoint) throws Exception {
        return handleResponse(messageContext);
    }

    @Override
    public boolean handleFault(MessageContext messageContext, Object endpoint) throws Exception {
        return handleFault(messageContext);
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Object endpoint, Exception ex)
            throws Exception {
    }
}
