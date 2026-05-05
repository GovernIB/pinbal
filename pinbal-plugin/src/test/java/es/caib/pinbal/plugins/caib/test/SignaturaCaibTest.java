/**
 * 
 */
package es.caib.pinbal.plugins.caib.test;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import es.caib.signatura.api.Signer;
import es.caib.signatura.api.SignerFactory;

/**
 * Test per a comprovar la signatura de documents.
 * 
 * Per a executar correctament el test s'han de configurar les següents
 * propietats de sistema:
 *     - caib-crypto-keystore: path al keystore amb les claus
 *     - caib-crypto-keystore-password: contrasenya del keystore
 * 
 * Exemple: -Dcaib-crypto-keystore=/cert.jks -Dcaib-crypto-keystore-password=1234
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SignaturaCaibTest {

	private static final String CONTENT_TYPE = "application/pdf";
	private static final String CERT_NAME = "TEST: TEST CAIB - certificado 12345678Z";
	//private static final String CERT_NAME = "TEST: TEST CAIB - certificado 32323232K";
	private static final String CERT_PASSWORD = "1234";
	private static final String RESOURCE_FILE_NAME = "/original.pdf";

	@Test
	public void signarDocument() throws Exception {
		/*String[] certs = getSigner().getCertList(CONTENT_TYPE);
		for (String cert: certs)
			System.out.println(">>> " + cert);*/
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		getSigner().signPDF(
				getClass().getResourceAsStream(RESOURCE_FILE_NAME),
				baos,
				CERT_NAME,
				CERT_PASSWORD,
				CONTENT_TYPE,
				"documentUrl",
				Signer.PDF_SIGN_POSITION_LEFT);
		/*FileOutputStream fop = new FileOutputStream("out.pdf");
		fop.write(baos.toByteArray());
		fop.close();*/
	}

	private Signer getSigner() throws Exception {
		SignerFactory sf = new SignerFactory();
		return sf.getSigner();
	}

}
