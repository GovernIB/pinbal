/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.pinbal.client.procediments.ProcedimentBasic;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.Entitat;
import es.caib.pinbal.client.recobriment.v2.PeticioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioConfirmacioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaSincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioSincrona;
import es.caib.pinbal.client.recobriment.v2.ValorEnum;
import es.caib.pinbal.client.serveis.ServeiBasic;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.RecobrimentScspException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

/**
 * Declaració dels mètodes per a fer peticions al recobriment SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RecobrimentService {

	/**
	 * Realitza una petició síncrona al recobriment SCSP.
	 * 
	 * @param peticion
	 *            petició SCSP a enviar.
	 * @return la resposta a la petició.
	 * @throws RecobrimentScspException
	 *            Si hi s'han produit excepcions SCSP al fer la petició.
	 */
	public ScspRespuesta peticionSincrona(
			ScspPeticion peticion) throws RecobrimentScspException;

	/**
	 * Realitza una petició asíncrona al recobriment SCSP.
	 * 
	 * @param peticion
	 *            petició SCSP a enviar.
	 * @return la confirmació de la petició.
	 * @throws RecobrimentScspException
	 *            Si hi s'han produit excepcions SCSP al fer la petició.
	 */
	public ScspConfirmacionPeticion peticionAsincrona(
			ScspPeticion peticion) throws RecobrimentScspException;

	/**
	 * Obté la resposta d'una petició SCSP enviada.
	 * 
	 * @param idPeticion
	 *            id de la petició SCSP enviada.
	 * @return la resposta associada a la petició.
	 * @throws RecobrimentScspException
	 *            Si hi s'han produit excepcions SCSP al fer la petició.
	 */
	public ScspRespuesta getRespuesta(
			String idPeticion) throws RecobrimentScspException;

	/**
	 *
	 * @param idPeticion
	 *            id de la petició SCSP enviada.
	 * @param idSolicitud
	 *            id de la sol·licitud dins la petició SCSP.
	 * @return el fitxer amb el justificant.
	 * @throws RecobrimentScspException
	 *            Si hi s'han produit excepcions SCSP al fer la petició.
	 */
	public ScspJustificante getJustificante(
			String idPeticion,
			String idSolicitud) throws RecobrimentScspException;

	/**
	 *
	 * @param idPeticion
	 *            id de la petició SCSP enviada.
	 * @param idSolicitud
	 *            id de la sol·licitud dins la petició SCSP.
	 * @return el fitxer amb la versió imprimible del justificant.
	 * @throws RecobrimentScspException
	 *            Si hi s'han produit excepcions SCSP al fer la petició.
	 */
	public ScspJustificante getJustificanteImprimible(
			String idPeticion,
			String idSolicitud) throws RecobrimentScspException;

	/**
	 *
	 * @param idPeticion
	 *            id de la petició SCSP enviada.
	 * @param idSolicitud
	 *            id de la sol·licitud dins la petició SCSP.
	 * @return el CSV del justificant.
	 * @throws RecobrimentScspException
	 *            Si hi s'han produit excepcions SCSP al fer la petició.
	 */
	public String getJustificanteCsv(
			String idPeticion,
			String idSolicitud) throws RecobrimentScspException;

	/**
	 *
	 * @param idPeticion
	 *            id de la petició SCSP enviada.
	 * @param idSolicitud
	 *            id de la sol·licitud dins la petició SCSP.
	 * @return l'UUID del justificant.
	 * @throws RecobrimentScspException
	 *            Si hi s'han produit excepcions SCSP al fer la petició.
	 */
	public String getJustificanteUuid(
			String idPeticion,
			String idSolicitud) throws RecobrimentScspException;



	// V2
	// /////////////////////////////////////////////////////////////

	/**
	 * @return llista d'entitats a les que l'usuari autenticat té permís
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	List<Entitat> getEntitats();

	/**
	 * @param entitatCodi codi de l'entitat
	 * @return llista de procediments disponibles per l'entitat especificada
	 * @throws EntitatNotFoundException
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	List<ProcedimentBasic> getProcediments(String entitatCodi) throws EntitatNotFoundException;

	/**
	 * @return llista de tots els elements de tipus Servei configurats a PINBAL
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	List<ServeiBasic> getServeis();

	/**
	 * @param entitatCodi codi de l'entitat
	 * @return lista de tots els elements de tipus Servei d’una entitat
	 * @throws EntitatNotFoundException
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	List<ServeiBasic> getServeisByEntitat(String entitatCodi) throws EntitatNotFoundException;

	/**
	 * @param procedimentCodi codi del procediment
	 * @return llista de tots els elements de tipus Servei d’un procediment
	 * @throws ProcedimentNotFoundException
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	List<ServeiBasic> getServeisByProcediment(String procedimentCodi) throws ProcedimentNotFoundException;

	/**
	 * @param serveiCodi codi del servei
	 * @return llista de camps que son necessaris per emplenar l’apartat de dades específiques de la petició SCSP
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	List<DadaEspecifica> getDadesEspecifiquesByServei(String serveiCodi) throws ServeiNotFoundException;

	/**
	 * @param serveiCodi codi del servei
	 * @param enumCodi codi de l'enumerat
	 * @param filtre filtre a aplicar en l’obtenció dels possibles valors de l’enumerat (opcional)
	 * @return llistes de valors, siguin de enumerats o de valors de dades externes
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	List<ValorEnum> getValorsEnumByServei(String serveiCodi, String campCodi, String enumCodi, String filtre) throws Exception;

	/**
	 * Valida les dades d'una petició síncrona
	 *
	 * @param peticio Dades de la petició síncrona a validar
	 * @return Mapa amb els errors detectats a la petició.
	 * La clau correspon al path del camp amb error, i el valor al missatge d'error
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	Map<String, List<String>> validatePeticio(String serveiCodi, PeticioSincrona peticio);

	/**
	 * Valida les dades d'una petició asíncrona
	 *
	 * @param peticio Dades de la petició asíncrona a validar
	 * @return Mapa amb els errors detectats a la petició.
	 * La clau correspon al path del camp amb error, i el valor al missatge d'error
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	Map<String, List<String>> validatePeticio(String serveiCodi, PeticioAsincrona peticio);

	/**
	 * Realitza una petició síncrona al recobriment SCSP.
	 *
	 * @param peticio
	 *            petició a enviar.
	 * @return la resposta a la petició.
	 * @throws RecobrimentScspException
	 *            Si hi s'han produit excepcions al fer la petició.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public PeticioRespostaSincrona peticionSincrona(PeticioSincrona peticio);

	/**
	 * Realitza una petició asíncrona al recobriment SCSP.
	 *
	 * @param peticio
	 *            petició a enviar.
	 * @return la resposta a la petició.
	 * @throws RecobrimentScspException
	 *            Si hi s'han produit excepcions al fer la petició.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public PeticioConfirmacioAsincrona peticionAsincrona(PeticioAsincrona peticio);

	/**
	 * Obté la resposta d'una petició SCSP enviada.
	 *
	 * @param idPeticion
	 *            id de la petició SCSP enviada.
	 * @return la resposta associada a la petició.
	 * @throws RecobrimentScspException
	 *            Si hi s'han produit excepcions SCSP al fer la petició.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public PeticioRespostaAsincrona getResposta(String idPeticion) throws RecobrimentScspException, ConsultaNotFoundException;

	/**
	 *
	 * @param idPeticion
	 *            id de la petició SCSP enviada.
	 * @param idSolicitud
	 *            id de la sol·licitud dins la petició SCSP.
	 * @return el fitxer amb el justificant.
	 * @throws RecobrimentScspException
	 *            Si hi s'han produit excepcions SCSP al fer la petició.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public ScspJustificante getJustificant(
			String idPeticion,
			String idSolicitud) throws RecobrimentScspException, ConsultaNotFoundException;

	/**
	 *
	 * @param idPeticion
	 *            id de la petició SCSP enviada.
	 * @param idSolicitud
	 *            id de la sol·licitud dins la petició SCSP.
	 * @return el fitxer amb la versió imprimible del justificant.
	 * @throws RecobrimentScspException
	 *            Si hi s'han produit excepcions SCSP al fer la petició.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public ScspJustificante getJustificantImprimible(
			String idPeticion,
			String idSolicitud) throws RecobrimentScspException, ConsultaNotFoundException;

	/**
	 *
	 * @param idPeticion
	 *            id de la petició SCSP enviada.
	 * @param idSolicitud
	 *            id de la sol·licitud dins la petició SCSP.
	 * @return el CSV del justificant.
	 * @throws RecobrimentScspException
	 *            Si hi s'han produit excepcions SCSP al fer la petició.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public String getJustificantCsv(
			String idPeticion,
			String idSolicitud) throws RecobrimentScspException, ConsultaNotFoundException;

	/**
	 *
	 * @param idPeticion
	 *            id de la petició SCSP enviada.
	 * @param idSolicitud
	 *            id de la sol·licitud dins la petició SCSP.
	 * @return l'UUID del justificant.
	 * @throws RecobrimentScspException
	 *            Si hi s'han produit excepcions SCSP al fer la petició.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public String getJustificantUuid(
			String idPeticion,
			String idSolicitud) throws RecobrimentScspException, ConsultaNotFoundException;
}
