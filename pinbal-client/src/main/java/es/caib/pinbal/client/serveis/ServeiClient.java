package es.caib.pinbal.client.serveis;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import es.caib.pinbal.client.comu.ClientBase;
import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.comu.Page;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ServeiClient extends ClientBase {

    public ServeiClient(String urlBase, String usuari, String contrasenya, LogLevel logLevel) {
        super(urlBase, usuari, contrasenya, logLevel);
    }

    public Page<Servei> getServeis(String codi, String descripcio, int page, int size, String sort)
            throws UniformInterfaceException, ClientHandlerException, IOException {

        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("codi", codi);
        queryParams.put("descripcio", descripcio);
        queryParams.put("page", String.valueOf(page));
        queryParams.put("size", String.valueOf(size));
        queryParams.put("sort", sort);

        ClientResponse response = restPeticioGet("/serveis", queryParams, ClientResponse.class);

        return processPagedResponse(response, Servei.class, page, size, sort);
    }

    public Servei getServei(String serveiCodi) throws UniformInterfaceException, ClientHandlerException, IOException {
        ClientResponse response = restPeticioGet("/serveis/" + serveiCodi, new HashMap<String, String>(), ClientResponse.class);

        return processResponse(response, Servei.class);
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