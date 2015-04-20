/**
 * 
 */
package es.caib.pinbal.plugins;


/**
 * Plugin per a emmagatzemar i obtenir documents de custòdia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CustodiaPlugin {

	/**
	 * Obté la url de comprovació de signatura donat un identificador
	 * de document.
	 * 
	 * @param documentId
	 *            Identificador del document a enviar.
	 * @return La URL de comprovació de signatura.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun error durant el procés.
	 */
	public String obtenirUrlVerificacioDocument(
			String documentId) throws SistemaExternException;

	/**
	 * Envia un document PDF signat a la custòdia.
	 * 
	 * @param documentId
	 *            Identificador del document a enviar.
	 * @param arxiuNom
	 *            Nom de l'arxiu a custodiar.
	 * @param arxiuContingut
	 *            Contingut de l'arxiu a custodiar.
	 * @param documentTipus
	 *            Codi de tipus de document.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun error durant el procés.
	 */
	public void enviarPdfSignat(
			String documentId,
			String arxiuNom,
			byte[] arxiuContingut,
			String documentTipus) throws SistemaExternException;

	/**
	 * Obté un document de la custòdia.
	 * 
	 * @param documentId
	 *            Identificador del document custodiat.
	 * @return el document com un array de bytes.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun error durant el procés.
	 */
	public byte[] obtenirDocument(
			String documentId) throws SistemaExternException;

}
