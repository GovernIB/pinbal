package es.caib.pinbal.scsp.binding;

import es.caib.pinbal.scsp.SchemaValidatorVfsCompat;
import es.caib.pinbal.scsp.XmlHelper;
import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.peticion.Atributos;
import es.scsp.bean.common.peticion.Consentimiento;
import es.scsp.bean.common.peticion.DatosGenericos;
import es.scsp.bean.common.peticion.Emisor;
import es.scsp.bean.common.peticion.Funcionario;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.peticion.Procedimiento;
import es.scsp.bean.common.peticion.Solicitante;
import es.scsp.bean.common.peticion.SolicitudTransmision;
import es.scsp.bean.common.peticion.Solicitudes;
import es.scsp.bean.common.peticion.TipoDocumentacion;
import es.scsp.bean.common.peticion.Titular;
import es.scsp.bean.common.peticion.Transmision;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.bean.common.respuesta.TransmisionDatos;
import es.scsp.bean.common.solicitud.SolicitudRespuesta;
import es.scsp.client.BasicServiceClientSpring;
import es.scsp.client.ClienteUnico;
import es.scsp.common.core.ServiceManager;
import es.scsp.common.dao.PeticionRespuestaDao;
import es.scsp.common.dao.ServeiDao;
import es.scsp.common.dao.TransmisionDao;
import es.scsp.common.domain.core.PeticionRespuesta;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.exceptions.ScspException;
import es.scsp.common.signature.XMLDSigManager;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEvaluators;

import javax.security.auth.x500.X500Principal;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests de {@link ScspJaxb2Marshaller}: les peticions SCSP han de tenir la forma de namespaces de
 * SCSP 4 (DatosEspecificos amb xmlns per defecte, sense ns2..ns5) sense cap altre canvi respecte de
 * les que genera el {@link Jaxb2Marshaller} que s'usava fins ara.
 */
public class ScspJaxb2MarshallerTest {

	private static final String NS_PETICION = "http://intermediacion.redsara.es/scsp/esquemas/V3/peticion";
	private static final String NS_SOLICITUD = "http://intermediacion.redsara.es/scsp/esquemas/V3/solicitudRespuesta";
	private static final String NS_RESPUESTA = "http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta";
	private static final String NS_CONFIRMACION = "http://intermediacion.redsara.es/scsp/esquemas/V3/confirmacionPeticion";
	private static final String NS_DE = ScspNamespaceNormalizer.DATOS_ESPECIFICOS_NS;
	private static final String NS_SOAP = "http://schemas.xmlsoap.org/soap/envelope/";

	private static final Class<?>[] CLASSES = {
			Peticion.class,
			Respuesta.class,
			SolicitudRespuesta.class,
			ConfirmacionPeticion.class };

	private static KeyPair keyPair;
	private static X509Certificate certificat;

	/** Marshaller tal com estava configurat fins ara (application-context-scsp.xml). */
	private Jaxb2Marshaller marshallerActual;
	private ScspJaxb2Marshaller marshallerNou;
	/** Configuració dels serveis: per defecte, tots amb el format de SCSP 4 activat. */
	private ServeiDao serveiDao;

	@BeforeClass
	public static void inicialitzarClaus() throws Exception {
		org.apache.xml.security.Init.init();
		Security.addProvider(new BouncyCastleProvider());
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		keyPair = kpg.generateKeyPair();
		X500Principal dn = new X500Principal("CN=PINBAL test, O=Test");
		@SuppressWarnings("deprecation")
		X509V3CertificateGenerator generador = new X509V3CertificateGenerator();
		generador.setSerialNumber(BigInteger.ONE);
		generador.setIssuerDN(dn);
		generador.setSubjectDN(dn);
		generador.setNotBefore(new Date(System.currentTimeMillis() - 86400000L));
		generador.setNotAfter(new Date(System.currentTimeMillis() + 86400000L));
		generador.setPublicKey(keyPair.getPublic());
		generador.setSignatureAlgorithm("SHA256WithRSAEncryption");
		certificat = generador.generate(keyPair.getPrivate(), "BC");
	}

	@Before
	public void configurar() throws Exception {
		marshallerActual = new Jaxb2Marshaller();
		marshallerActual.setClassesToBeBound(CLASSES);
		marshallerActual.afterPropertiesSet();
		serveiDao = mock(ServeiDao.class);
		when(serveiDao.serveiFormatNamespacesScsp4(anyString())).thenReturn(true);
		marshallerNou = new ScspJaxb2Marshaller();
		marshallerNou.setClassesToBeBound(CLASSES);
		marshallerNou.setServeiDao(serveiDao);
		marshallerNou.afterPropertiesSet();
	}

	// ------------------------------------------------------------------------------------------
	// 0) Configuració per servei (PBL_SERVEI_CONFIG.FORMAT_NS_SCSP4)
	// ------------------------------------------------------------------------------------------

