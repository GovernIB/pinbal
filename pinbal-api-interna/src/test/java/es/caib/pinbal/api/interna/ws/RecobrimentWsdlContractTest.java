package es.caib.pinbal.api.interna.ws;

import es.caib.pinbal.logic.intf.ws.Recobriment;
import es.caib.pinbal.logic.intf.ws.model.WsAtributos;
import es.caib.pinbal.logic.intf.ws.model.WsConsentimiento;
import es.caib.pinbal.logic.intf.ws.model.WsEstado;
import es.caib.pinbal.logic.intf.ws.model.WsPeticion;
import es.caib.pinbal.logic.intf.ws.model.WsRespuesta;
import es.caib.pinbal.logic.intf.ws.model.WsSolicitudTransmision;
import es.caib.pinbal.logic.intf.ws.model.WsTipoDocumentacion;
import es.caib.pinbal.logic.intf.ws.model.WsTransmisionDatos;
import es.caib.pinbal.logic.intf.ws.model.WsTransmisiones;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean;
import org.apache.cxf.service.Service;
import org.apache.cxf.wsdl11.ServiceWSDLBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.wsdl.Definition;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica que el WSDL que publica el servei SOAP de recobriment (generat per CXF, el mateix motor
 * que JBossWS) és compatible amb el de PINBAL 1.4.x ({@code wsdl/recobriment-1.4.44.wsdl}), que és
 * el que tenen les aplicacions integrades: mateixos elements, tipus, noms, ordre, cardinalitat i
 * valors d'enumeracions.
 */
class RecobrimentWsdlContractTest {

	private static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";
	private static final String TNS = "http://www.caib.es/pinbal/ws/recobriment";
	/** Tipus del WSDL antic que no fa servir cap missatge (artefactes de l'ArrayList de SCSP 4). */
	private static final Set<String> TIPUS_NO_USATS = new HashSet<>(Arrays.asList(
			"abstractCollection", "abstractList", "arrayList"));

	private static Bus bus;
	private static Element schemaNou;
	private static Element schemaAntic;
	/** JAXBContext que fa servir CXF en temps d'execució (inclou les classes wrapper generades). */
	private static JAXBContext jaxbContext;

	@BeforeAll
	static void generarWsdl() throws Exception {
		bus = BusFactory.newInstance().createBus();
		JaxWsServiceFactoryBean factory = new JaxWsServiceFactoryBean();
		factory.setBus(bus);
		factory.setServiceClass(Recobriment.class);
		Service service = factory.create();
		jaxbContext = ((JAXBDataBinding) service.getDataBinding()).getContext();
		Definition definition = new ServiceWSDLBuilder(bus, service.getServiceInfos()).build();
		StringWriter writer = new StringWriter();
		WSDLFactory.newInstance().newWSDLWriter().writeWSDL(definition, writer);
		schemaNou = schemaServei(parse(writer.toString()));
		try (InputStream is = RecobrimentWsdlContractTest.class.getResourceAsStream("/wsdl/recobriment-1.4.44.wsdl")) {
			assertNotNull(is);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			schemaAntic = schemaServei(dbf.newDocumentBuilder().parse(is));
		}
	}

	@AfterAll
	static void tancar() {
		if (bus != null) {
			bus.shutdown(true);
		}
	}

	@Test
	void elementsLocalsNoQualificats() {
		assertEquals("unqualified", valorODefecte(schemaNou.getAttribute("elementFormDefault"), "unqualified"));
	}

	@Test
	void mateixosElementsGlobals() {
		Set<String> nous = elementsGlobals(schemaNou);
		assertTrue(nous.containsAll(elementsGlobals(schemaAntic)), "Elements nous: " + nous);
		// L'únic element afegit és el del fault (ScspException): no afecta els clients existents
		nous.removeAll(elementsGlobals(schemaAntic));
		assertEquals(new HashSet<>(Arrays.asList("ScspException : ScspException")), nous);
	}

	@Test
	void mateixosTipusComplexos() {
		Map<String, List<String>> antics = tipusComplexos(schemaAntic);
		Map<String, List<String>> nous = tipusComplexos(schemaNou);
		for (Map.Entry<String, List<String>> antic : antics.entrySet()) {
			if (TIPUS_NO_USATS.contains(antic.getKey())) {
				continue;
			}
			assertTrue(nous.containsKey(antic.getKey()), "Falta el tipus " + antic.getKey() + ". Tipus nous: " + nous.keySet());
			assertEquals(antic.getValue(), nous.get(antic.getKey()), "Tipus " + antic.getKey());
		}
	}

	@Test
	void mateixesEnumeracions() {
		Map<String, List<String>> antics = tipusSimples(schemaAntic);
		Map<String, List<String>> nous = tipusSimples(schemaNou);
		assertEquals(antics.keySet(), nous.keySet());
		for (String nom : antics.keySet()) {
			assertEquals(new HashSet<>(antics.get(nom)), new HashSet<>(nous.get(nom)), "Enumeració " + nom);
		}
	}

