package es.caib.pinbal.client.usuaris;

import es.caib.pinbal.client.comu.ClientBase;
import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.comu.Page;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    public void createOrUpdateUsuari(UsuariEntitat usuariEntitat) throws IOException {
        Response response = restPeticioPost("/usuaris", usuariEntitat, MediaType.APPLICATION_JSON_TYPE);

        processResponse(response, UsuariEntitat.class);
    }

    public Page<UsuariEntitat> getUsuaris(String entitatCodi, FiltreUsuaris filtreUsuaris, int page, int size, String sort) throws IOException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("entitatCodi", entitatCodi);
        if (filtreUsuaris != null)
            queryParams.put("filtreUsuaris", URLEncoder.encode(mapper.writeValueAsString(filtreUsuaris), "UTF-8"));
        queryParams.put("page", String.valueOf(page));
        queryParams.put("size", String.valueOf(size));
        queryParams.put("sort", sort);

        Response response = restPeticioGet("/usuaris", queryParams);

        return processPagedResponse(response, UsuariEntitat.class, page, size, sort);
    }

    public UsuariEntitat getUsuari(String usuariCodi, String entitatCodi) throws IOException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("entitatCodi", entitatCodi);

        Response response = restPeticioGet("/usuaris/" + usuariCodi, queryParams);

        return processResponse(response, UsuariEntitat.class);
    }

    public void grantPermissions(String usuariCodi, PermisosServei permisosServei) throws IOException {
        Response response = restPeticioPost("/usuaris/" + usuariCodi + "/permisos", permisosServei, MediaType.APPLICATION_JSON_TYPE);

        processResponse(response, PermisosServei.class);
    }

    public PermisosServei getUserPermissions(String usuariCodi, String entitatCodi) throws IOException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("entitatCodi", entitatCodi);

        Response response = restPeticioGet("/usuaris/" + usuariCodi + "/permisos", queryParams);

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