	@Test
	public void ambLOpcioDesactivadaLaPeticioEsIdenticaALActual() throws Exception {
		when(serveiDao.serveiFormatNamespacesScsp4(anyString())).thenReturn(false);
		for (Map.Entry<String, Object> cas : casos().entrySet()) {
			assertEquals(cas.getKey(),
					marshalComClienteUnico(marshallerActual, cas.getValue()),
					marshalComClienteUnico(marshallerNou, cas.getValue()));
			StringWriter actual = new StringWriter();
			marshallerActual.marshal(cas.getValue(), new StreamResult(actual));
			StringWriter nou = new StringWriter();
			marshallerNou.marshal(cas.getValue(), new StreamResult(nou));
			assertEquals(cas.getKey(), actual.toString(), nou.toString());
		}
	}

	@Test
	public void senseServeiDaoLaPeticioEsIdenticaALActual() throws Exception {
		ScspJaxb2Marshaller senseDao = new ScspJaxb2Marshaller();
		senseDao.setClassesToBeBound(CLASSES);
		senseDao.afterPropertiesSet();
		for (Map.Entry<String, Object> cas : casos().entrySet()) {
			assertEquals(cas.getKey(),
					marshalComClienteUnico(marshallerActual, cas.getValue()),
					marshalComClienteUnico(senseDao, cas.getValue()));
		}
	}

	@Test
	public void siFallaLaConsultaDeLaConfiguracioEsGeneraComAra() throws Exception {
		when(serveiDao.serveiFormatNamespacesScsp4(anyString())).thenThrow(new RuntimeException("ORA-00904: FORMAT_NS_SCSP4"));
		Object peticion = casos().get("SCDHPAJU");
		assertEquals(
				marshalComClienteUnico(marshallerActual, peticion),
				marshalComClienteUnico(marshallerNou, peticion));
	}

	@Test
	public void elFormatEsDecideixPerServei() throws Exception {
		when(serveiDao.serveiFormatNamespacesScsp4(anyString())).thenReturn(false);
		when(serveiDao.serveiFormatNamespacesScsp4(eq("SCDHPAJU"))).thenReturn(true);
		Map<String, Object> casos = casos();

		String scdhpaju = marshalComClienteUnico(marshallerNou, casos.get("SCDHPAJU"));
		assertEquals("<Peticion xmlns=\"" + NS_PETICION + "\">", primeraEtiqueta(scdhpaju));
		assertTrue(scdhpaju, scdhpaju.contains("<DatosEspecificos xmlns=\"" + NS_DE + "\"><Solicitud>"));

		Object scdcpaju = casos.get("SCDCPAJU");
		String xmlScdcpaju = marshalComClienteUnico(marshallerNou, scdcpaju);
		assertEquals(marshalComClienteUnico(marshallerActual, scdcpaju), xmlScdcpaju);
		assertTrue(xmlScdcpaju, xmlScdcpaju.contains("<ns2:DatosEspecificos>"));

		// SolicitudRespuesta: es decideix pel seu CodigoCertificado
		assertEquals("<SolicitudRespuesta xmlns=\"" + NS_SOLICITUD + "\">",
				primeraEtiqueta(marshalComClienteUnico(marshallerNou, solicitudRespuesta("SCDHPAJU"))));
		SolicitudRespuesta altra = solicitudRespuesta("SVDDGPCIWS02");
		assertEquals(marshalComClienteUnico(marshallerActual, altra), marshalComClienteUnico(marshallerNou, altra));
	}

	@Test
	public void respostesIJustificantsNoConsultenLaConfiguracio() throws Exception {
		Respuesta respuesta = (Respuesta) marshallerNou.createUnmarshaller().unmarshal(parse(respostaSincronaXml()));
		marshalComClienteUnico(marshallerNou, respuesta);
		marshalADocument(marshallerNou, respuesta.getTransmisiones().getTransmisionDatos().get(0));
		org.mockito.Mockito.verifyNoInteractions(serveiDao);
	}

	// ------------------------------------------------------------------------------------------
	// 1) Equivalència amb el marshaller actual (llevat de prefixos i declaracions de namespaces)
	// ------------------------------------------------------------------------------------------

	@Test
	public void peticionsEquivalentsAlMarshallerActual() throws Exception {
		for (Map.Entry<String, Object> cas : casos().entrySet()) {
			String actual = marshalComClienteUnico(marshallerActual, cas.getValue());
			String nou = marshalComClienteUnico(marshallerNou, cas.getValue());
			assertEquivalents(cas.getKey(), actual, nou);
		}
	}

	@Test
	public void laNormalitzacioNomesCanviaPeticionISolicitudRespuesta() throws Exception {
		// Respuesta i TransmisionDatos (JustificanteMaker) han de sortir byte a byte iguals
		Respuesta respuesta = (Respuesta) marshallerActual.createUnmarshaller().unmarshal(
				parse(respostaSincronaXml()));
		assertEquals(
				marshalComClienteUnico(marshallerActual, respuesta),
				marshalComClienteUnico(marshallerNou, respuesta));
		TransmisionDatos transmisionDatos = respuesta.getTransmisiones().getTransmisionDatos().get(0);
		assertEquals(
				marshalADocument(marshallerActual, transmisionDatos),
				marshalADocument(marshallerNou, transmisionDatos));
	}

	// ------------------------------------------------------------------------------------------
	// 2) Forma exacta de la petició
	// ------------------------------------------------------------------------------------------

