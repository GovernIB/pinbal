/**
 * 
 */
package es.caib.pinbal.plugin.caib.unitat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import es.caib.dir3caib.ws.api.unidad.Dir3CaibObtenerUnidadesWs;
import es.caib.dir3caib.ws.api.unidad.Dir3CaibObtenerUnidadesWsService;
import es.caib.dir3caib.ws.api.unidad.UnidadTF;
import es.caib.pinbal.plugin.unitat.NodeDir3;
import es.caib.pinbal.plugin.unitat.UnitatOrganitzativa;
import es.caib.pinbal.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.pinbal.plugins.SistemaExternException;
import es.caib.pinbal.plugins.caib.PropertiesHelper;

/**
 * Implementació de proves del plugin d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatsOrganitzativesPluginDir3 implements UnitatsOrganitzativesPlugin {

    private static final String SERVEI_ORGANIGRAMA = "/rest/organigrama/";
    
    private static final String PROPERTY_SERVICE_URL = "es.caib.pinbal.plugin.unitats.organitzatives.dir3.service.url";
    private static final String PROPERTY_SERVICE_USERNAME = "es.caib.pinbal.plugin.unitats.organitzatives.dir3.service.username";
    private static final String PROPERTY_SERVICE_PASSWORD = "es.caib.pinbal.plugin.unitats.organitzatives.dir3.service.password";
    private static final String PROPERTY_SERVICE_TIMEOUT = "es.caib.ripea.plugin.unitats.organitzatives.dir3.service.timeout";
    private static final String PROPERTY_SERVICE_LOG_ACTIU = "es.caib.pinbal.plugin.unitats.organitzatives.dir3.service.log.actiu";
    private static final String PROPERTY_CERCA_URL = "es.caib.pinbal.plugin.unitats.cerca.dir3.service.url";
    private static final String PROPERTY_CONSULTA_URL = "es.caib.pinbal.plugin.unitats.organitzatives.dir3.consulta.rest.service.url";

    public Map<String, NodeDir3> organigrama(String codi) throws SistemaExternException {
        Map<String, NodeDir3> organigrama = new HashMap<String, NodeDir3>();
        try {
            URL url = new URL(getServiceUrl() + SERVEI_ORGANIGRAMA + "?codigo=" + codi);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            byte[] response = IOUtils.toByteArray(httpConnection.getInputStream());
            if (response != null && response.length > 0) {
                NodeDir3 arrel = mapper.readValue(response, NodeDir3.class);
                nodeToOrganigrama(arrel, organigrama);
            }
            return organigrama;
        } catch (Exception ex) {
            throw new SistemaExternException(
                    "No s'ha pogut consultar l'organigrama de unitats organitzatives via REST ("
                            + "codiEntitat=" + codi + ")",
                    ex);
        }
    }

    @Override
    public List<UnitatOrganitzativa> findAmbPare(String pareCodi) throws SistemaExternException {
        try {
            UnidadTF unidadPare = getObtenerUnidadesService().obtenerUnidad(pareCodi, null, null);
            if (unidadPare != null) {
                List<UnitatOrganitzativa> unitats = new ArrayList<UnitatOrganitzativa>();
                List<UnidadTF> unidades = getObtenerUnidadesService().obtenerArbolUnidades(pareCodi, null,
                        null);// df.format(new
                // Date()));
                if (unidades != null) {
                    unidades.add(0, unidadPare);
                    for (UnidadTF unidad : unidades) {
                        if ("V".equalsIgnoreCase(unidad.getCodigoEstadoEntidad())) {
                            unitats.add(toUnitatOrganitzativa(unidad));
                        }
                    }
                } else {
                    unitats.add(toUnitatOrganitzativa(unidadPare));
                }
                return unitats;
            } else {
                throw new SistemaExternException(
                        "No s'han trobat la unitat pare (pareCodi=" + pareCodi + ")");
            }
        } catch (Exception ex) {
            throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via WS ("
                    + "pareCodi=" + pareCodi + ")", ex);
        }
    }

    @Override
    public UnitatOrganitzativa findAmbCodi(String codi) throws SistemaExternException {
        try {
            UnitatOrganitzativa unitat = null;
            UnidadTF unidad = getObtenerUnidadesService().obtenerUnidad(codi, null, null);
            if (unidad != null && "V".equalsIgnoreCase(unidad.getCodigoEstadoEntidad())) {
                unitat = toUnitatOrganitzativa(unidad);
            } else {
                throw new SistemaExternException(
                        "La unitat organitzativa no està vigent (" + "codi=" + codi + ")");
            }
            return unitat;
        } catch (SistemaExternException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new SistemaExternException(
                    "No s'ha pogut consultar la unitat organitzativa (" + "codi=" + codi + ")", ex);
        }
    }

    public List<UnitatOrganitzativa> cercaUnitats(String codi, String denominacio, Long nivellAdministracio,
                                                  Long comunitatAutonoma, Boolean ambOficines,
                                                  Boolean esUnitatArrel, Long provincia,
                                                  String municipi) throws SistemaExternException {
        List<UnitatOrganitzativa> unitats = new ArrayList<UnitatOrganitzativa>();
        try {
            URL url = new URL(getServiceCercaUrl() + "?codigo=" + codi + "&denominacion=" + denominacio
                    + "&codNivelAdministracion=" + (nivellAdministracio != null ? nivellAdministracio : "-1")
                    + "&codComunidadAutonoma=" + (comunitatAutonoma != null ? comunitatAutonoma : "-1")
                    + "&conOficinas=" + (ambOficines != null && ambOficines ? "true" : "false")
                    + "&unidadRaiz=" + (esUnitatArrel != null && esUnitatArrel ? "true" : "false")
                    + "&provincia=" + (provincia != null ? provincia : "-1") + "&localidad="
                    + (municipi != null ? municipi : "-1") + "&vigentes=true");
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            unitats = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance()
                    .constructCollectionType(List.class, UnitatOrganitzativa.class));
            Collections.sort(unitats);
            return unitats;
        } catch (JsonMappingException e) {
            // No results
        } catch (Exception ex) {
            throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via REST ("
                    + "codi=" + codi + ", " + "denominacio=" + denominacio + ", " + "nivellAdministracio="
                    + nivellAdministracio + ", " + "comunitatAutonoma=" + comunitatAutonoma + ", "
                    + "ambOficines=" + ambOficines + ", " + "esUnitatArrel=" + esUnitatArrel + ", "
                    + "provincia=" + provincia + ", " + "municipi=" + municipi + ")", ex);
        }
        return unitats;
    }

    private Dir3CaibObtenerUnidadesWs getObtenerUnidadesService() throws MalformedURLException {
        Dir3CaibObtenerUnidadesWs client = null;
        URL url = new URL(getServiceUrl() + "?wsdl");
        Dir3CaibObtenerUnidadesWsService service = new Dir3CaibObtenerUnidadesWsService(url,
                new QName("http://unidad.ws.dir3caib.caib.es/", "Dir3CaibObtenerUnidadesWsService"));
        client = service.getDir3CaibObtenerUnidadesWs();
        BindingProvider bp = (BindingProvider) client;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getServiceUrl());
        String username = getServiceUsername();
        if (username != null && !username.isEmpty()) {
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, getServicePassword());
        }
        if (isLogMissatgesActiu()) {
            @SuppressWarnings("rawtypes")
            List<Handler> handlerChain = new ArrayList<Handler>();
            handlerChain.add(new LogMessageHandler());
            bp.getBinding().setHandlerChain(handlerChain);
        }
        Integer connectTimeout = getServiceTimeout();
        if (connectTimeout != null) {
            bp.getRequestContext().put("org.jboss.ws.timeout", connectTimeout);
        }
        return client;
    }

    private UnitatOrganitzativa toUnitatOrganitzativa(UnidadTF unidad) {
        UnitatOrganitzativa unitat = new UnitatOrganitzativa(unidad.getCodigo(), unidad.getDenominacion(),
                unidad.getCodigo(), // CifNif
                unidad.getFechaAltaOficial(), unidad.getCodigoEstadoEntidad(), unidad.getCodUnidadSuperior(),
                unidad.getCodUnidadRaiz(), unidad.getCodigoAmbPais(), unidad.getCodAmbComunidad(),
                unidad.getCodAmbProvincia(), unidad.getCodPostal(), unidad.getDescripcionLocalidad(),
                unidad.getCodigoTipoVia(), unidad.getNombreVia(), unidad.getNumVia());
        return unitat;
    }

    private class LogMessageHandler implements SOAPHandler<SOAPMessageContext> {
        public boolean handleMessage(SOAPMessageContext messageContext) {
            log(messageContext);
            return true;
        }

        public Set<QName> getHeaders() {
            return Collections.emptySet();
        }

        public boolean handleFault(SOAPMessageContext messageContext) {
            log(messageContext);
            return true;
        }

        public void close(MessageContext context) {
        }

        private void log(SOAPMessageContext messageContext) {
            SOAPMessage msg = messageContext.getMessage();
            try {
                Boolean outboundProperty = (Boolean) messageContext
                        .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                if (outboundProperty)
                    System.out.print("Missatge SOAP petició: ");
                else
                    System.out.print("Missatge SOAP resposta: ");
                msg.writeTo(System.out);
                System.out.println();
            } catch (SOAPException ex) {
                Logger.getLogger(LogMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(LogMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String getServiceUrl() {
        return PropertiesHelper.getProperties()
                .getProperty(PROPERTY_SERVICE_URL);
    }

    private String getServiceUsername() {
        return PropertiesHelper.getProperties()
                .getProperty(PROPERTY_SERVICE_USERNAME);
    }

    private String getServicePassword() {
        return PropertiesHelper.getProperties()
                .getProperty(PROPERTY_SERVICE_PASSWORD);
    }

    private boolean isLogMissatgesActiu() {
        return PropertiesHelper.getProperties()
                .getAsBoolean(PROPERTY_SERVICE_LOG_ACTIU);
    }

    private Integer getServiceTimeout() {
        String key = PROPERTY_SERVICE_TIMEOUT;
        if (PropertiesHelper.getProperties().getProperty(key) != null)
            return PropertiesHelper.getProperties().getAsInt(key);
        else
            return null;
    }

    private String getServiceCercaUrl() {
        String serviceUrl = PropertiesHelper.getProperties()
                .getProperty(PROPERTY_CONSULTA_URL);
        if (serviceUrl == null) {
            serviceUrl = PropertiesHelper.getProperties()
                    .getProperty(PROPERTY_CERCA_URL);
        }
        return serviceUrl;
    }

    private void nodeToOrganigrama(NodeDir3 unitat, Map<String, NodeDir3> organigrama) {
        organigrama.put(unitat.getCodi(), unitat);
        if (unitat.getFills() != null)
            for (NodeDir3 fill : unitat.getFills())
                nodeToOrganigrama(fill, organigrama);
    }
}
