package es.caib.pinbal.plugins.caib.test;

import static org.junit.Assert.assertNotNull;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import es.caib.pinbal.plugins.FirmaServidorPlugin;
import es.caib.pinbal.plugins.FirmaServidorPlugin.TipusFirma;
import es.caib.pinbal.plugins.caib.FirmaServidorPluginPortafib;
import es.caib.pinbal.plugin.PropertiesHelper;

/** Classe de test per provar el plugin de signatura en el servidor de RIPEA.
 * Les implementacions conegudes del plugin són l'API del Portafib i la
 * implmentació mock. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaServidorPluginTest {
	private static String PATH_TEST_FILE = "/firma_servidor_test.pdf";
			
			
	@Before
	public void setUp() throws Exception {
		// Carrega les propietats de test
//		PropertiesHelper.getProperties().setLlegirSystem(false);
		PropertiesHelper.getProperties().load(ClassLoader.getSystemResourceAsStream("test.properties"));
	}

	@Test
	public void signarDocumentPortafibCorrecte() throws Throwable {
		String nom = "";
		String motiu = "prova signatura";
		byte[] contingut = this.obtenirContingutPerFirmar();
		FirmaServidorPlugin signaturaPlugin = new FirmaServidorPluginPortafib();
		try {
			byte[] signatura = signaturaPlugin.firmar(
					nom,
					motiu,
					contingut,
					TipusFirma.PADES,
					"ca");
			assertNotNull("La firma retornada no pot ser nul·la", signatura);
		} catch (Exception ex) {
			System.err.println("Excepció obtinguda signant: " + ex.getMessage());
			ex.printStackTrace();
			throw ex;
		}
	}

	/** Retorna el contingut de l'arxiu per signar.
	 * 
	 * @return 
	 * @throws Throwable 
	 */
	private byte[] obtenirContingutPerFirmar() throws Throwable {
		byte[] contingut = IOUtils.toByteArray(this.getClass().getResourceAsStream(PATH_TEST_FILE)); 
		return contingut;
	}

}