	@Test
	public void formaDatosEspecificosComAbansDeScsp5() throws Exception {
		Peticion peticion = peticion("SCDHPAJU", "PINBAL00000000000031353388", de(
				"<DatosEspecificos xmlns=\"" + NS_DE + "\"><Solicitud><ProvinciaSolicitud>07</ProvinciaSolicitud>"
						+ "<MunicipioSolicitud>040</MunicipioSolicitud><Titular><Documentacion><Tipo>NIF</Tipo>"
						+ "<Valor>12345678Z</Valor></Documentacion></Titular></Solicitud></DatosEspecificos>"));

		String actual = marshalComClienteUnico(marshallerActual, peticion);
		// Comprova que el test reprodueix el problema
		assertTrue(actual, actual.contains("<ns2:DatosEspecificos>"));
		assertTrue(actual, actual.contains("xmlns:ns5="));

		String xml = marshalComClienteUnico(marshallerNou, peticion);
		assertEquals("<Peticion xmlns=\"" + NS_PETICION + "\">", primeraEtiqueta(xml));
		assertTrue(xml, xml.contains(
				"<DatosEspecificos xmlns=\"" + NS_DE + "\"><Solicitud><ProvinciaSolicitud>07</ProvinciaSolicitud>"
						+ "<MunicipioSolicitud>040</MunicipioSolicitud><Titular><Documentacion><Tipo>NIF</Tipo>"));
		assertFalse(xml, xml.contains("xmlns:ns"));
		assertFalse(xml, Pattern.compile("<ns\\d+:").matcher(xml).find());
		assertEquals("Només dues declaracions: Peticion i DatosEspecificos", 2, compta(xml, "xmlns"));
	}

	@Test
	public void formaDatosEspecificosAmbAtributsIDiversosNamespaces() throws Exception {
		Peticion peticion = peticion("SVDDGPCIWS02", "PINBAL0000000000000000001", de(datosEspecificosComplexos()));
		String xml = marshalComClienteUnico(marshallerNou, peticion);
		assertEquals("<Peticion xmlns=\"" + NS_PETICION + "\">", primeraEtiqueta(xml));
		assertTrue(xml, xml.contains("<DatosEspecificos xmlns=\"" + NS_DE + "\" "));
		assertTrue(xml, xml.contains("<Consulta>"));
		// El namespace propi del subarbre es manté declarat on toca
		assertTrue(xml, xml.contains("<r:Detall xmlns:r=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos/renta2023\""));
		assertFalse(xml, xml.contains("xmlns:ns"));
	}

	@Test
	public void formaPeticionSenseDatosEspecificos() throws Exception {
		String xml = marshalComClienteUnico(marshallerNou, peticion("AEAT103", "PINBAL0000000000000000002", (Element) null));
		assertEquals("<Peticion xmlns=\"" + NS_PETICION + "\">", primeraEtiqueta(xml));
		assertEquals(1, compta(xml, "xmlns"));
	}

	@Test
	public void formaDatosEspecificosBuit() throws Exception {
		String xml = marshalComClienteUnico(marshallerNou, peticion("SVDDGPCIWS02", "PINBAL0000000000000000003",
				de("<DatosEspecificos xmlns=\"" + NS_DE + "\"/>")));
		assertEquals("<Peticion xmlns=\"" + NS_PETICION + "\">", primeraEtiqueta(xml));
		assertTrue(xml, xml.contains("<DatosEspecificos xmlns=\"" + NS_DE + "\"/>"));
		assertEquals(2, compta(xml, "xmlns"));
	}

	@Test
	public void formaSolicitudRespuesta() throws Exception {
		String actual = marshalComClienteUnico(marshallerActual, solicitudRespuesta("SVDDGPCIWS02"));
		assertTrue(actual, actual.contains("xmlns:ns5="));
		String xml = marshalComClienteUnico(marshallerNou, solicitudRespuesta("SVDDGPCIWS02"));
		assertEquals("<SolicitudRespuesta xmlns=\"" + NS_SOLICITUD + "\">", primeraEtiqueta(xml));
		assertEquals(1, compta(xml, "xmlns"));
	}

	@Test
	public void formaPeticionAmbDiversesSolicituds() throws Exception {
		String xml = marshalComClienteUnico(marshallerNou, peticionMultiple());
		assertEquals("<Peticion xmlns=\"" + NS_PETICION + "\">", primeraEtiqueta(xml));
		assertEquals(3, compta(xml, "<DatosEspecificos xmlns=\"" + NS_DE + "\""));
		assertEquals(4, compta(xml, "xmlns"));
	}

	// ------------------------------------------------------------------------------------------
	// 3) Validació contra els XSD dels serveis (com SchemaValidator)
	// ------------------------------------------------------------------------------------------

