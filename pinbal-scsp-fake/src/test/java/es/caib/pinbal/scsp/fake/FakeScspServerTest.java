package es.caib.pinbal.scsp.fake;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Prova d'integració HTTP real contra {@link FakeScspServer}: verifica que
 * les respostes es resolen correctament per a serveis amb esquemes de
 * petició diferents (V3 amb {@code Documentacion} com a text directe, i V2
 * amb {@code Documentacion/Valor} dins de {@code DatosEspecificos}, com
 * SCDCPAJU), i que el disparador d'error simulat funciona en tots dos casos.
 */
public class FakeScspServerTest {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 18199;
	private static final String BASE_URL = "http://" + HOST + ":" + PORT;
	private static final HttpClient CLIENT = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(5))
		.build();

	@BeforeClass
	public static void startServer() throws Exception {
		System.setProperty("fake.scsp.host", HOST);
		System.setProperty("fake.scsp.port", String.valueOf(PORT));
		FakeScspServer.main(new String[0]);
		waitUntilReady();
	}

	@AfterClass
	public static void clearProperties() {
		System.clearProperty("fake.scsp.host");
		System.clearProperty("fake.scsp.port");
	}

	private static void waitUntilReady() throws Exception {
		for (int i = 0; i < 20; i++) {
			try {
				HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/__fake-scsp"))
					.timeout(Duration.ofSeconds(5))
					.GET()
					.build();
				HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
				if (response.statusCode() == 200) {
					return;
				}
			} catch (IOException e) {
				// encara no està a punt
			}
			Thread.sleep(100);
		}
		throw new IllegalStateException("El fake SCSP no ha arrencat a temps");
	}

	private static HttpResponse<String> send(String path, String body) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(path))
			.timeout(Duration.ofSeconds(5))
			.POST(HttpRequest.BodyPublishers.ofString(body))
			.build();
		return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private static String peticionV3(String codigoCertificado, String documentacio) {
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soapenv:Body><Peticion xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/peticion\">"
			+ "<Atributos><IdPeticion>TEST00001</IdPeticion><NumElementos>1</NumElementos>"
			+ "<CodigoCertificado>" + codigoCertificado + "</CodigoCertificado></Atributos>"
			+ "<Solicitudes><SolicitudTransmision><DatosGenericos>"
			+ "<Titular><TipoDocumentacion>NIF</TipoDocumentacion><Documentacion>" + documentacio + "</Documentacion></Titular>"
			+ "<Transmision><CodigoCertificado>" + codigoCertificado + "</CodigoCertificado>"
			+ "<IdSolicitud>TEST00001</IdSolicitud></Transmision>"
			+ "</DatosGenericos></SolicitudTransmision></Solicitudes></Peticion></soapenv:Body></soapenv:Envelope>";
	}

	private static String peticionScdcpaju(String documentacio) {
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soapenv:Body><Peticion xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/peticion\">"
			+ "<Atributos><IdPeticion>TEST00002</IdPeticion><NumElementos>1</NumElementos>"
			+ "<CodigoCertificado>SCDCPAJU</CodigoCertificado></Atributos>"
			+ "<Solicitudes><SolicitudTransmision><DatosGenericos>"
			+ "<Titular/>"
			+ "<Transmision><CodigoCertificado>SCDCPAJU</CodigoCertificado>"
			+ "<IdSolicitud>TEST00002</IdSolicitud></Transmision>"
			+ "</DatosGenericos>"
			+ "<DatosEspecificos xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">"
			+ "<Solicitud><Titular><Documentacion><Tipo>NIF</Tipo><Valor>" + documentacio + "</Valor></Documentacion></Titular></Solicitud>"
			+ "</DatosEspecificos>"
			+ "</SolicitudTransmision></Solicitudes></Peticion></soapenv:Body></soapenv:Envelope>";
	}

	@Test
	public void shouldReturnSuccessCodeForOrdinaryRequest_v3Schema() throws Exception {
		HttpResponse<String> response = send(BASE_URL + "/servicios/fake", peticionV3("Q2827003ATGSS001", "43102532A"));
		assertTrue("s'esperava CodigoEstado 0003, resposta: " + response.body(),
			response.body().contains("<CodigoEstado>0003</CodigoEstado>"));
	}

	@Test
	public void shouldSimulateErrorWhenTitularDocMatchesTrigger_v3Schema() throws Exception {
		HttpResponse<String> response = send(BASE_URL + "/servicios/fake", peticionV3("Q2827003ATGSS001", "00000000ERR"));
		assertTrue("s'esperava CodigoEstado 9999, resposta: " + response.body(),
			response.body().contains("<CodigoEstado>9999</CodigoEstado>"));
	}

	@Test
	public void shouldReturnSuccessCodeForOrdinaryRequest_v2SchemaWithNestedDocumentacion() throws Exception {
		HttpResponse<String> response = send(BASE_URL + "/servicios/fake", peticionScdcpaju("41510455D"));
		assertTrue("s'esperava CodigoEstado 0003, resposta: " + response.body(),
			response.body().contains("<CodigoEstado>0003</CodigoEstado>"));
	}

	@Test
	public void shouldSimulateErrorWhenTitularDocMatchesTrigger_v2SchemaWithNestedDocumentacion() throws Exception {
		HttpResponse<String> response = send(BASE_URL + "/servicios/fake", peticionScdcpaju("00000000ERR"));
		assertTrue("s'esperava CodigoEstado 9999, resposta: " + response.body(),
			response.body().contains("<CodigoEstado>9999</CodigoEstado>"));
	}

	/**
	 * SCDCPAJU es consumeix amb XMLSignature (no WS-Security): el client SCSP
	 * valida el PRIMER element Signature del document contra l'Id del Body.
	 * La resposta, que el fake modifica (IdPeticion, IdSolicitud...), ha de
	 * portar una signatura XMLDSig nova i vàlida, no la de la plantilla.
	 */
	@Test
	public void shouldReturnValidXmlDsigSignature_whenClientUsesXmlDsig() throws Exception {
		String request = peticionScdcpaju("41510455D").replace(
			"<soapenv:Body>",
			"<soapenv:Header><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"/></soapenv:Header>"
				+ "<soapenv:Body Id=\"MsgBody\">");
		HttpResponse<String> response = send(BASE_URL + "/servicios/SCDCPAJU/sincrona", request);
		assertEquals(200, response.statusCode());
		assertFirstSignatureIsValidXmlDsigOverBody(response.body());
	}

	@Test
	public void shouldUseTemplateSignatureStyle_whenRequestIsUnsigned() throws Exception {
		HttpResponse<String> response = send(BASE_URL + "/servicios/SCDCPAJU/sincrona", peticionScdcpaju("41510455D"));
		assertEquals(200, response.statusCode());
		assertFirstSignatureIsValidXmlDsigOverBody(response.body());
	}

	@Test
	public void shouldKeepWsSecurity_forWssServices() throws Exception {
		HttpResponse<String> response = send(BASE_URL + "/servicios/fake", peticionV3("Q2827003ATGSS001", "43102532A"));
		Document document = parseNamespaceAware(response.body());
		Element header = directChild(document.getDocumentElement(), "Header");
		assertTrue("s'esperava wsse:Security a la capçalera", directChild(header, "Security") != null);
		assertTrue("no hi hauria d'haver cap ds:Signature directa a la capçalera", directChild(header, "Signature") == null);
	}

	// ------------------------- SVDDGPCIWS02 asíncron (sense plantilles asíncrones) -------------------------

	private static String peticionDgp(String idPeticion, String... documents) {
		StringBuilder solicituds = new StringBuilder();
		for (int i = 0; i < documents.length; i++) {
			String idSolicitud = documents.length == 1 ? idPeticion : String.format("%06d", i + 1);
			solicituds.append("<SolicitudTransmision><DatosGenericos>")
				.append("<Titular><TipoDocumentacion>DNI</TipoDocumentacion><Documentacion>").append(documents[i])
				.append("</Documentacion></Titular>")
				.append("<Transmision><CodigoCertificado>SVDDGPCIWS02</CodigoCertificado>")
				.append("<IdSolicitud>").append(idSolicitud).append("</IdSolicitud></Transmision>")
				.append("</DatosGenericos></SolicitudTransmision>");
		}
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soapenv:Body><Peticion xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/peticion\">"
			+ "<Atributos><IdPeticion>" + idPeticion + "</IdPeticion><NumElementos>" + documents.length + "</NumElementos>"
			+ "<CodigoCertificado>SVDDGPCIWS02</CodigoCertificado></Atributos>"
			+ "<Solicitudes>" + solicituds + "</Solicitudes></Peticion></soapenv:Body></soapenv:Envelope>";
	}

	private static String solicitudRespuestaDgp(String idPeticion, int numElementos) {
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>"
			+ "<SolicitudRespuesta xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/solicitudRespuesta\">"
			+ "<Atributos><IdPeticion>" + idPeticion + "</IdPeticion><NumElementos>" + numElementos + "</NumElementos>"
			+ "<CodigoCertificado>SVDDGPCIWS02</CodigoCertificado></Atributos>"
			+ "</SolicitudRespuesta></soapenv:Body></soapenv:Envelope>";
	}

	private static java.util.List<String> textsByLocalName(Document document, String localName) {
		java.util.List<String> values = new java.util.ArrayList<String>();
		NodeList nodes = document.getElementsByTagNameNS("*", localName);
		for (int i = 0; i < nodes.getLength(); i++) {
			values.add(nodes.item(i).getTextContent().trim());
		}
		return values;
	}

	@Test
	public void svddgpciws02_peticioAsincrona_retornaConfirmacioGenerada() throws Exception {
		HttpResponse<String> response = send(BASE_URL + "/servicios/SVDDGPCIWS02/asincrona",
			peticionDgp("PINBALASYNC0001", "12345678Z", "00000000T"));
		assertEquals(200, response.statusCode());
		Document document = parseNamespaceAware(response.body());
		Element body = directChild(document.getDocumentElement(), "Body");
		Element root = (Element) body.getElementsByTagNameNS("*", "ConfirmacionPeticion").item(0);
		assertTrue("s'esperava una ConfirmacionPeticion, resposta: " + response.body(), root != null);
		assertEquals("PINBALASYNC0001", textsByLocalName(document, "IdPeticion").get(0));
		assertEquals("2", textsByLocalName(document, "NumElementos").get(0));
		assertEquals("0002", textsByLocalName(document, "CodigoEstado").get(0));
		assertEquals("SVDDGPCIWS02", textsByLocalName(document, "CodigoCertificado").get(0));
		Element header = directChild(document.getDocumentElement(), "Header");
		assertTrue("la confirmació ha d'anar signada", directChild(header, "Security") != null);
	}

	@Test
	public void svddgpciws02_solicitudRespuestaMultiple_retornaUnaTransmissioPerSollicitud() throws Exception {
		HttpResponse<String> response = send(BASE_URL + "/servicios/SVDDGPCIWS02/asincrona",
			solicitudRespuestaDgp("PINBALASYNC0001", 2));
		assertEquals(200, response.statusCode());
		Document document = parseNamespaceAware(response.body());
		assertEquals(1, document.getElementsByTagNameNS("*", "Respuesta").getLength());
		assertEquals(2, document.getElementsByTagNameNS("*", "TransmisionDatos").getLength());
		assertEquals(java.util.Arrays.asList("000001", "000002"), textsByLocalName(document, "IdSolicitud"));
		assertEquals("PINBALASYNC0001", textsByLocalName(document, "IdPeticion").get(0));
		assertEquals("0003", textsByLocalName(document, "CodigoEstado").get(0));
	}

	@Test
	public void svddgpciws02_solicitudRespuestaUnaSollicitud_idSolicitudIgualAIdPeticion() throws Exception {
		HttpResponse<String> response = send(BASE_URL + "/servicios/SVDDGPCIWS02/asincrona",
			solicitudRespuestaDgp("PINBALASYNC0002", 1));
		assertEquals(200, response.statusCode());
		Document document = parseNamespaceAware(response.body());
		assertEquals(1, document.getElementsByTagNameNS("*", "TransmisionDatos").getLength());
		assertEquals(java.util.Collections.singletonList("PINBALASYNC0002"), textsByLocalName(document, "IdSolicitud"));
	}

	@Test
	public void svddgpciws02_peticioSincrona_segueixRetornantRespuesta() throws Exception {
		HttpResponse<String> response = send(BASE_URL + "/servicios/SVDDGPCIWS02/sincrona",
			peticionDgp("PINBALSYNC0001", "12345678Z"));
		assertEquals(200, response.statusCode());
		Document document = parseNamespaceAware(response.body());
		assertEquals(1, document.getElementsByTagNameNS("*", "Respuesta").getLength());
		assertEquals(0, document.getElementsByTagNameNS("*", "ConfirmacionPeticion").getLength());
		assertEquals(java.util.Collections.singletonList("PINBALSYNC0001"), textsByLocalName(document, "IdSolicitud"));
	}

	private static void assertFirstSignatureIsValidXmlDsigOverBody(String xml) throws Exception {
		Document document = parseNamespaceAware(xml);
		Element signature = (Element) document.getElementsByTagNameNS("*", "Signature").item(0);
		assertTrue("la resposta no porta cap signatura", signature != null);
		assertEquals("la primera signatura ha de ser directament sota el Header",
			"Header", signature.getParentNode().getLocalName());
		Element body = directChild(document.getDocumentElement(), "Body");
		body.setIdAttribute("Id", true);

		DOMValidateContext context = new DOMValidateContext(new X509KeySelector(), signature);
		context.setProperty("org.jcp.xml.dsig.secureValidation", Boolean.FALSE);
		XMLSignature xmlSignature = XMLSignatureFactory.getInstance("DOM").unmarshalXMLSignature(context);
		Reference reference = (Reference) xmlSignature.getSignedInfo().getReferences().get(0);
		assertEquals("#" + body.getAttribute("Id"), reference.getURI());
		assertTrue("digest del Body invàlid", reference.validate(context));
		assertTrue("signatura XMLDSig invàlida", xmlSignature.validate(context));
	}

	private static Document parseNamespaceAware(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
	}

	private static Element directChild(Element parent, String localName) {
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element && localName.equals(child.getLocalName())) {
				return (Element) child;
			}
		}
		return null;
	}

	private static final class X509KeySelector extends KeySelector {
		@Override
		public KeySelectorResult select(KeyInfo keyInfo, Purpose purpose, AlgorithmMethod method, XMLCryptoContext context)
			throws KeySelectorException {
			for (Object item : keyInfo.getContent()) {
				if (item instanceof X509Data) {
					for (Object data : ((X509Data) item).getContent()) {
						if (data instanceof X509Certificate) {
							final PublicKey key = ((X509Certificate) data).getPublicKey();
							return () -> key;
						}
					}
				}
			}
			throw new KeySelectorException("No s'ha trobat cap certificat X509 al KeyInfo");
		}
	}
}
