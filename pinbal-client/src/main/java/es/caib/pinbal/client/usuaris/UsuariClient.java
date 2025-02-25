package es.caib.pinbal.client.usuaris;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import es.caib.pinbal.client.comu.ClientBase;
import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.comu.Page;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UsuariClient extends ClientBase {

    private static final String BASE_URL_SUFIX = "/interna/";

    public UsuariClient(String urlBase, String usuari, String contrasenya, LogLevel logLevel) {
        super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, logLevel);
    }

    public void createOrUpdateUsuari(UsuariEntitat usuariEntitat) throws UniformInterfaceException, ClientHandlerException, IOException {
        ClientResponse response = restPeticioPost("/usuaris", usuariEntitat, ClientResponse.class, MediaType.APPLICATION_JSON_TYPE);

        processResponse(response, UsuariEntitat.class);
    }


    public Page<UsuariEntitat> getUsuaris(String entitatCodi, FiltreUsuaris filtreUsuaris, int page, int size, String sort)
        throws UniformInterfaceException, ClientHandlerException, IOException {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("entitatCodi", entitatCodi);
        if (filtreUsuaris != null)
            queryParams.put("filtreUsuaris", URLEncoder.encode(mapper.writeValueAsString(filtreUsuaris), "UTF-8"));
        queryParams.put("page", String.valueOf(page));
        queryParams.put("size", String.valueOf(size));
        queryParams.put("sort", sort);

        ClientResponse response = restPeticioGet("/usuaris", queryParams, ClientResponse.class);

        return processPagedResponse(response, UsuariEntitat.class, page, size, sort);
    }

    public UsuariEntitat getUsuari(String usuariCodi, String entitatCodi) throws UniformInterfaceException, ClientHandlerException, IOException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("entitatCodi", entitatCodi);

        ClientResponse response = restPeticioGet("/usuaris/" + usuariCodi, queryParams, ClientResponse.class);

        return processResponse(response, UsuariEntitat.class);
    }


    public void grantPermissions(String usuariCodi, PermisosServei permisosServei) 
        throws UniformInterfaceException, ClientHandlerException, IOException {
        ClientResponse response = restPeticioPost("/usuaris/" + usuariCodi + "/permisos", permisosServei, ClientResponse.class, MediaType.APPLICATION_JSON_TYPE);

        processResponse(response, PermisosServei.class);
    }


    public PermisosServei getUserPermissions(String usuariCodi, String entitatCodi) throws UniformInterfaceException, ClientHandlerException, IOException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("entitatCodi", entitatCodi);

        ClientResponse response = restPeticioGet("/usuaris/" + usuariCodi + "/permisos", queryParams, ClientResponse.class);

        return processResponse(response, PermisosServei.class);
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