	@Test
	public void peticionsValidenContraXsd() throws Exception {
		Map<String, Object> casos = casos();
		String[][] validacions = {
				{ "SCDHPAJU", "SCDHPAJUv3", "peticion.xsd" },
				{ "SCDHPAJU (XmlHelper)", "SCDHPAJUv3", "peticion.xsd" },
				{ "SCDCPAJU", "SCDCPAJUv3", "peticion.xsd" },
				{ "AEAT103 sense DatosEspecificos", "AEAT103v3", "peticion.xsd" },
				{ "DGP SVDDGPCIWS02", "SVDDGPCIWS02v3", "peticion.xsd" },
				{ "DGP DatosEspecificos buit", "SVDDGPCIWS02v3", "peticion.xsd" },
				{ "DGP DatosEspecificos amb atribut Id", "SVDDGPCIWS02v3", "peticion.xsd" },
				{ "Asíncrona amb 3 sol·licituds", "SVDDGPCIWS02v3", "peticion.xsd" },
				{ "SolicitudRespuesta", "SVDDGPCIWS02v3", "solicitud-respuesta.xsd" } };
		for (String[] v : validacions) {
			Object objecte = casos.get(v[0]);
			assertNotNull(v[0], objecte);
			validar(v[0], v[1], v[2], parse(marshalComClienteUnico(marshallerNou, objecte)));
			// Control: el marshaller actual també valida (el test no és permissiu per defecte)
			validar(v[0] + " (actual)", v[1], v[2], parse(marshalComClienteUnico(marshallerActual, objecte)));
		}
	}

	@Test(expected = AssertionError.class)
	public void laValidacioXsdDetectaErrors() throws Exception {
		Peticion peticion = peticion("SCDHPAJU", "PINBAL0000000000000000009",
				de("<DatosEspecificos xmlns=\"" + NS_DE + "\"><Solicitud><Inexistent/></Solicitud></DatosEspecificos>"));
		validar("control", "SCDHPAJUv3", "peticion.xsd", parse(marshalComClienteUnico(marshallerNou, peticion)));
	}

	// ------------------------------------------------------------------------------------------
	// 4) Signatura XMLDSig de la llibreria (Body Id="MsgBody", c14n inclusiva)
	// ------------------------------------------------------------------------------------------

	@Test
	public void signaturaEsGeneraIVerifica() throws Exception {
		for (Map.Entry<String, Object> cas : casos().entrySet()) {
			Document envelope = envelope(marshalADom(marshallerNou, cas.getValue()));
			new XMLDSigManager().generateSignature(envelope, certificat, keyPair.getPrivate());
			String signat = toString(envelope);
			assertTrue(cas.getKey(), signat.contains("Id=\"MsgBody\""));
			assertTrue(cas.getKey(), signat.contains("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments"));
			// Verificació sobre el missatge rebut (reparsejat), com fa el receptor
			Document rebut = parse(signat);
			assertEquals(cas.getKey(), certificat, new XMLDSigManager().validateSignature(rebut));
			// Un intermediari que reenvia el missatge (parse + serialització) no invalida la signatura
			Document reenviat = parse(toString(parse(signat)));
			assertEquals(cas.getKey(), certificat, new XMLDSigManager().validateSignature(reenviat));
		}
	}

	@Test
	public void signaturaDetectaModificacionsDelBody() throws Exception {
		Document envelope = envelope(marshalADom(marshallerNou, casos().get("SCDHPAJU")));
		new XMLDSigManager().generateSignature(envelope, certificat, keyPair.getPrivate());
		Document rebut = parse(toString(envelope).replace(">07<", ">08<"));
		try {
			new XMLDSigManager().validateSignature(rebut);
			fail("La signatura no hauria de validar");
		} catch (ScspException expected) {
			assertEquals("0305", expected.getScspCode());
		}
	}

	// ------------------------------------------------------------------------------------------
	// 5) Flux real de ClienteUnico (marshal de la petició + unmarshal de respostes)
	// ------------------------------------------------------------------------------------------

	@Test
	public void clienteUnicoSincronaEnviaFormaNovaILlegeixRespuesta() throws Exception {
		ClienteUnicoProva prova = new ClienteUnicoProva(parse(respostaSincronaXml()).getDocumentElement());
		Peticion peticion = (Peticion) casos().get("SCDHPAJU");
		Respuesta respuesta = prova.clienteUnico.realizaPeticionSincrona(peticion);

		String enviat = toString(prova.requestEnviat());
		assertEquals("<Peticion xmlns=\"" + NS_PETICION + "\">", primeraEtiqueta(enviat));
		assertTrue(enviat, enviat.contains("<DatosEspecificos xmlns=\"" + NS_DE + "\"><Solicitud>"));
		assertEquivalents("ClienteUnico síncrona", marshalComClienteUnico(marshallerActual, peticion), enviat);

		assertEquals("0003", respuesta.getAtributos().getEstado().getCodigoEstado());
		Object datosEspecificos = respuesta.getTransmisiones().getTransmisionDatos().get(0).getDatosEspecificos();
		assertTrue(String.valueOf(datosEspecificos), datosEspecificos instanceof Element);
		Element de = (Element) datosEspecificos;
		assertEquals("DatosEspecificos", de.getLocalName());
		assertEquals("0003", de.getElementsByTagNameNS(NS_DE, "CodigoEstado").item(0).getTextContent());
		assertEquals("Manacor", de.getElementsByTagNameNS(NS_DE, "Nombre").item(0).getTextContent());
	}