	// ---------------------------------------------------------------------------------------------
	// Format dels missatges (amb el JAXBContext de CXF)
	// ---------------------------------------------------------------------------------------------

	/** Petició com l'envia un client generat amb el WSDL de la 1.4.x (és la que ara es rebutjava). */
	static final String PETICIO_SINCRONA_1_4 =
			"<ns2:peticionSincrona xmlns:ns2=\"" + TNS + "\">"
			+ "<arg0>"
			+ "<atributos><codigoCertificado>SCDHPAJU</codigoCertificado><idPeticion>PET-1</idPeticion>"
			+ "<numElementos>1</numElementos><timeStamp>2026-11-25T10:00:00.000+01:00</timeStamp></atributos>"
			+ "<solicitudes><solicitudTransmision>"
			+ "<datosEspecificos><Solicitud xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">"
			+ "<ProvinciaSolicitud>07</ProvinciaSolicitud><MunicipioSolicitud>040</MunicipioSolicitud></Solicitud></datosEspecificos>"
			+ "<datosGenericos>"
			+ "<emisor><nifEmisor>S0711001H</nifEmisor><nombreEmisor>Emissor</nombreEmisor></emisor>"
			+ "<solicitante><codigoUnidadTramitadora>A04000000</codigoUnidadTramitadora><consentimiento>Si</consentimiento>"
			+ "<finalidad>Finalitat</finalidad><funcionario><nifFuncionario>12345678Z</nifFuncionario>"
			+ "<nombreCompletoFuncionario>Funcionari</nombreCompletoFuncionario></funcionario>"
			+ "<idExpediente>EXP-1</idExpediente><identificadorSolicitante>S0711001H</identificadorSolicitante>"
			+ "<nombreSolicitante>CAIB</nombreSolicitante><procedimiento><codProcedimiento>P1</codProcedimiento>"
			+ "<nombreProcedimiento>Procediment</nombreProcedimiento></procedimiento><unidadTramitadora>Unitat</unidadTramitadora>"
			+ "</solicitante>"
			+ "<titular><documentacion>12345678Z</documentacion><nombreCompleto>Nom Complet</nombreCompleto>"
			+ "<tipoDocumentacion>NIF</tipoDocumentacion></titular>"
			+ "<transmision><codigoCertificado>SCDHPAJU</codigoCertificado><idSolicitud>PET-1</idSolicitud></transmision>"
			+ "</datosGenericos>"
			+ "</solicitudTransmision></solicitudes>"
			+ "</arg0>"
			+ "</ns2:peticionSincrona>";

	@Test
	void llegeixLaPeticioDelsClients14() throws Exception {
		WsPeticion peticion = (WsPeticion) arg0(unmarshaller().unmarshal(new DOMSource(parse(PETICIO_SINCRONA_1_4))));

		assertEquals("SCDHPAJU", peticion.getAtributos().getCodigoCertificado());
		assertEquals("PET-1", peticion.getAtributos().getIdPeticion());
		assertEquals("1", peticion.getAtributos().getNumElementos());
		WsSolicitudTransmision st = peticion.getSolicitudes().getSolicitudTransmision().get(0);
		assertEquals("S0711001H", st.getDatosGenericos().getEmisor().getNifEmisor());
		assertEquals(WsConsentimiento.Si, st.getDatosGenericos().getSolicitante().getConsentimiento());
		assertEquals("A04000000", st.getDatosGenericos().getSolicitante().getCodigoUnidadTramitadora());
		assertEquals("P1", st.getDatosGenericos().getSolicitante().getProcedimiento().getCodProcedimiento());
		assertEquals(WsTipoDocumentacion.NIF, st.getDatosGenericos().getTitular().getTipoDocumentacion());
		// xs:anyType -> element DOM <datosEspecificos> amb el contingut del client, com a la 1.4.x
		assertTrue(st.getDatosEspecificos() instanceof Element);
		Element de = (Element) st.getDatosEspecificos();
		assertEquals("datosEspecificos", de.getLocalName());
		assertEquals("040", de.getElementsByTagNameNS("*", "MunicipioSolicitud").item(0).getTextContent());
	}

	@Test
	void laPeticioAmbElFormatDeLaVersio201EsRebutja() throws Exception {
		// Format que publicava la 2.0.1 (beans SCSP 5): ja no forma part del contracte
		String peticio201 = "<ns2:peticionSincrona xmlns:ns2=\"" + TNS + "\"><arg0>"
				+ "<Atributos xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/peticion\"><IdPeticion>PET-1</IdPeticion></Atributos>"
				+ "</arg0></ns2:peticionSincrona>";
		Unmarshaller unmarshaller = unmarshaller();
		unmarshaller.setEventHandler(event -> false);
		assertThrows(UnmarshalException.class, () -> unmarshaller.unmarshal(new DOMSource(parse(peticio201))));
	}

