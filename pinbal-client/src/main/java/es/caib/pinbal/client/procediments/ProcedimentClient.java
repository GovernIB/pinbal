package es.caib.pinbal.client.procediments;

import es.caib.pinbal.client.comu.ClientBase;
import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.comu.Page;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ProcedimentClient extends ClientBase {

    private static final String BASE_URL_SUFIX = "/interna/";

    public ProcedimentClient(String urlBase, String usuari, String contrasenya, LogLevel logLevel) {
        super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, logLevel);
    }

    public void createProcediment(Procediment procediment) throws IOException {
        Response response = restPeticioPost("/procediments", procediment, MediaType.APPLICATION_JSON_TYPE);

        processVoidResponse(response);
    }

    public void updateProcediment(Long procedimentId, Procediment procediment) throws IOException {
        Response response = restPeticioPost("/procediments/" + procedimentId, procediment, MediaType.APPLICATION_JSON_TYPE);

        processVoidResponse(response);
    }

    public void enableServeiToProcediment(Long procedimentId, String serveiCodi) throws IOException {
        Response response = restPeticioPost("/procediments/" + procedimentId + "/serveis/" + serveiCodi + "/enable", null, MediaType.APPLICATION_JSON_TYPE);

        processVoidResponse(response);
    }

    public Page<Procediment> getProcediments(String entitatCodi, String codi, String nom, String organGestor, int page, int size, String sort) throws IOException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("entitatCodi", entitatCodi);
        queryParams.put("codi", codi);
        queryParams.put("nom", nom);
        queryParams.put("organGestor", organGestor);
        queryParams.put("page", String.valueOf(page));
        queryParams.put("size", String.valueOf(size));
        queryParams.put("sort", sort);

        Response response = restPeticioGet("/procediments", queryParams);

        return processPagedResponse(response, Procediment.class, page, size, sort);
    }

    public Procediment getProcediment(Long procedimentId) throws IOException {
        Response response = restPeticioGet("/procediments/" + procedimentId, new HashMap<>());

        return processResponse(response, Procediment.class);
    }

    public Procediment getProcediment(String procedimentCodi, String entitatCodi) throws IOException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("entitatCodi", entitatCodi);
        Response response = restPeticioGet("/procediments/byCodi/" + procedimentCodi, queryParams);

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