	@Test
	public void clienteUnicoAsincronaISolicitudRespuesta() throws Exception {
		ClienteUnicoProva prova = new ClienteUnicoProva(parse(confirmacionXml()).getDocumentElement());
		Peticion peticion = peticionMultiple();
		ConfirmacionPeticion confirmacion = prova.clienteUnico.realizaPeticionAsincrona(peticion);
		assertEquals("0002", confirmacion.getAtributos().getEstado().getCodigoEstado());
		String enviat = toString(prova.requestEnviat());
		assertEquals("<Peticion xmlns=\"" + NS_PETICION + "\">", primeraEtiqueta(enviat));
		assertEquivalents("ClienteUnico asíncrona", marshalComClienteUnico(marshallerActual, peticion), enviat);

		// Solicitud de resposta
		ClienteUnicoProva provaSolicitud = new ClienteUnicoProva(parse(respostaSincronaXml()).getDocumentElement());
		PeticionRespuesta peticionRespuesta = new PeticionRespuesta();
		peticionRespuesta.setEstado("0002");
		when(provaSolicitud.peticionRespuestaDao.select(anyString())).thenReturn(peticionRespuesta);
		es.scsp.common.domain.core.Transmision transmision = new es.scsp.common.domain.core.Transmision();
		transmision.setIdSolicitante("S0711001H");
		when(provaSolicitud.transmisionDao.select(any(PeticionRespuesta.class))).thenReturn(Collections.singletonList(transmision));
		SolicitudRespuesta solicitud = solicitudRespuesta("SCDHPAJU");
		Respuesta respuesta = provaSolicitud.clienteUnico.realizaSolicitudRespuesta(solicitud);
		assertEquals("0003", respuesta.getAtributos().getEstado().getCodigoEstado());
		String enviatSolicitud = toString(provaSolicitud.requestEnviat());
		assertEquals("<SolicitudRespuesta xmlns=\"" + NS_SOLICITUD + "\">", primeraEtiqueta(enviatSolicitud));
		assertEquivalents("ClienteUnico solicitud", marshalComClienteUnico(marshallerActual, solicitud), enviatSolicitud);
	}

	@Test
	public void unmarshalDeRespuestaIConfirmacionNoCanvia() throws Exception {
		for (String xml : new String[] { respostaSincronaXml(), confirmacionXml() }) {
			Object actual = marshallerActual.createUnmarshaller().unmarshal(parse(xml));
			Object nou = marshallerNou.createUnmarshaller().unmarshal(parse(xml));
			assertEquals(actual.getClass(), nou.getClass());
			assertEquals(marshalComClienteUnico(marshallerActual, actual), marshalComClienteUnico(marshallerActual, nou));
			// I via Spring OXM (com MessageRecover/LoadContextReceiver)
			Object oxm = marshallerNou.unmarshal(new DOMSource(parse(xml)));
			assertEquals(marshalComClienteUnico(marshallerActual, actual), marshalComClienteUnico(marshallerActual, oxm));
		}
	}

	@Test
	public void marshalViaSpringOxmIStreamTambeNormalitza() throws Exception {
		Peticion peticion = (Peticion) casos().get("SCDHPAJU");
		StringWriter writer = new StringWriter();
		marshallerNou.marshal(peticion, new StreamResult(writer));
		String xml = writer.toString();
		assertEquals("<Peticion xmlns=\"" + NS_PETICION + "\">", primeraEtiqueta(xml));
		assertTrue(xml, xml.contains("<DatosEspecificos xmlns=\"" + NS_DE + "\">"));
		assertEquivalents("Spring OXM stream", marshalComClienteUnico(marshallerActual, peticion), xml);
	}

	// ------------------------------------------------------------------------------------------
	// Casos
	// ------------------------------------------------------------------------------------------

