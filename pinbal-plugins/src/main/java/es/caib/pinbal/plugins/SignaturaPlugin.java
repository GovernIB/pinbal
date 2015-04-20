/**
 * 
 */
package es.caib.pinbal.plugins;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.X509Certificate;

/**
 * Plugin per a emmagatzemar i obtenir documents de custòdia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SignaturaPlugin {

	public enum CertificatValidacioResultat {
		OK,
		INVALID,
		REVOCAT
	}

	/**
	 * Signa un document PDF i l'estampa amb una URL de validació
	 * del document.
	 * 
	 * @param contentStream
	 *            Stream d'entrada amb el contingut del document.
	 * @param signedStream
	 *            Stream de sortida amb el document signat.
	 * @param url
	 *            Url de verificació del document a estampar.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun error durant el procés.
	 */
	public void signarEstamparPdf(
			InputStream contentStream,
			OutputStream signedStream,
			String url) throws SistemaExternException;

	/**
	 * Valida la validesa d'un certificat.
	 * 
	 * @param certificat
	 *            Certificat a comprovar.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun error durant el procés.
	 */
	public CertificatValidacioResultat validarCertificat(
			X509Certificate certificat) throws SistemaExternException;

}
