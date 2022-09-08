/**
 * 
 */
package es.caib.pinbal.scsp;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import es.scsp.common.domain.core.Servicio;

/**
 * Test per a fer consultes amb xpath de les dades dels DatosEspecificos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GeneracioDadesEspecifiquesTest {

	public static boolean iniDadesEspecifiques = true;
	public static void main(String[] args) {
		try {
			new GeneracioDadesEspecifiquesTest().test();
			new GeneracioDadesEspecifiquesTest().test_SVDCCAACPASWS01();
			new GeneracioDadesEspecifiquesTest().test_SVDDELSEXCDIWS01();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void test() throws Exception {
		Servicio servicio = new Servicio();
		servicio.setCodCertificado("AEATIAE");
		servicio.setVersionEsquema("V3");
		Map<String, Object> dades = new HashMap<String, Object>();
		dades.put(
				"DatosEspecificos/Solicitud/Titular/Documentacion/Tipo",
				"NIF");
		dades.put(
				"DatosEspecificos/Solicitud/Titular/Documentacion/Valor",
				"12345678Z");
		dades.put(
				"DatosEspecificos/Solicitud/Titular/DatosPersonales/Documentacion/Tipo",
				"NIF");
		dades.put(
				"DatosEspecificos/Solicitud/ProvinciaSolicitud",
				"07");
		dades.put(
				"DatosEspecificos/Solicitud/MunicipioSolicitud",
				"07033");
		XmlHelper helper = new XmlHelper();
		Element resultat = helper.crearDadesEspecifiques(servicio, dades, false, iniDadesEspecifiques);
		System.out.println(">>> " + nodeToString(resultat));
	}

	public void test_SVDCCAACPASWS01() throws Exception {
		Servicio servicio = new Servicio();
		servicio.setCodCertificado("SVDCCAACPASWS01");
		servicio.setVersionEsquema("V3");
		Map<String, Object> dades = new HashMap<String, Object>();
//		dades.put(
//				"DatosEspecificos/Consulta/CodigoProvincia",
//				"01");
		XmlHelper helper = new XmlHelper();
		Element resultat = helper.crearDadesEspecifiques(servicio, dades, false, iniDadesEspecifiques);
		System.out.println(">>> " + nodeToString(resultat));
	}
	
	public void test_SVDDELSEXCDIWS01() throws Exception {
		Servicio servicio = new Servicio();
		servicio.setCodCertificado("SVDDELSEXCDIWS01");
		servicio.setVersionEsquema("V3");
		Map<String, Object> dades = new HashMap<String, Object>();
//		dades.put(
//				"DatosEspecificos/Consulta/AnioNacimiento",
//				"01");
		XmlHelper helper = new XmlHelper();
		Element resultat = helper.crearDadesEspecifiques(servicio, dades, false, iniDadesEspecifiques);
		System.out.println(">>> " + nodeToString(resultat));
	}


	private String nodeToString(Node node) {
		Document document = node.getOwnerDocument();
		DOMImplementationLS domImplLS = (DOMImplementationLS) document
		    .getImplementation();
		LSSerializer serializer = domImplLS.createLSSerializer();
		return serializer.writeToString(node);
	}

}
