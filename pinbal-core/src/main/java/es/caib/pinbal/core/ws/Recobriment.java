/**
 * 
 */
package es.caib.pinbal.core.ws;

import javax.jws.WebService;

import org.springframework.security.access.prepost.PreAuthorize;

import es.scsp.bean.common.ConfirmacionPeticion;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Respuesta;
import es.scsp.common.exceptions.ScspException;

/**
 * Declaració dels mètodes per al recobriment de les peticions
 * SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = "Recobriment",
		serviceName = "RecobrimentService",
		portName = "RecobrimentServicePort",
		targetNamespace = "http://www.caib.es/pinbal/ws/recobriment")
public interface Recobriment {

	/**
	 * Realitza una petició de tipus síncron emprant SCSP.
	 * 
	 * @param peticion La petició a enviar.
	 * @return La resposta obtinguda.
	 * @throws ScspException Si s'ha produit algun error.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public Respuesta peticionSincrona(
			Peticion peticion) throws ScspException;

	/**
	 * Realitza una petició de tipus asíncron emprant SCSP.
	 * 
	 * @param peticion La petició a enviar.
	 * @return La confirmació de recepció de la petició.
	 * @throws ScspException Si s'ha produit algun error.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public ConfirmacionPeticion peticionAsincrona(
			Peticion peticion) throws ScspException;

	/**
	 * Obtenir l'estat de la petició i la resposta de les taules SCSP.
	 * 
	 * @param idpeticion L'identificador de la petició enviada.
	 * @return La informació de la resposta si ja es troba disponible.
	 * @throws ScspException Si s'ha produit algun error.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public Respuesta getRespuesta(
			String idpeticion) throws ScspException;

	/**
	 * Obtenir un fitxer PDF amb el justificant de la petició enviada.
	 * 
	 * @param idpeticion L'identificador de la petició enviada.
	 * @param idsolicitud L'identificador de la sol·licitud enviada.
	 * @return L'estat de la resposta i la resposta si ja es troba disponible.
	 * @throws ScspException Si s'ha produit algun error.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public byte[] getJustificante(
			String idpeticion,
			String idsolicitud) throws ScspException;

}
