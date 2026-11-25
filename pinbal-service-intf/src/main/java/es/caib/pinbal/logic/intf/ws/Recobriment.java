/**
 * 
 */
package es.caib.pinbal.logic.intf.ws;

import es.caib.pinbal.logic.intf.ws.model.WsConfirmacionPeticion;
import es.caib.pinbal.logic.intf.ws.model.WsPeticion;
import es.caib.pinbal.logic.intf.ws.model.WsRespuesta;
import es.scsp.common.exceptions.ScspException;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.jws.WebService;

/**
 * Declaració dels mètodes per al recobriment de les peticions
 * SCSP.
 * <p>
 * El contracte (WSDL) ha de ser el mateix que publicava PINBAL 1.4.x: les aplicacions integrades
 * tenen el client generat a partir d'aquell WSDL. Per això els tipus són els de
 * {@code es.caib.pinbal.logic.intf.ws.model} i no els beans de SCSP 5.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = "Recobriment",
		targetNamespace = "http://www.caib.es/pinbal/ws/recobriment")
public interface Recobriment {

	/**
	 * Realitza una petició de tipus síncron emprant SCSP.
	 * 
	 * @param peticion La petició a enviar.
	 * @return La resposta obtinguda.
	 * @throws ScspException Si s'ha produit algun error.
	 */
	@PreAuthorize("hasRole('PBL_WS')")
	public WsRespuesta peticionSincrona(
			WsPeticion peticion) throws ScspException;

	/**
	 * Realitza una petició de tipus asíncron emprant SCSP.
	 * 
	 * @param peticion La petició a enviar.
	 * @return La confirmació de recepció de la petició.
	 * @throws ScspException Si s'ha produit algun error.
	 */
	@PreAuthorize("hasRole('PBL_WS')")
	public WsConfirmacionPeticion peticionAsincrona(
			WsPeticion peticion) throws ScspException;

	/**
	 * Obtenir l'estat de la petició i la resposta de les taules SCSP.
	 * 
	 * @param idpeticion L'identificador de la petició enviada.
	 * @return La informació de la resposta si ja es troba disponible.
	 * @throws ScspException Si s'ha produit algun error.
	 */
	@PreAuthorize("hasRole('PBL_WS')")
	public WsRespuesta getRespuesta(
			String idpeticion) throws ScspException;

	/**
	 * Obtenir un fitxer PDF amb el justificant de la petició enviada.
	 * 
	 * @param idpeticion L'identificador de la petició enviada.
	 * @param idsolicitud L'identificador de la sol·licitud enviada.
	 * @return L'estat de la resposta i la resposta si ja es troba disponible.
	 * @throws ScspException Si s'ha produit algun error.
	 */
	@PreAuthorize("hasRole('PBL_WS')")
	public byte[] getJustificante(
			String idpeticion,
			String idsolicitud) throws ScspException;

}
