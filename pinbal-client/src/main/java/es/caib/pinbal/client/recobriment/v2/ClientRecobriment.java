package es.caib.pinbal.client.recobriment.v2;

import es.caib.pinbal.client.comu.BasicAuthClientBase;
import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.procediments.ProcedimentBasic;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.serveis.ServeiBasic;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientRecobriment extends BasicAuthClientBase {

    private static final String BASE_URL_SUFIX = "/interna/recobriment/v2/";

    public ClientRecobriment(String urlBase, String usuari, String contrasenya) {
        super(urlBase + BASE_URL_SUFIX, usuari, contrasenya);
    }

    public ClientRecobriment(String urlBase, String usuari, String contrasenya, LogLevel logLevel) {
        super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, logLevel);
    }

    public ClientRecobriment(String urlBase, String usuari, String contrasenya,
                             Integer timeoutConnect, Integer timeoutRead) {
        super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, timeoutConnect, timeoutRead);
    }

    public ClientRecobriment(String urlBase, String usuari, String contrasenya,
                             Integer timeoutConnect, Integer timeoutRead, LogLevel logLevel) {
        super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, timeoutConnect, timeoutRead, logLevel);
    }

    // Obtencio d'entitats
    // /////////////////////////////////////////////////////////////

    public List<Entitat> getEntitats() throws IOException {
        Response response = restPeticioGet("entitats", null);
        return processListResponse(response, Entitat.class);
    }

    // Obtencio de procediments
    // /////////////////////////////////////////////////////////////

    public List<ProcedimentBasic> getProcediments(String entitatCodi) throws IOException {
        Response response = restPeticioGet("entitats/" + entitatCodi + "/procediments", null);
        return processListResponse(response, ProcedimentBasic.class);
    }

    // Obtenció de serveis
    // /////////////////////////////////////////////////////////////

    public List<ServeiBasic> getServeis() throws IOException {
        Response response = restPeticioGet("serveis", null);
        return processListResponse(response, ServeiBasic.class);
    }

    public List<ServeiBasic> getServeisPerEntitat(String entitatCodi) throws IOException {
        Response response = restPeticioGet("entitats/" + entitatCodi + "/serveis", null);
        return processListResponse(response, ServeiBasic.class);
    }

    public List<ServeiBasic> getServeisPerProcediment(String entitatCodi, String procedimentCodi) throws IOException {
        Response response = restPeticioGet("entitats/" + entitatCodi + "/procediments/" + procedimentCodi + "/serveis", null);
        return processListResponse(response, ServeiBasic.class);
    }

    // Obtenció de dades específiques
    // /////////////////////////////////////////////////////////////

    public List<DadaEspecifica> getDadesEspecifiques(String serveiCodi) throws IOException {
        Response response = restPeticioGet("serveis/" + serveiCodi + "/dadesEspecifiques", null);
        return processListResponse(response, DadaEspecifica.class);
    }

    public List<ValorEnum> getValorsEnum(String serveiCodi, String campCodi, String enumCodi, String filtre) throws IOException {
        Map<String, String> params = null;
        if (filtre != null) {
            params = new HashMap<>();
            params.put("filtre", filtre);
        }
        Response response = restPeticioGet("serveis/" + serveiCodi + "/camps/" + campCodi + "/enumerat/" + enumCodi, params);
        return processListResponse(response, ValorEnum.class);
    }

    // Realització de consultes
    // /////////////////////////////////////////////////////////////

    public PeticioRespostaSincrona peticioSincrona(String serveiCodi, PeticioSincrona peticio) throws IOException {
        Response response = restPeticioPost("serveis/" + serveiCodi + "/peticioSincrona", peticio, MediaType.APPLICATION_JSON_TYPE);
        return processResponse(response, PeticioRespostaSincrona.class);
    }

    public PeticioConfirmacioAsincrona peticioAsincrona(String serveiCodi, PeticioAsincrona peticio) throws IOException {
        Response response = restPeticioPost("serveis/" + serveiCodi + "/peticioAsincrona", peticio, MediaType.APPLICATION_JSON_TYPE);
        return processResponse(response, PeticioConfirmacioAsincrona.class);
    }

    // Obtenció de respostes
    // /////////////////////////////////////////////////////////////

    public PeticioRespostaAsincrona getResposta(String idPeticio) throws IOException {
        Response response = restPeticioGet("consultes/" + idPeticio + "/resposta", null);
        return processResponse(response, PeticioRespostaAsincrona.class);
    }

    public ScspJustificante getJustificant(String idPeticio, String idSolicitud) throws IOException {
        Response response = restPeticioGet("consultes/" + idPeticio + "/solicitud/" + idSolicitud + "/justificant", null);
        return processResponse(response, ScspJustificante.class);
    }

    public ScspJustificante getJustificantImprimible(String idPeticio, String idSolicitud) throws IOException {
        Response response = restPeticioGet("consultes/" + idPeticio + "/solicitud/" + idSolicitud + "/justificantImprimible", null);
        return processResponse(response, ScspJustificante.class);
    }

    public String getJustificantCsv(String idPeticio, String idSolicitud) throws IOException {
        Response response = restPeticioGet("consultes/" + idPeticio + "/solicitud/" + idSolicitud + "/justificantCsv", null);
        return processResponse(response, String.class);
    }

    public String getJustificantUuid(String idPeticio, String idSolicitud) throws IOException {
        Response response = restPeticioGet("consultes/" + idPeticio + "/solicitud/" + idSolicitud + "/justificantUuid", null);
        return processResponse(response, String.class);
    }

}
