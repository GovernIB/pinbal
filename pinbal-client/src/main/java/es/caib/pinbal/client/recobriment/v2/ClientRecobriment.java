package es.caib.pinbal.client.recobriment.v2;

import com.sun.jersey.api.client.ClientResponse;
import es.caib.pinbal.client.comu.BasicAuthClientBase;
import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.procediments.ProcedimentBasic;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.serveis.ServeiBasic;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientRecobriment extends BasicAuthClientBase {

    private static final String BASE_URL_SUFIX = "/interna/recobriment/v2/";


    protected ClientRecobriment(
            String urlBase,
            String usuari,
            String contrasenya) {
        super(urlBase + BASE_URL_SUFIX, usuari, contrasenya);
    }

    public ClientRecobriment(
            String urlBase,
            String usuari,
            String contrasenya,
            LogLevel logLevel) {
        super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, logLevel);
    }

    public ClientRecobriment(
            String urlBase,
            String usuari,
            String contrasenya,
            Integer timeoutConnect,
            Integer timeoutRead) {
        super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, timeoutConnect, timeoutRead);
    }

    public ClientRecobriment(
            String urlBase,
            String usuari,
            String contrasenya,
            Integer timeoutConnect,
            Integer timeoutRead,
            LogLevel logLevel) {
        super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, timeoutConnect, timeoutRead, logLevel);
    }


    // Obtencio d'entitats
    // /////////////////////////////////////////////////////////////

    /**
     * Obtén les entitat accessibles
     * @return llista d'entitats a les que l'usuari autenticat té permís
     */
    List<Entitat> getEntitats() throws IOException {
        ClientResponse response = restPeticioGet("entitats", null, ClientResponse.class);
        return processListResponse(response, Entitat.class);
    }

    // Obtencio de procediments
    // /////////////////////////////////////////////////////////////

    /**
     * Obtén els procediments d'una entitat
     * @param entitatCodi
     * @return llista de procediments disponibles per l'entitat especificada a 'entitatCodi'
     */
    List<ProcedimentBasic> getProcediments(String entitatCodi) throws IOException {
        ClientResponse response = restPeticioGet("entitats/" + entitatCodi + "/procediments", null, ClientResponse.class);
        return processListResponse(response, ProcedimentBasic.class);
    }

    // Obtenció de serveis
    // /////////////////////////////////////////////////////////////

    /**
     * Obtén tots els serveis de Pinbal
     * @return llista de serveis disponibles a Pinbal
     */
    List<ServeiBasic> getServeis() throws IOException {
        ClientResponse response = restPeticioGet("serveis", null, ClientResponse.class);
        return processListResponse(response, ServeiBasic.class);
    }

    /**
     * Obtén tots els serveis de Pinbal per entitat
     * @param entitatCodi Codi de l'entitat
     * @return llista dels serveis disponibles a Pinbal per una entitat
     */
    List<ServeiBasic> getServeisPerEntitat(String entitatCodi) throws IOException {
        ClientResponse response = restPeticioGet("entitats/" + entitatCodi + "/serveis", null, ClientResponse.class);
        return processListResponse(response, ServeiBasic.class);
    }

    /**
     * Obtén tots els serveis de Pinbal per procediment
     * @param procedimentCodi Codi del procediment
     * @return llista de serveis disponibles a Pinbal per un procediment
     */
    List<ServeiBasic> getServeisPerProcediment(String procedimentCodi) throws IOException {
        ClientResponse response = restPeticioGet("procediments/" + procedimentCodi + "/serveis", null, ClientResponse.class);
        return processListResponse(response, ServeiBasic.class);
    }

    // Obtenció de dades específiques
    // /////////////////////////////////////////////////////////////

    /**
     * Obtén les dades específiques d'un servei
     * @param serveiCodi Codi del servei
     * @return lista de camps que son necessaris per emplenar l’apartat de dades específiques de la petició SCSP al servei web final
     */
    List<DadaEspecifica> getDadesEspecifiques(String serveiCodi) throws IOException {
        ClientResponse response = restPeticioGet("serveis/" + serveiCodi + "/dadesEspecifiques", null, ClientResponse.class);
        return processListResponse(response, DadaEspecifica.class);
    }

    /**
     * Obtén tots els valors d'un camp de tipus enumerat
     * @param serveiCodi Codi del servei
     * @param campCodi Codi del camp
     * @param enumCodi Codi de l'enumerat
     * @param filtre Filtre a aplicar en l’obtenció dels possibles valors de l’enumerat (opcional)
     * @return llistes de valors, siguin de enumerats o de valors de dades externes.
     * Se li passa el codi enum que s’obté de la cridada anterior quant el camp és de tipus enumerat.
     * També se li passa opcionalment un filtre a aplicar que pot tenir varis comportaments segons el enumerat
     */
    List<ValorEnum> getValorsEnum(
            String serveiCodi,
            String campCodi,
            String enumCodi,
            String filtre) throws IOException {
        Map<String, String> params = null;
        if (filtre != null) {
            params = new HashMap<>();
            params.put("filtre", filtre);
        }
//        ClientResponse response = restPeticioGet("serveis/" + serveiCodi + "/camps/" + URLEncoder.encode(campCodi) + "/enumerat/" + enumCodi, params, ClientResponse.class);
        ClientResponse response = restPeticioGet("serveis/" + serveiCodi + "/camps/" + campCodi + "/enumerat/" + enumCodi, params, ClientResponse.class);
        return processListResponse(response, ValorEnum.class);
    }

    // Realització de consultes
    // /////////////////////////////////////////////////////////////

    /**
     * Realització una consulta síncrona
     * @param serveiCodi Codi del servei
     * @param peticio Petició síncrona
     * @return informació dels possilbes errors de validació de les dades o en la consulta, i una entitat de tipus ScspRespuesta que conté la resposta a la consulta en cas d'havers-se realitzat correctament
     */
    PeticioRespostaSincrona peticioSincrona(String serveiCodi, PeticioSincrona peticio) throws IOException {
        ClientResponse response = restPeticioPost("serveis/" + serveiCodi + "/peticioSincrona", peticio, ClientResponse.class, MediaType.APPLICATION_JSON_TYPE);
        return processResponse(response, PeticioRespostaSincrona.class);
    }

    /**
     * Realització una consulta asíncrona
     * @param serveiCodi Codi del servei
     * @param peticio Petició asíncrona
     * @return informació dels possilbes errors de validació de les dades o en la consulta, i una entitat de tipus ScspConfirmacionPeticion que conté informació de la resposta en cas d'havers-se realitzat correctament
     */
    PeticioConfirmacioAsincrona peticioAsincrona(String serveiCodi, PeticioAsincrona peticio) throws IOException {
        ClientResponse response = restPeticioPost("serveis/" + serveiCodi + "/peticioAsincrona", peticio, ClientResponse.class, MediaType.APPLICATION_JSON_TYPE);
        return processResponse(response, PeticioConfirmacioAsincrona.class);
    }

    // Obtenció de respostes
    // /////////////////////////////////////////////////////////////

    /**
     * Obtenció del resultat d'una petició ja realitzada
     * @param idPeticio Identificador de la petició
     * @return informació de la resposta de la petició
     */
    PeticioRespostaAsincrona getResposta(String idPeticio) throws IOException {
        ClientResponse response = restPeticioGet("consultes/" + idPeticio + "/resposta", null, ClientResponse.class);
        return processResponse(response, PeticioRespostaAsincrona.class);
    }

    /**
     * Obtenció del justificant
     * @param idPeticio Identificador de la petició
     * @param idSolicitud Identificador de la sol·licitud
     * @return justificant de la petició
     * @throws Exception
     */
    ScspJustificante getJustificant(String idPeticio, String idSolicitud) throws Exception {
        ClientResponse response = restPeticioGet("consultes/" + idPeticio + "/solicitud/" + idSolicitud + "/justificant", null, ClientResponse.class);
        return processResponse(response, ScspJustificante.class);
    }

    /**
     * Obtenció de la versió imprimible del justificant
     * @param idPeticio Identificador de la petició
     * @param idSolicitud Identificador de la sol·licitud
     * @return versió imprimible del justificant de la petició
     * @throws Exception
     */
    ScspJustificante getJustificantImprimible(String idPeticio, String idSolicitud) throws Exception {
        ClientResponse response = restPeticioGet("consultes/" + idPeticio + "/solicitud/" + idSolicitud + "/justificantImprimible", null, ClientResponse.class);
        return processResponse(response, ScspJustificante.class);
    }

    /**
     * Obtenció del CSV del justificant
     * @param idPeticio Identificador de la petició
     * @param idSolicitud Identificador de la sol·licitud
     * @return codi CSV del justificant de la petició a l'arxiu
     * @throws Exception
     */
    String getJustificantCsv(String idPeticio, String idSolicitud) throws Exception {
        ClientResponse response = restPeticioGet("consultes/" + idPeticio + "/solicitud/" + idSolicitud + "/justificantCsv", null, ClientResponse.class);
        return processResponse(response, String.class);
    }

    /**
     * Obtenció del UUID del justificant
     * @param idPeticio Identificador de la petició
     * @param idSolicitud Identificador de la sol·licitud
     * @return codi UUID del justificant de la petició a l'arxiu
     * @throws Exception
     */
    String getJustificantUuid(String idPeticio, String idSolicitud) throws Exception {
        ClientResponse response = restPeticioGet("consultes/" + idPeticio + "/solicitud/" + idSolicitud + "/justificantUuid", null, ClientResponse.class);
        return processResponse(response, String.class);
    }

}
