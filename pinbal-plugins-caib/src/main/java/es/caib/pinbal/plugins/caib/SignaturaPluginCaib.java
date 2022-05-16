/**
 * 
 */
package es.caib.pinbal.plugins.caib;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import es.caib.pinbal.plugin.PropertiesHelper;
import es.caib.pinbal.plugins.SignaturaPlugin;
import es.caib.pinbal.plugins.SistemaExternException;
import es.caib.signatura.api.Signer;
import es.caib.signatura.impl.CAIBSigner;
import es.caib.signatura.impl.SignaturaProperties;
import es.caib.signatura.impl.ValidadorProxy;

/**
 * Implementació del plugin de signatura emprant l'API de signatura de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SignaturaPluginCaib implements SignaturaPlugin {

	@Override
	public void signarEstamparPdf(
			InputStream contentStream,
			OutputStream signedStream,
			String url)
			throws SistemaExternException {
		try {
			getSigner().signPDF(
					contentStream,
					signedStream,
					getPropertyCertAlias(),
					getPropertyCertPassword(),
					getPropertyContentTypeSignatura(),
					url,
					Signer.PDF_SIGN_POSITION_LEFT);
		} catch (Exception ex) {
			throw new SistemaExternException("Error al signar document PDF", ex);
		}
	}

	@Override
	public CertificatValidacioResultat validarCertificat(
			X509Certificate certificat) throws SistemaExternException {
		// Primer hauries d'instanciar un CAIBSigner
		// http://www.caib.es/signaturacaib/docum/guia.html#_Toc310255295
		// Signer signer = new CAIBSigner(); (el comentam perquè no s'empra)
		// Primer miram que el certificat sigui vàlid
		try {
			certificat.checkValidity();
		} catch (CertificateExpiredException cee) {
			System.out.println("CMSSignature Certificat rebutjat, el certificat ha caducat.");
			return CertificatValidacioResultat.INVALID;
		} catch (CertificateNotYetValidException cve) {
			System.out.println("CMSSignature Certificat rebutjat, el certificat encara no és vàlid.");
			return CertificatValidacioResultat.INVALID;
		}
		boolean isVerified = true;
		try {
			X509Certificate certificateChain[] = new X509Certificate[] {certificat};
			// (Comentat perquè només copia un chain a damunt l'altre)
			/*X509Certificate validationCertificateChain[] = new X509Certificate[certificateChain.length];
			// Se invierte la cadena de certificación para adaptarla a la
			// especificación del validador.
			for (int i = 0; i < validationCertificateChain.length; i++) {
				validationCertificateChain[i] = certificateChain[i];
			}*/
			// mires si el client del validador està instal.lat
			// recorda que la propietat es.caib.signatura.library_path indica el
			// directori on està la instalació de producció, preproducció o
			// desenvolupament:
			// http://www.caib.es/signaturacaib/docum/manual_instal_servidores.jsp
			ValidadorProxy validador = new ValidadorProxy();
			if (validador.isValidadorInstalado()) {
				ClassLoader prevCL = Thread.currentThread().getContextClassLoader();
				Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
				try {
					// Això no cal, només és per diferenciar si ha
					// de validar si val per a firma o per a autenticació.
					SignaturaProperties properties = getSignaturaProperties();
					if (!properties.needsRecognizedCertificate(getPropertyContentTypeValidacio()))
						isVerified = validador.validarAutenticacion(certificateChain);
					else
						isVerified = validador.validarFirma(certificateChain);
				} catch (Exception ex) {
					throw new SistemaExternException("Error al validar el certificat", ex);
				} finally {
					Thread.currentThread().setContextClassLoader(prevCL);
				}
			}
			return (isVerified) ? CertificatValidacioResultat.OK : CertificatValidacioResultat.REVOCAT;
		} catch (Exception ex) {
			throw new SistemaExternException("Error al validar el certificat", ex);
		}
	}



	@SuppressWarnings("deprecation")
	private Signer getSigner() throws Exception {
		/*SignerFactory sf = new SignerFactory();
		return sf.getSigner();*/
		return new CAIBSigner();
	}
	@SuppressWarnings("deprecation")
	private SignaturaProperties getSignaturaProperties() throws Exception {
		return new SignaturaProperties();
	}

	private String getPropertyCertAlias() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.pinbal.plugin.signatura.caib.certificat.alias");
	}
	private String getPropertyCertPassword() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.pinbal.plugin.signatura.caib.certificat.password");
	}
	private String getPropertyContentTypeSignatura() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.pinbal.plugin.signatura.caib.signatura.content.type");
	}
	private String getPropertyContentTypeValidacio() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.pinbal.plugin.signatura.caib.validacio.content.type");
	}

}