	private Map<String, Object> casos() throws Exception {
		Map<String, Object> casos = new LinkedHashMap<>();
		casos.put("SCDHPAJU", peticion("SCDHPAJU", "PINBAL00000000000031353388", de(
				"<DatosEspecificos xmlns=\"" + NS_DE + "\"><Solicitud><ProvinciaSolicitud>07</ProvinciaSolicitud>"
						+ "<MunicipioSolicitud>040</MunicipioSolicitud><Titular><Documentacion><Tipo>NIF</Tipo>"
						+ "<Valor>12345678Z</Valor></Documentacion></Titular><NumeroAnyos>5</NumeroAnyos></Solicitud>"
						+ "</DatosEspecificos>")));
		casos.put("SCDHPAJU (XmlHelper)", peticion("SCDHPAJU", "PINBAL00000000000031353389", dadesXmlHelper()));
		casos.put("SCDCPAJU", peticion("SCDCPAJU", "PINBAL00000000000031353390", de(
				"<DatosEspecificos xmlns=\"" + NS_DE + "\">\n  <Solicitud>\n    <ProvinciaSolicitud>07</ProvinciaSolicitud>\n"
						+ "    <MunicipioSolicitud>033</MunicipioSolicitud>\n    <Titular><Documentacion><Tipo>NIE</Tipo>"
						+ "<Valor>X1234567L</Valor></Documentacion></Titular>\n  </Solicitud>\n</DatosEspecificos>")));
		casos.put("AEAT103 sense DatosEspecificos", peticion("AEAT103", "PINBAL00000000000031353391", (Element) null));
		casos.put("DGP SVDDGPCIWS02", peticion("SVDDGPCIWS02", "PINBAL00000000000031353392", de(
				"<DatosEspecificos xmlns=\"" + NS_DE + "\"><Consulta><NumeroSoporte>AAA123456</NumeroSoporte></Consulta></DatosEspecificos>")));
		casos.put("DGP DatosEspecificos buit", peticion("SVDDGPCIWS02", "PINBAL00000000000031353393",
				de("<DatosEspecificos xmlns=\"" + NS_DE + "\"/>")));
		casos.put("DGP DatosEspecificos amb atribut Id", peticion("SVDDGPCIWS02", "PINBAL00000000000031353394", de(
				"<DatosEspecificos xmlns=\"" + NS_DE + "\" Id=\"DE1\"><Consulta><NumeroSoporte>E12345678</NumeroSoporte></Consulta></DatosEspecificos>")));
		casos.put("Complexa (diversos nivells, atributs, namespaces)", peticion("SVDDGPCIWS02", "PINBAL00000000000031353395",
				de(datosEspecificosComplexos())));
		casos.put("DatosEspecificos amb prefix propi", peticion("SCDHPAJU", "PINBAL00000000000031353396", de(
				"<de:DatosEspecificos xmlns:de=\"" + NS_DE + "\"><de:Solicitud><de:ProvinciaSolicitud>07</de:ProvinciaSolicitud>"
						+ "</de:Solicitud></de:DatosEspecificos>")));
		casos.put("DatosEspecificos v2 (map.es)", peticion("SVDCDYGWS01", "PINBAL00000000000031353397", de(
				"<DatosEspecificos xmlns=\"http://www.map.es/scsp/esquemas/datosespecificos\"><Solicitud><Dato>1</Dato></Solicitud></DatosEspecificos>")));
		casos.put("Fills sense namespace", peticion("SCDHPAJU", "PINBAL00000000000031353398", de(
				"<DatosEspecificos xmlns=\"" + NS_DE + "\"><Solicitud xmlns=\"\"><Valor>1</Valor></Solicitud></DatosEspecificos>")));
		casos.put("Asíncrona amb 3 sol·licituds", peticionMultiple());
		casos.put("SolicitudRespuesta", solicitudRespuesta("SVDDGPCIWS02"));
		return casos;
	}

	private String datosEspecificosComplexos() {
		return "<DatosEspecificos xmlns=\"" + NS_DE + "\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Id=\"DE-1\">"
				+ "<Consulta>"
				+ "<NumeroSoporte>AAA123456</NumeroSoporte>"
				+ "<Nivell1 codi=\"A\" ordre=\"1\"><Nivell2><Nivell3 xsi:nil=\"true\"/><Nivell3 valor=\"x &amp; y\">Text &lt;escapat&gt;</Nivell3></Nivell2></Nivell1>"
				+ "<r:Detall xmlns:r=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos/renta2023\" r:tipus=\"T\">"
				+ "<r:Import>1.000,00</r:Import><Normal>sense prefix</Normal></r:Detall>"
				+ "<!-- comentari -->"
				+ "</Consulta></DatosEspecificos>";
	}

