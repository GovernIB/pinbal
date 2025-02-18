package es.caib.pinbal.client.recobriment.v2;

import es.caib.pinbal.client.comu.BasicAuthClientBase;
import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.serveis.Servei;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URLEncoder;
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
        return restPeticioGetList("entitats", null, Entitat.class);
    }

    // Obtencio de procediments
    // /////////////////////////////////////////////////////////////

    /**
     * Obtén els procediments d'una entitat
     * @param entitatCodi
     * @return llista de procediments disponibles per l'entitat especificada a 'entitatCodi'
     */
    List<Procediment> getProcediments(String entitatCodi) throws IOException {
        return restPeticioGetList("entitats/" + entitatCodi + "/procediments", null, Procediment.class);
    }

    // Obtenció de serveis
    // /////////////////////////////////////////////////////////////

    /**
     * Obtén tots els serveis de Pinbal
     * @return llista de serveis disponibles a Pinbal
     */
    List<Servei> getServeis() throws IOException {
        return restPeticioGetList("serveis", null, Servei.class);
    }

    /**
     * Obtén tots els serveis de Pinbal per entitat
     * @param entitatCodi Codi de l'entitat
     * @return llista dels serveis disponibles a Pinbal per una entitat
     */
    List<Servei> getServeisPerEntitat(String entitatCodi) throws IOException {
        return restPeticioGetList("entitats/" + entitatCodi + "/serveis", null, Servei.class);
    }

    /**
     * Obtén tots els serveis de Pinbal per procediment
     * @param procedimentCodi Codi del procediment
     * @return llista de serveis disponibles a Pinbal per un procediment
     */
    List<Servei> getServeisPerProcediment(String procedimentCodi) throws IOException {
        return restPeticioGetList("procediments/" + procedimentCodi + "/serveis", null, Servei.class);
    }

    // Obtenció de dades específiques
    // /////////////////////////////////////////////////////////////

    /**
     * Obtén les dades específiques d'un servei
     * @param serveiCodi Codi del servei
     * @return lista de camps que son necessaris per emplenar l’apartat de dades específiques de la petició SCSP al servei web final
     */
    List<DadaEspecifica> getDadesEspecifiques(String serveiCodi) throws IOException {
        return restPeticioGetList("serveis/" + serveiCodi + "/dadesEspecifiques", null, DadaEspecifica.class);
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
        return restPeticioGetList("serveis/" + serveiCodi + "/camps/" + URLEncoder.encode(campCodi) + "/enumerat/" + enumCodi, params, ValorEnum.class);
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
        return restPeticioPost("serveis/" + serveiCodi + "/peticioSincrona", peticio, PeticioRespostaSincrona.class, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Realització una consulta asíncrona
     * @param serveiCodi Codi del servei
     * @param peticio Petició asíncrona
     * @return informació dels possilbes errors de validació de les dades o en la consulta, i una entitat de tipus ScspConfirmacionPeticion que conté informació de la resposta en cas d'havers-se realitzat correctament
     */
    PeticioRespostaAsincrona peticioAsincrona(String serveiCodi, PeticioAsincrona peticio) throws IOException {
        return restPeticioPost("serveis/" + serveiCodi + "/peticioAsincrona", peticio, PeticioRespostaAsincrona.class, MediaType.APPLICATION_JSON_TYPE);
    }

    // Obtenció de respostes
    // /////////////////////////////////////////////////////////////

    /**
     * Obtenció del resultat d'una petició ja realitzada
     * @param idPeticio Identificador de la petició
     * @return informació de la resposta de la petició
     */
    PeticioRespostaSincrona getResposta(String idPeticio) throws IOException {
        return restPeticioGet("consultes/" + idPeticio + "/resposta", null, PeticioRespostaSincrona.class);
    }

    /**
     * Obtenció del justificant
     * @param idPeticio Identificador de la petició
     * @param idSolicitud Identificador de la sol·licitud
     * @return justificant de la petició
     * @throws Exception
     */
    ScspJustificante getJustificant(String idPeticio, String idSolicitud) throws Exception {
        return restPeticioGet("consultes/" + idPeticio + "/solicitud/" + idSolicitud + "/justificant", null, ScspJustificante.class);
    }

    /**
     * Obtenció de la versió imprimible del justificant
     * @param idPeticio Identificador de la petició
     * @param idSolicitud Identificador de la sol·licitud
     * @return versió imprimible del justificant de la petició
     * @throws Exception
     */
    ScspJustificante getJustificantImprimible(String idPeticio, String idSolicitud) throws Exception {
        return restPeticioGet("consultes/" + idPeticio + "/solicitud/" + idSolicitud + "/justificantImprimible", null, ScspJustificante.class);
    }

    /**
     * Obtenció del CSV del justificant
     * @param idPeticio Identificador de la petició
     * @param idSolicitud Identificador de la sol·licitud
     * @return codi CSV del justificant de la petició a l'arxiu
     * @throws Exception
     */
    String getJustificantCsv(String idPeticio, String idSolicitud) throws Exception {
        return restPeticioGet("consultes/" + idPeticio + "/solicitud/" + idSolicitud + "/justificantCsv", null, String.class);
    }

    /**
     * Obtenció del UUID del justificant
     * @param idPeticio Identificador de la petició
     * @param idSolicitud Identificador de la sol·licitud
     * @return codi UUID del justificant de la petició a l'arxiu
     * @throws Exception
     */
    String getJustificantUuid(String idPeticio, String idSolicitud) throws Exception {
        return restPeticioGet("consultes/" + idPeticio + "/solicitud/" + idSolicitud + "/justificantUuid", null, String.class);
    }

}
