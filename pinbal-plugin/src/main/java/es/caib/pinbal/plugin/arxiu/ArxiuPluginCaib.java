package es.caib.pinbal.plugin.arxiu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.pinbal.plugin.PluginMetricHelper;
import es.caib.plugins.arxiu.api.*;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ArxiuPluginCaib extends es.caib.plugins.arxiu.caib.ArxiuPluginCaib implements ArxiuPlugin {

    public static final String ARXIU_BASE_PROP = "es.caib.notib.plugin.arxiu.";

    private static final String ARXIUCAIB_BASE_PROP = ARXIU_BASE_PROP + "caib.";
    private static final String CONCSV_URL_PROP = ARXIUCAIB_BASE_PROP + "concsv.base.url";
    private static final String CONCSV_USUARI_PROP = ARXIUCAIB_BASE_PROP + "concsv.usuari";
    private static final String CONCSV_CONTRASENYA_PROP = ARXIUCAIB_BASE_PROP + "concsv.contrasenya";
    private static final String TIMEOUT_CONNECT = "10000";
    private static final String TIMEOUT_READ = "60000";
    private static final String ERROR_METADADES = "No ha estat possible obtenir les metadades del document amb UUID ";

    private Client versioImprimibleClient;

    @FunctionalInterface
    private interface ArxiuOp<T> {
        T call() throws ArxiuException;
    }

    public ArxiuPluginCaib() {
        throw new RuntimeException("No es possible instanciar la clase ArxiuCaibPluginImpl sense paràmetres");
    }

    public ArxiuPluginCaib(String propertyKeyBase) {
        throw new RuntimeException("No es possible instanciar la clase ArxiuCaibPluginImpl sense propietats");
    }

    public ArxiuPluginCaib(String propertyKeyBase, Properties properties) {
        super(propertyKeyBase, properties);
        PluginMetricHelper.addEndpoint(IntegracioApp.ARX, this.getProperty("plugin.arxiu.caib.base.url"));
    }

    private <T> T tracked(ArxiuOp<T> op) throws ArxiuException {
        try {
            long start = System.currentTimeMillis();
            T result = op.call();
            PluginMetricHelper.addSuccessOperation(IntegracioApp.ARX, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable ex) {
            PluginMetricHelper.addErrorOperation(IntegracioApp.ARX);
            throw ex;
        }
    }

    @Override
    public ContingutArxiu expedientCrear(Expedient expedient) throws ArxiuException {
        return tracked(() -> super.expedientCrear(expedient));
    }

    @Override
    public Expedient expedientDetalls(String identificador, String versio) throws ArxiuException {
        return tracked(() -> super.expedientDetalls(identificador, versio));
    }

    @Override
    public ConsultaResultat expedientConsulta(List<ConsultaFiltre> filtres, Integer pagina, Integer itemsPerPagina) throws ArxiuException {
        return tracked(() -> super.expedientConsulta(filtres, pagina, itemsPerPagina));
    }

    @Override
    public String expedientTancar(String identificador) throws ArxiuException {
        return tracked(() -> super.expedientTancar(identificador));
    }

    @Override
    public void expedientEsborrar(String identificador) throws ArxiuException {
        tracked(() -> { super.expedientEsborrar(identificador); return null; });
    }

    @Override
    public ContingutArxiu documentCrear(Document document, String identificadorPare) throws ArxiuException {
        return tracked(() -> super.documentCrear(document, identificadorPare));
    }

    @Override
    public Document documentDetalls(String identificador, String versio, boolean ambContingut) throws ArxiuException {
        return tracked(() -> {
            try {
                Document response = new Document();
                if (ambContingut) {
                    try {
                        response.setContingut(documentImprimible(identificador));
                    } catch (Exception ex) {
                        logger.info("No s'ha pogut obtenir la versio imprimible. Obtenint versio original del document uuid " + identificador);
                        response.setContingut(documentOriginal(identificador));
                    }
                }
                try {
                    var result = documentMetadades(identificador);
                    response.setMetadades(toDocumentMetadades(result));
                    response.setFirmes(result.containsKey("eni:tipoFirma") ? Collections.singletonList(new Firma()) : null);
                } catch(Exception e) {
                    logger.debug(ERROR_METADADES + identificador);
                    response.setMetadades(null);
                }
                return response;
            } catch (Exception var12) {
                throw new ArxiuException("S'ha produit un error obtenent els detalls del document: " + identificador, var12);
            }
        });
    }

//    @Override
//    public DocumentContingut documentImprimible(String identificador) throws ArxiuException {
//        return tracked(() -> {
//            try {
//                return getDocumentContingut(identificador);
//            } catch (Exception ex) {
//                logger.debug("S'ha produit un error generant la versió imprimible del document amb UUID " + identificador, ex);
//                throw new ArxiuException("S'ha produit un error generant la versió imprimible del document amb UUID " + identificador, ex);
//            }
//        });
//    }
//
//    /*
//     * La URL de consulta és la següent:
//     *   https://intranet.caib.es/concsv/rest/printable/uuid/IDENTIFICADOR?metadata1=METADADA_1&metadata2=METADADA_2&watermark=MARCA_AIGUA
//     * A on:
//     *   - IDENTIFICADOR és el UUID del document a consultar [OBLIGATORI]
//     *   - METADADA_1 és la primera metadada [OPCIONAL]
//     *   - METADADA_2 és la segona metadada [OPCIONAL]
//     *   - MARCA_AIGUA és el text de la marca d'aigua que apareixerà impresa a cada fulla [OPCIONAL]
//     * Només es obligatori informar la HASH, la resta d'elements son opcionals. Si no s'informen metadades s'imprimeix l'hora i dia de la generació del document imprimible.
//     */
//    @Nonnull
//    private DocumentContingut getDocumentContingut(String identificador) throws IOException {
//        InputStream is = generarVersioImprimible(identificador, null, null, null);
//        DocumentContingut contingut = new DocumentContingut();
//        contingut.setArxiuNom("versio_imprimible.pdf");
//        contingut.setTipusMime("application/pdf");
//        contingut.setContingut(IOUtils.toByteArray(is));
//        contingut.setTamany(contingut.getContingut().length);
//        return contingut;
//    }
//
//    private InputStream generarVersioImprimible(String identificador, String metadada1, String metadada2, String marcaAigua) {
//
//        String url = getPropertyConcsvUrl();
//        url += (url.endsWith("/") ? "" : "/") + "printable/uuid/";
//        WebResource webResource;
//        logger.debug("[ARXIU] Generant versio imprimible uuid. identificador " + identificador + " metadada1 " + metadada1 + " metadada2 " + metadada2 + " marcaAigua " + marcaAigua);
//        webResource = getVersioImprimibleClientConcsv().resource(url + (url.endsWith("/") ? "" : "/") + identificador);
//        if (metadada1 != null) {
//            webResource.queryParam("metadata1", metadada1);
//        }
//        if (metadada2 != null) {
//            webResource.queryParam("metadata2", metadada2);
//        }
//        if (marcaAigua != null) {
//            webResource.queryParam("watermark", marcaAigua);
//        }
//        return webResource.get(InputStream.class);
//    }

    private DocumentContingut documentOriginal(String identificador) throws ArxiuException {

        try {
            logger.info("[ConCSV] Recuperant versio original uuid. identificador " + identificador);
            String url = getPropertyConcsvUrl();
            url += (url.endsWith("/") ? "" : "/") + "original/uuid/";
            var webResource = getVersioImprimibleClientConcsv().resource(url + (!url.endsWith("/") ? "/" : "") + identificador.split(":")[1]);
            var is = webResource.get(InputStream.class);
            var contingut = new DocumentContingut();
            contingut.setContingut(IOUtils.toByteArray(is));
            contingut.setTipusMime(getMimeType(contingut.getContingut()));
            contingut.setArxiuNom("versio_original" + getMimeExtension(contingut.getTipusMime()));
            contingut.setTamany(contingut.getContingut().length);
            return contingut;
        } catch (Exception ex) {
            logger.debug("S'ha produit un error generant el document " + identificador, ex);
            throw new ArxiuException("S'ha produit un error recuperant el document original " + identificador, ex);
        }
    }

    private String getMimeExtension(String tipusMime) throws MimeTypeException {
        return MimeTypes.getDefaultMimeTypes().forName(tipusMime).getExtension();
    }

    private String getMimeType(byte[] contingut) {
        try {
            Tika tika = new Tika();
            return tika.detect(contingut);
        } catch (Exception ex) {
            logger.warn("No s'ha pogut detectar el tipus MIME del contingut", ex);
            return "application/octet-stream";
        }
    }

    private Map<String,Object> documentMetadades(String identificador) {
        try {
            String url = getPropertyConcsvUrl();
            url += (url.endsWith("/") ? "" : "/") + "metadata/uuid/";
            logger.info("[ARXIU] Obtinguent metadades uuid del document " + identificador + " url " + url);
            var webResource = getVersioImprimibleClientConcsv().resource(url + identificador);
            var jsonData = webResource.accept(MediaType.APPLICATION_JSON).get(String.class);
            return new ObjectMapper().readValue(jsonData, HashMap.class);
        } catch (Exception ex) {
            var msg = ERROR_METADADES + identificador;
            logger.debug(msg, ex);
            throw new ArxiuException(msg, ex);
        }
    }

    private static DocumentMetadades toDocumentMetadades(Map<String,Object> metadatas) throws ArxiuException {

        DocumentMetadades metadades = new DocumentMetadades();
        for (var entry : metadatas.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            if ("eni:id".equals(entry.getKey())) {
                metadades.setIdentificador(value.toString());
            } else if ("eni:v_nti".equals(entry.getKey())) {
                metadades.setVersioNti(value.toString());
            } else if ("eni:origen".equals(entry.getKey())) {
                metadades.setOrigen(ContingutOrigen.toEnum(String.valueOf(value)));
            } else if ("eni:fecha_inicio".equals(entry.getKey())) {
                metadades.setDataCaptura(parseDateIso8601(value.toString()));
            } else if ("eni:estado_elaboracion".equals(entry.getKey())) {
                metadades.setEstatElaboracio(DocumentEstatElaboracio.toEnum(value.toString()));
            } else if ("eni:tipo_doc_ENI".equals(entry.getKey())) {
                metadades.setTipusDocumental(DocumentTipus.toEnum(value.toString()));
            } else {
                Object val;
                if ("eni:organo".equals(entry.getKey())) {
                    val = value;
                    if (val instanceof List) {
                        metadades.setOrgans((List<String>) value);
                    } else {
                        metadades.setOrgans(Arrays.asList((String) val));
                    }
                } else if ("eni:nombre_formato".equals(entry.getKey())) {
                    metadades.setFormat(DocumentFormat.toEnum(value.toString()));
                } else if ("eni:extension_formato".equals(entry.getKey())) {
                    metadades.setExtensio(DocumentExtensio.toEnum(value.toString()));
                } else {
                    Map<String, Object> metadadesAddicionals = metadades.getMetadadesAddicionals();
                    if (metadadesAddicionals == null) {
                        metadadesAddicionals = new HashMap<>();
                        metadades.setMetadadesAddicionals(metadadesAddicionals);
                    }
                    metadadesAddicionals.put(entry.getKey(), value);
                }
            }
        }
        return metadades;
    }

    private static Date parseDateIso8601(String date) throws ArxiuException {
        if (date == null) {
            return null;
        }
        try {
            return Date.from(OffsetDateTime.parse(date).toInstant());
        } catch (DateTimeParseException e) {
            throw new ArxiuException("No s'ha pogut parsejar el valor de la data (valor=" + date + ")");
        }
    }

    private Client getVersioImprimibleClientConcsv() {

        if (versioImprimibleClient != null) {
            return versioImprimibleClient;
        }
        versioImprimibleClient = Client.create();
        versioImprimibleClient.setConnectTimeout(getPropertyTimeoutConnect());
        versioImprimibleClient.setReadTimeout(getPropertyTimeoutRead());
        String usuari = getPropertyConcsvUsuari();
        String contrasenya = getPropertyConcsvContrasenya();
        if (usuari != null) {
            versioImprimibleClient.addFilter(new HTTPBasicAuthFilter(usuari, contrasenya));
        }
        return versioImprimibleClient;
    }

    private String getPropertyConcsvUrl() {
        return getPluginProperties().getProperty(CONCSV_URL_PROP);
    }

    private String getPropertyConcsvUsuari() {
        return getPluginProperties().getProperty(CONCSV_USUARI_PROP);
    }

    private String getPropertyConcsvContrasenya() {
        return getPluginProperties().getProperty(CONCSV_CONTRASENYA_PROP);
    }

    private int getPropertyTimeoutConnect() {
        var timeout = getPluginProperties().getProperty(TIMEOUT_CONNECT, TIMEOUT_CONNECT);
        return Integer.parseInt(timeout);
    }

    private int getPropertyTimeoutRead() {
        var timeout = getPluginProperties().getProperty(TIMEOUT_READ, TIMEOUT_READ);
        return Integer.parseInt(timeout);
    }

    private static final Logger logger = LoggerFactory.getLogger(ArxiuPluginCaib.class);
}
