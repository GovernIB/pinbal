package es.caib.pinbal.client.procediments;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import es.caib.pinbal.client.comu.ClientBase;
import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.comu.Page;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ProcedimentClient extends ClientBase {

    private static final String BASE_URL_SUFIX = "/interna/";

    public ProcedimentClient(String urlBase, String usuari, String contrasenya, LogLevel logLevel) {
        super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, logLevel);
    }

    public void createProcediment(Procediment procediment) throws UniformInterfaceException, ClientHandlerException, IOException {
        ClientResponse response = restPeticioPost("/procediments", procediment, ClientResponse.class, MediaType.APPLICATION_JSON_TYPE);

        processVoidResponse(response);
    }

    public void updateProcediment(Long procedimentId, Procediment procediment) throws UniformInterfaceException, ClientHandlerException, IOException {
        ClientResponse response = restPeticioPost("/procediments/" + procedimentId, procediment, ClientResponse.class, MediaType.APPLICATION_JSON_TYPE);

        processVoidResponse(response);
    }

    public void enableServeiToProcediment(Long procedimentId, String serveiCodi) throws UniformInterfaceException, ClientHandlerException, IOException {
        ClientResponse response = restPeticioPost("/procediments/" + procedimentId + "/serveis/" + serveiCodi + "/enable", null, ClientResponse.class, MediaType.APPLICATION_JSON_TYPE);

        processVoidResponse(response);
    }

    public Page<Procediment> getProcediments(String entitatCodi, String codi, String nom, String organGestor, int page, int size, String sort)
        throws UniformInterfaceException, ClientHandlerException, IOException {
        
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("entitatCodi", entitatCodi);
        queryParams.put("codi", codi);
        queryParams.put("nom", nom);
        queryParams.put("organGestor", organGestor);
        queryParams.put("page", String.valueOf(page));
        queryParams.put("size", String.valueOf(size));
        queryParams.put("sort", sort);

        ClientResponse response = restPeticioGet("/procediments", queryParams, ClientResponse.class);

        return processPagedResponse(response, Procediment.class, page, size, sort);
    }

    public Procediment getProcediment(Long procedimentId) throws UniformInterfaceException, ClientHandlerException, IOException {
        ClientResponse response = restPeticioGet("/procediments/" + procedimentId, new HashMap<String, String>(), ClientResponse.class);

        return processResponse(response, Procediment.class);
    }

    public Procediment getProcediment(String procedimentCodi, final String entitatCodi) throws UniformInterfaceException, ClientHandlerException, IOException {
        ClientResponse response = restPeticioGet("/procediments/byCodi/" + procedimentCodi, new HashMap<String, String>() {{ put("entitatCodi", entitatCodi); }}, ClientResponse.class);

        return processResponse(response, Procediment.class);
    }


    private void logTrace(String message) {
        if (logLevel.isTraceEnabled()) log.trace(message);
    }
    private void logDebug(String message) {
        if (logLevel.isDebugEnabled()) log.debug(message);
    }
    private void logInfo(String message) {
        if (logLevel.isInfoEnabled()) log.info(message);
    }
    private void logWarn(String message) {
        if (logLevel.isWarnEnabled()) log.warn(message);
    }
    private void logError(String message) {
        if (logLevel.isErrorEnabled()) log.error(message);
    }
}