	@Test
	void generaLaRespostaAmbElFormat14() throws Exception {
		WsRespuesta respuesta = new WsRespuesta();
		WsAtributos atributos = new WsAtributos();
		atributos.setIdPeticion("PET-1");
		atributos.setNumElementos("1");
		atributos.setCodigoCertificado("SCDHPAJU");
		WsEstado estado = new WsEstado();
		estado.setCodigoEstado("0003");
		atributos.setEstado(estado);
		respuesta.setAtributos(atributos);
		WsTransmisiones transmisiones = new WsTransmisiones();
		WsTransmisionDatos td = new WsTransmisionDatos();
		td.setDatosEspecificos(parse("<DatosEspecificos xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">"
				+ "<Estado><CodigoEstado>0003</CodigoEstado></Estado></DatosEspecificos>").getDocumentElement());
		transmisiones.getTransmisionDatos().add(td);
		respuesta.setTransmisiones(transmisiones);

		StringWriter writer = new StringWriter();
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.marshal(new JAXBElement<>(new QName("return"), WsRespuesta.class, respuesta), writer);
		String xml = writer.toString();

		assertTrue(xml.startsWith("<return"), xml);
		assertTrue(xml.contains("<atributos><codigoCertificado>SCDHPAJU</codigoCertificado>"
				+ "<estado><codigoEstado>0003</codigoEstado></estado><idPeticion>PET-1</idPeticion>"
				+ "<numElementos>1</numElementos></atributos>"), xml);
		assertTrue(xml.contains("<transmisiones><transmisionDatos><datosEspecificos"), xml);
		assertTrue(xml.contains("<CodigoEstado>0003</CodigoEstado>"), xml);
		assertFalse(xml.contains("<Atributos"), xml);
	}

	@Test
	void respostaBuidaSenseElements() throws Exception {
		StringWriter writer = new StringWriter();
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.marshal(new JAXBElement<>(new QName("return"), WsRespuesta.class, new WsRespuesta()), writer);
		assertTrue(writer.toString().matches("<return( xmlns:ns2=\"[^\"]*\")?/>"), writer.toString());
	}

	private static Unmarshaller unmarshaller() throws Exception {
		return jaxbContext.createUnmarshaller();
	}

	private static Object arg0(Object wrapper) throws Exception {
		Object valor = wrapper instanceof JAXBElement ? ((JAXBElement<?>) wrapper).getValue() : wrapper;
		assertNotNull(valor);
		return valor.getClass().getMethod("getArg0").invoke(valor);
	}

	// ---------------------------------------------------------------------------------------------

	private static Element schemaServei(Document wsdl) {
		for (Element schema : fills(wsdl.getDocumentElement(), XSD_NS, "schema", true)) {
			if (TNS.equals(schema.getAttribute("targetNamespace"))) {
				return schema;
			}
		}
		throw new AssertionError("No s'ha trobat l'esquema " + TNS);
	}

	private static Set<String> elementsGlobals(Element schema) {
		Set<String> result = new HashSet<>();
		for (Element e : fills(schema, XSD_NS, "element", false)) {
			result.add(e.getAttribute("name") + " : " + localName(e.getAttribute("type")));
		}
		return result;
	}

	private static Map<String, List<String>> tipusComplexos(Element schema) {
		Map<String, List<String>> result = new LinkedHashMap<>();
		for (Element ct : fills(schema, XSD_NS, "complexType", false)) {
			List<String> elements = new ArrayList<>();
			for (Element e : fills(ct, XSD_NS, "element", true)) {
				elements.add(e.getAttribute("name")
						+ " type=" + localName(e.getAttribute("type"))
						+ " min=" + valorODefecte(e.getAttribute("minOccurs"), "1")
						+ " max=" + valorODefecte(e.getAttribute("maxOccurs"), "1")
						+ " nillable=" + valorODefecte(e.getAttribute("nillable"), "false"));
			}
			result.put(ct.getAttribute("name"), elements);
		}
		return result;
	}

	private static Map<String, List<String>> tipusSimples(Element schema) {
		Map<String, List<String>> result = new LinkedHashMap<>();
		for (Element st : fills(schema, XSD_NS, "simpleType", false)) {
			List<String> valors = new ArrayList<>();
			for (Element en : fills(st, XSD_NS, "enumeration", true)) {
				valors.add(en.getAttribute("value"));
			}
			result.put(st.getAttribute("name"), valors);
		}
		return result;
	}

	private static List<Element> fills(Element pare, String ns, String nom, boolean recursiu) {
		List<Element> result = new ArrayList<>();
		for (Node n = pare.getFirstChild(); n != null; n = n.getNextSibling()) {
			if (n.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element e = (Element) n;
			if (ns.equals(e.getNamespaceURI()) && nom.equals(e.getLocalName())) {
				result.add(e);
			} else if (recursiu) {
				result.addAll(fills(e, ns, nom, true));
			}
		}
		return result;
	}

	private static String localName(String qname) {
		return qname.contains(":") ? qname.substring(qname.indexOf(':') + 1) : qname;
	}

	private static String valorODefecte(String valor, String defecte) {
		return valor == null || valor.isEmpty() ? defecte : valor;
	}

	private static Document parse(String xml) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
	}

}
