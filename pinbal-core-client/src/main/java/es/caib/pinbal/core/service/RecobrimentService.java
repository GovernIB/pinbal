/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.core.service.exception.RecobrimentScspException;

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

}