	private Element dadesXmlHelper() throws Exception {
		Servicio servicio = new Servicio();
		servicio.setCodCertificado("SCDHPAJU");
		servicio.setVersionEsquema("V3");
		Map<String, Object> dades = new HashMap<>();
		dades.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
		dades.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "040");
		dades.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "NIF");
		dades.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "12345678Z");
		Element element = new XmlHelper().crearDadesEspecifiques(servicio, dades, false, false, null, true);
		assertNotNull(element);
		assertEquals(NS_DE, element.getNamespaceURI());
		return element;
	}

	private Peticion peticionMultiple() throws Exception {
		return peticion("SVDDGPCIWS02", "PINBAL00000000000031353399",
				de("<DatosEspecificos xmlns=\"" + NS_DE + "\"><Consulta><NumeroSoporte>AAA000001</NumeroSoporte></Consulta></DatosEspecificos>"),
				de("<DatosEspecificos xmlns=\"" + NS_DE + "\"><Consulta><NumeroSoporte>AAA000002</NumeroSoporte></Consulta></DatosEspecificos>"),
				de("<DatosEspecificos xmlns=\"" + NS_DE + "\" Id=\"3\"><Consulta/></DatosEspecificos>"));
	}

	private static Peticion peticion(String codi, String idPeticion, Element... datosEspecificos) {
		Peticion peticion = new Peticion();
		Atributos atributos = new Atributos();
		atributos.setIdPeticion(idPeticion);
		atributos.setNumElementos(datosEspecificos.length);
		atributos.setTimeStamp("2026-11-25T10:15:30.123+01:00");
		atributos.setCodigoCertificado(codi);
		peticion.setAtributos(atributos);
		Solicitudes solicitudes = new Solicitudes();
		for (int i = 0; i < datosEspecificos.length; i++) {
			SolicitudTransmision st = new SolicitudTransmision();
			DatosGenericos dg = new DatosGenericos();
			Emisor emisor = new Emisor();
			emisor.setNifEmisor(codi.startsWith("SVDDGP") ? "S2816015H" : "S2833002E");
			emisor.setNombreEmisor("Emissor de proves");
			dg.setEmisor(emisor);
			Solicitante solicitante = new Solicitante();
			solicitante.setIdentificadorSolicitante("S0711001H");
			solicitante.setNombreSolicitante("Govern de les Illes Balears");
			solicitante.setUnidadTramitadora("Unitat & tramitadora");
			Procedimiento procedimiento = new Procedimiento();
			procedimiento.setCodProcedimiento("PROC1");
			procedimiento.setNombreProcedimiento("Procediment de proves");
			solicitante.setProcedimiento(procedimiento);
			solicitante.setFinalidad("Finalitat de proves");
			solicitante.setConsentimiento(Consentimiento.SI);
			Funcionario funcionario = new Funcionario();
			funcionario.setNombreCompletoFuncionario("Funcionari de proves");
			funcionario.setNifFuncionario("12345678Z");
			solicitante.setFuncionario(funcionario);
			solicitante.setIdExpediente("EXP/2026/1");
			dg.setSolicitante(solicitante);
			Titular titular = new Titular();
			titular.setTipoDocumentacion(codi.startsWith("SVDDGP") ? TipoDocumentacion.DNI : TipoDocumentacion.NIF);
			titular.setDocumentacion("12345678Z");
			titular.setNombre("Nom");
			titular.setApellido1("Llinatge1");
			titular.setApellido2("Llinatge2");
			dg.setTitular(titular);
			Transmision transmision = new Transmision();
			transmision.setCodigoCertificado(codi);
			transmision.setIdSolicitud(datosEspecificos.length == 1 ? idPeticion : String.format("%06d", i + 1));
			transmision.setFechaGeneracion("2026-11-25T10:15:30.123+01:00");
			dg.setTransmision(transmision);
			st.setDatosGenericos(dg);
			if (datosEspecificos[i] != null) {
				st.setDatosEspecificos(datosEspecificos[i]);
			}
			solicitudes.getSolicitudTransmision().add(st);
		}
		peticion.setSolicitudes(solicitudes);
		return peticion;
	}

	private static SolicitudRespuesta solicitudRespuesta(String codi) {
		SolicitudRespuesta solicitud = new SolicitudRespuesta();
		es.scsp.bean.common.solicitud.Atributos atributos = new es.scsp.bean.common.solicitud.Atributos();
		atributos.setIdPeticion("PINBAL00000000000031353399");
		atributos.setNumElementos(3);
		atributos.setTimeStamp("2026-11-25T10:20:00.000+01:00");
		atributos.setCodigoCertificado(codi);
		solicitud.setAtributos(atributos);
		return solicitud;
	}

	private static String respostaSincronaXml() {
		return "<Respuesta xmlns=\"" + NS_RESPUESTA + "\"><Atributos><IdPeticion>PINBAL00000000000031353388</IdPeticion>"
				+ "<NumElementos>1</NumElementos><TimeStamp>2026-11-25T10:15:31.000+01:00</TimeStamp>"
				+ "<Estado><CodigoEstado>0003</CodigoEstado><LiteralError>TRAMITADA</LiteralError>"
				+ "<TiempoEstimadoRespuesta>0</TiempoEstimadoRespuesta></Estado><CodigoCertificado>SCDHPAJU</CodigoCertificado>"
				+ "</Atributos><Transmisiones><TransmisionDatos><DatosGenericos><Emisor><NifEmisor>S2833002E</NifEmisor>"
				+ "<NombreEmisor>Emissor</NombreEmisor></Emisor><Solicitante><IdentificadorSolicitante>S0711001H</IdentificadorSolicitante>"
				+ "<NombreSolicitante>CAIB</NombreSolicitante><Finalidad>Proves</Finalidad><Consentimiento>Si</Consentimiento>"
				+ "</Solicitante><Titular><TipoDocumentacion>NIF</TipoDocumentacion><Documentacion>12345678Z</Documentacion></Titular>"
				+ "<Transmision><CodigoCertificado>SCDHPAJU</CodigoCertificado><IdSolicitud>PINBAL00000000000031353388</IdSolicitud>"
				+ "<IdTransmision>T0001</IdTransmision><FechaGeneracion>2026-11-25T10:15:31.000+01:00</FechaGeneracion>"
				+ "</Transmision></DatosGenericos><DatosEspecificos xmlns=\"" + NS_DE + "\"><Estado><CodigoEstado>0003</CodigoEstado>"
				+ "<LiteralError>TRAMITADA</LiteralError></Estado><Solicitud><ProvinciaSolicitud>07</ProvinciaSolicitud>"
				+ "<MunicipioSolicitud>040</MunicipioSolicitud></Solicitud><Resultado><MunicipioRespuesta><Codigo>040</Codigo>"
				+ "<Nombre>Manacor</Nombre></MunicipioRespuesta></Resultado></DatosEspecificos></TransmisionDatos></Transmisiones>"
				+ "</Respuesta>";
	}

	private static String confirmacionXml() {
		return "<ConfirmacionPeticion xmlns=\"" + NS_CONFIRMACION + "\"><Atributos>"
				+ "<IdPeticion>PINBAL00000000000031353399</IdPeticion><NumElementos>3</NumElementos>"
				+ "<TimeStamp>2026-11-25T10:15:31.000+01:00</TimeStamp><Estado><CodigoEstado>0002</CodigoEstado>"
				+ "<LiteralError>EN PROCESO</LiteralError><TiempoEstimadoRespuesta>1</TiempoEstimadoRespuesta></Estado>"
				+ "<CodigoCertificado>SVDDGPCIWS02</CodigoCertificado></Atributos></ConfirmacionPeticion>";
	}

	// ------------------------------------------------------------------------------------------
	// Utilitats
	// ------------------------------------------------------------------------------------------

	/** ClienteUnico real amb el BasicServiceClientSpring mockejat per capturar la petició. */
	private class ClienteUnicoProva {
		final ClienteUnico clienteUnico = new ClienteUnico();
		final BasicServiceClientSpring basicServiceClient = mock(BasicServiceClientSpring.class);
		final ServiceManager serviceManager = mock(ServiceManager.class);
		final PeticionRespuestaDao peticionRespuestaDao = mock(PeticionRespuestaDao.class);
		final TransmisionDao transmisionDao = mock(TransmisionDao.class);
		final ArgumentCaptor<Element> request = ArgumentCaptor.forClass(Element.class);

		ClienteUnicoProva(Element resposta) throws Exception {
			setField("jaxb2Marshaller", marshallerNou);
			setField("basicServiceClient", basicServiceClient);
			setField("serviceManager", serviceManager);
			setField("peticionRespuestaDao", peticionRespuestaDao);
			setField("transmisionDao", transmisionDao);
			Servicio servicio = new Servicio();
			servicio.setCodCertificado("PROVA");
			when(serviceManager.selectServicio(anyString())).thenReturn(servicio);
			when(basicServiceClient.sendReceive(
					request.capture(), any(), any(), any(), any(), any(), any(), anyBoolean())).thenReturn(resposta);
		}

		Element requestEnviat() {
			return request.getValue();
		}

		private void setField(String nom, Object valor) throws Exception {
			Field field = ClienteUnico.class.getDeclaredField(nom);
			field.setAccessible(true);
			field.set(clienteUnico, valor);
		}
	}

	/** Marshal tal com ho fa ClienteUnico, serialitzat com BasicServiceClientSpring.sendReceive. */
	private static String marshalComClienteUnico(Jaxb2Marshaller marshaller, Object objecte) throws Exception {
		return toString(marshalADom(marshaller, objecte));
	}

	private static Element marshalADom(Jaxb2Marshaller marshaller, Object objecte) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = dbf.newDocumentBuilder().newDocument();
		marshaller.createMarshaller().marshal(objecte, new DOMResult(doc));
		return doc.getDocumentElement();
	}

	/** Com JustificanteMaker.getOMElementRespuesta: marshal(objecte, Node). */
	private static String marshalADocument(Jaxb2Marshaller marshaller, Object objecte) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		marshaller.createMarshaller().marshal(objecte, doc);
		return toString(doc);
	}

	/** Missatge SOAP amb la petició al Body, com fa Spring-WS a partir del DOMSource. */
	private static Document envelope(Element peticio) throws Exception {
		Document envelope = parse("<soapenv:Envelope xmlns:soapenv=\"" + NS_SOAP + "\"><soapenv:Body/></soapenv:Envelope>");
		Node body = envelope.getElementsByTagNameNS(NS_SOAP, "Body").item(0);
		TransformerFactory.newInstance().newTransformer().transform(new DOMSource(peticio), new DOMResult(body));
		// Al fil, com el missatge que arriba a l'interceptor de signatura
		return parse(toString(envelope));
	}

	private static void assertEquivalents(String cas, String actual, String nou) {
		Diff diff = DiffBuilder.compare(actual)
				.withTest(nou)
				.withDifferenceEvaluator(DifferenceEvaluators.chain(
						DifferenceEvaluators.Default,
						(comparison, outcome) -> comparison.getType() == ComparisonType.NAMESPACE_PREFIX
								? ComparisonResult.EQUAL
								: outcome))
				.checkForIdentical()
				.build();
		assertFalse(cas + ": " + diff + "\nactual: " + actual + "\nnou:    " + nou, diff.hasDifferences());
	}

	private static void validar(String cas, String directori, String xsd, Document document) {
		String path = "schemas/" + directori + "/" + xsd;
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
			assertNotNull(path, is);
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			sf.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", true);
			sf.setResourceResolver(new SchemaValidatorVfsCompat.ClasspathDirectoryResolver("/schemas/" + directori + "/"));
			sf.newSchema(new StreamSource(is)).newValidator().validate(new DOMSource(document.getDocumentElement()));
		} catch (Exception ex) {
			throw new AssertionError(cas + " no valida contra " + path + ": " + ex.getMessage(), ex);
		}
	}

	private static Element de(String xml) throws Exception {
		return parse(xml).getDocumentElement();
	}

	private static Document parse(String xml) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
	}

	private static String toString(Node node) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TransformerFactory.newInstance().newTransformer().transform(new DOMSource(node), new StreamResult(out));
		return new String(out.toByteArray(), StandardCharsets.UTF_8);
	}

	private static String primeraEtiqueta(String xml) {
		Matcher m = Pattern.compile("<[A-Za-z][^>]*>").matcher(xml.replaceFirst("^<\\?xml[^>]*\\?>", ""));
		assertTrue(xml, m.find());
		return m.group();
	}

	private static int compta(String text, String subcadena) {
		int n = 0;
		for (int i = text.indexOf(subcadena); i >= 0; i = text.indexOf(subcadena, i + subcadena.length())) {
			n++;
		}
		return n;
	}


}
