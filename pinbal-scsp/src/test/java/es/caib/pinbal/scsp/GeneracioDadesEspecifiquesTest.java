/**
 * 
 */
package es.caib.pinbal.scsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.caib.pinbal.core.dto.ArbreDto;
import es.caib.pinbal.core.dto.DadaEspecificaDto;
import es.caib.pinbal.core.dto.NodeDto;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.scsp.XmlHelper.DadesEspecifiquesNode;
import es.caib.pinbal.scsp.tree.Tree;
import org.junit.Test;
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
		/*
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
		*/
		XmlHelper helper = new XmlHelper();
		Element resultat = helper.crearDadesEspecifiques(servicio, dades, false, iniDadesEspecifiques);
		System.out.println("AEATIAE >>> " + nodeToString(resultat));
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
		System.out.println("SVDCCAACPASWS01 >>> " + nodeToString(resultat));
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
		System.out.println("SVDDELSEXCDIWS01 >>> " + nodeToString(resultat));
	}

//	@Test
	public void test_SVDRRCCNACIMIENTOWS01() throws Exception {
		System.setProperty("es.caib.pinbal.xsd.base.path", "/home/siona/Feina/AppData/Pinbal/xsd");
		Servicio servicio = new Servicio();
		servicio.setCodCertificado("SVDRRCCNACIMIENTOWS01");
//		servicio.setCodCertificado("SVDINESECOPACONVIVENCIAACTUALWS01");
		servicio.setVersionEsquema("V3");
		Map<String, Object> dades = new HashMap<String, Object>();
		XmlHelper helper = new XmlHelper();
		Element resultat = helper.crearDadesEspecifiques(servicio, dades, true, iniDadesEspecifiques);
		System.out.println("SVDRRCCNACIMIENTOWS01 >>> " + nodeToString(resultat));
	}

//	@Test
	public void testArbre_SVDRRCCNACIMIENTOWS01() throws Exception {
		System.setProperty("es.caib.pinbal.xsd.base.path", "/home/siona/Feina/AppData/Pinbal/xsd");
		Servicio servicio = new Servicio();
		servicio.setCodCertificado("SVDRRCCNACIMIENTOWS01");
//		servicio.setCodCertificado("SECOPA");
		servicio.setVersionEsquema("V3");
		Map<String, Object> dades = new HashMap<String, Object>();
		XmlHelper helper = new XmlHelper();
		Tree<DadesEspecifiquesNode> arbrePerDadesEspecifiques = helper.getArbrePerDadesEspecifiques(servicio, true);
		System.out.println("SVDRRCCNACIMIENTOWS01 >>> " + arbrePerDadesEspecifiques.toString());

		ArbreDto<DadaEspecificaDto> arbre = new ArbreDto<DadaEspecificaDto>();
		NodeDto<DadaEspecificaDto> arrel = new NodeDto<DadaEspecificaDto>();
		copiarArbreDadesEspecifiques(
				arbrePerDadesEspecifiques.getRootElement(),
				arrel,
				new ArrayList<String>());
		arbre.setArrel(arrel);
		System.out.println("SVDRRCCNACIMIENTOWS01 >>> " + arbre.toString());
	}

	private void copiarArbreDadesEspecifiques(
			es.caib.pinbal.scsp.tree.Node<DadesEspecifiquesNode> source,
			NodeDto<DadaEspecificaDto> target,
			List<String> path) {
		if (source.getData() != null) {
			DadaEspecificaDto dada = new DadaEspecificaDto();
			dada.setPath(path.toArray(new String[path.size()]));
			dada.setNom(source.getData().getNom());
			if (source.getData().getEnumValues().size() > 0)
				dada.setEnumeracioValors(
						source.getData().getEnumValues().toArray(
								new String[source.getData().getEnumValues().size()]));
			dada.setComplexa(source.getData().isComplex());
			dada.setTipusDadaComplexa(DadaEspecificaDto.TipusDadaComplexaEnum.getTipus(source.getData().getGroupType()));
			target.setDades(dada);
		}
		if (source.getNumberOfChildren() > 0) {
			for (es.caib.pinbal.scsp.tree.Node<DadesEspecifiquesNode> child: source.getChildren()) {
				NodeDto<DadaEspecificaDto> fill = new NodeDto<DadaEspecificaDto>();
				path.add(source.getData().getNom());
				copiarArbreDadesEspecifiques(child, fill, path);
				path.remove(path.size() - 1);
				target.addFill(fill);
			}
		}
	}


	private String nodeToString(Node node) {
		Document document = node.getOwnerDocument();
		DOMImplementationLS domImplLS = (DOMImplementationLS) document
		    .getImplementation();
		LSSerializer serializer = domImplLS.createLSSerializer();
		return serializer.writeToString(node);
	}

}
