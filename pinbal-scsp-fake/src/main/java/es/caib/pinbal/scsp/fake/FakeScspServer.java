package es.caib.pinbal.scsp.fake;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import es.caib.pinbal.scsp.fake.ExampleCatalog.ServiceExamples;
import es.caib.pinbal.scsp.fake.ExampleCatalog.SoapEnvelopeInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.Executors;
import javax.xml.XMLConstants;

public final class FakeScspServer {

	private static final String DEFAULT_HOST = "0.0.0.0";
	private static final int DEFAULT_PORT = 18080;
	private static final String DEFAULT_KEYSTORE_RESOURCE = "interoperabilitat.jks";
	private static final String DEFAULT_KEYSTORE_PASSWORD = "tecnologies";
	private static final String DEFAULT_KEY_ALIAS = "limit_emiserv";
	private static final String SOAPENV_NS = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String WSSE_NS =
		"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	private static final String WSU_NS =
		"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
	private static final String DATOS_ESPECIFICOS_NS =
		"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";
	private static final String X509_V3_TYPE =
		"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3";
	private static final String BASE64_BINARY_TYPE =
		"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary";

	private FakeScspServer() {
	}

	public static void main(String[] args) throws Exception {
		String host = System.getProperty("fake.scsp.host", DEFAULT_HOST);
		int port = Integer.parseInt(System.getProperty("fake.scsp.port", String.valueOf(DEFAULT_PORT)));
		ExampleCatalog catalog = ExampleCatalog.loadDefault();

		HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
		server.createContext("/", new FakeScspHandler(catalog));
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();

		System.out.println("Fake SCSP escoltant a http://" + host + ":" + port);
		System.out.println("Serveis carregats: " + catalog.getServices().keySet());
	}

	private static final class FakeScspHandler implements HttpHandler {

		private final ExampleCatalog catalog;
		private final SoapResponseSigner signer;

		private FakeScspHandler(ExampleCatalog catalog) {
			this.catalog = catalog;
			this.signer = new SoapResponseSigner();
		}

		public void handle(HttpExchange exchange) throws IOException {
			try {
				if ("GET".equalsIgnoreCase(exchange.getRequestMethod()) && "/__fake-scsp".equals(exchange.getRequestURI().getPath())) {
					writeText(exchange, 200, buildStatus());
					return;
				}
				if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
					writeText(exchange, 405, "Només s'accepten peticions POST SOAP");
					return;
				}

				String requestXml = readBody(exchange.getRequestBody());
				SoapEnvelopeInfo requestInfo = SoapEnvelopeInfo.fromXml(requestXml);
				logRequest(exchange, requestInfo, requestXml);
				ServiceExamples service = catalog.getService(requestInfo.getServiceCode());

				if (service == null) {
					System.err.println("[FAKE-SCSP] Sense exemples per al servei " + requestInfo.getServiceCode());
					writeText(exchange, 404, "No hi ha cap exemple carregat per al servei " + requestInfo.getServiceCode());
					return;
				}

				String responseXml = resolveResponse(exchange, requestInfo, service);
				if (responseXml == null) {
					System.err.println("[FAKE-SCSP] Sense resposta per a servei=" + requestInfo.getServiceCode()
						+ ", body=" + requestInfo.getBodyRoot()
						+ ", path=" + exchange.getRequestURI().getPath());
					writeText(exchange, 404, "No hi ha resposta fake per a " + requestInfo.getServiceCode() + " i operació " + requestInfo.getBodyRoot());
					return;
				}
				responseXml = adaptResponseToRequest(responseXml, requestInfo);
				responseXml = signer.sign(responseXml);
				System.out.println("[FAKE-SCSP] XML resposta:");
				System.out.println(responseXml);

				System.out.println("[FAKE-SCSP] Resposta enviada"
					+ " method=" + exchange.getRequestMethod()
					+ " path=" + exchange.getRequestURI().getPath()
					+ " servei=" + requestInfo.getServiceCode()
					+ " body=" + requestInfo.getBodyRoot()
					+ " async=" + isAsync(exchange.getRequestURI().getPath())
					+ " multiple=" + hasMultipleElements(requestInfo)
					+ " numElementos=" + requestInfo.getNumElementos()
					+ " idPeticio=" + requestInfo.getIdPeticion()
					+ " idSolicitudes=" + requestInfo.getIdSolicitudes());
				writeXml(exchange, 200, responseXml);
			} catch (Exception e) {
				System.err.println("[FAKE-SCSP] Error intern processant "
					+ exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath()
					+ ": " + e.getMessage());
				e.printStackTrace(System.err);
				writeText(exchange, 500, "Error intern del fake SCSP: " + e.getMessage());
			} finally {
				exchange.close();
			}
		}

		private String resolveResponse(HttpExchange exchange, SoapEnvelopeInfo requestInfo, ServiceExamples service) {
			if ("SolicitudRespuesta".equals(requestInfo.getBodyRoot())) {
				if (containsEncryptedData(service.getAsyncResponse()) && service.getSyncResponse() != null) {
					System.out.println("[FAKE-SCSP] Resposta asincrona xifrada detectada; s'utilitza la resposta sincrona com a base"
						+ " servei=" + requestInfo.getServiceCode());
					return service.getSyncResponse();
				}
				System.out.println("[FAKE-SCSP] Resolent resposta asincrona"
					+ " servei=" + requestInfo.getServiceCode()
					+ " teAsyncResponse=" + (service.getAsyncResponse() != null));
				return service.getAsyncResponse();
			}
			if ("Peticion".equals(requestInfo.getBodyRoot())) {
				if (isAsync(exchange.getRequestURI().getPath()) && service.getAsyncConfirmation() != null) {
					System.out.println("[FAKE-SCSP] Resolent confirmacio asincrona per path"
						+ " servei=" + requestInfo.getServiceCode());
					return service.getAsyncConfirmation();
				}
				if (hasMultipleElements(requestInfo) && service.getAsyncConfirmation() != null) {
					System.out.println("[FAKE-SCSP] Resolent confirmacio asincrona per peticio multiple"
						+ " servei=" + requestInfo.getServiceCode());
					return service.getAsyncConfirmation();
				}
				System.out.println("[FAKE-SCSP] Resolent resposta sincrona"
					+ " servei=" + requestInfo.getServiceCode()
					+ " teSyncResponse=" + (service.getSyncResponse() != null));
				return service.getSyncResponse();
			}
			System.err.println("[FAKE-SCSP] Body SOAP no suportat: " + requestInfo.getBodyRoot());
			return null;
		}

		private boolean containsEncryptedData(String xml) {
			return xml != null && xml.contains("EncryptedData");
		}

		private void logRequest(HttpExchange exchange, SoapEnvelopeInfo requestInfo, String requestXml) {
			System.out.println("[FAKE-SCSP] Peticio rebuda"
				+ " method=" + exchange.getRequestMethod()
				+ " path=" + exchange.getRequestURI().getPath()
				+ " servei=" + requestInfo.getServiceCode()
				+ " body=" + requestInfo.getBodyRoot()
				+ " async=" + isAsync(exchange.getRequestURI().getPath())
				+ " multiple=" + hasMultipleElements(requestInfo)
				+ " numElementos=" + requestInfo.getNumElementos()
				+ " idPeticio=" + requestInfo.getIdPeticion()
				+ " idSolicitudes=" + requestInfo.getIdSolicitudes());
			System.out.println("[FAKE-SCSP] XML peticio:");
			System.out.println(requestXml);
		}

		private String adaptResponseToRequest(String responseXml, SoapEnvelopeInfo requestInfo) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(new java.io.ByteArrayInputStream(responseXml.getBytes(StandardCharsets.UTF_8)));

				if (requestInfo.getIdPeticion() != null && requestInfo.getIdPeticion().trim().length() > 0) {
					updateElementsByLocalName(document.getDocumentElement(), "IdPeticion", requestInfo.getIdPeticion());
				}

				String numElementos = requestInfo.getNumElementos();
				if ((numElementos == null || numElementos.trim().length() == 0) && !requestInfo.getIdSolicitudes().isEmpty()) {
					numElementos = String.valueOf(requestInfo.getIdSolicitudes().size());
				}
				if (numElementos != null && numElementos.trim().length() > 0) {
					updateFirstElementByLocalName(document.getDocumentElement(), "NumElementos", numElementos);
				}

				int expectedTransmissions = resolveExpectedTransmissions(requestInfo);
				if (expectedTransmissions > 0) {
					ensureTransmisionCount(document, expectedTransmissions);
				}
				List<String> responseIds = resolveResponseIds(requestInfo, expectedTransmissions);
				if (!responseIds.isEmpty()) {
					updateSequentialElementsByLocalName(document.getDocumentElement(), "IdSolicitud", responseIds);
				}
				normalizeAsyncResponseStatus(document, requestInfo);
				ensureIdTransmisionValues(document);
				normalizeEncryptedSpecificData(document);

				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				StringWriter writer = new StringWriter();
				transformer.transform(new DOMSource(document), new StreamResult(writer));
				return writer.toString();
			} catch (Exception e) {
				return responseXml;
			}
		}

		private void updateFirstElementByLocalName(Element root, String localName, String value) {
			Element found = findFirstByLocalName(root, localName);
			if (found != null) {
				found.setTextContent(value);
			}
		}

		private void updateElementsByLocalName(Element root, String localName, String value) {
			List<Element> elements = findAllByLocalName(root, localName);
			for (Element element : elements) {
				element.setTextContent(value);
			}
		}

		private void updateSequentialElementsByLocalName(Element root, String localName, List<String> values) {
			List<Element> elements = findAllByLocalName(root, localName);
			for (int i = 0; i < elements.size() && i < values.size(); i++) {
				elements.get(i).setTextContent(values.get(i));
			}
		}

		private void ensureTransmisionCount(Document document, int expectedCount) {
			if (expectedCount <= 1) {
				return;
			}
			Element transmissions = findFirstByLocalName(document.getDocumentElement(), "Transmisiones");
			if (transmissions == null) {
				return;
			}
			List<Element> items = findDirectChildrenByLocalName(transmissions, "TransmisionDatos");
			if (items.isEmpty()) {
				return;
			}
			Element template = items.get(0);
			while (items.size() < expectedCount) {
				Element clone = (Element) template.cloneNode(true);
				transmissions.appendChild(clone);
				items.add(clone);
			}
		}

		private int resolveExpectedTransmissions(SoapEnvelopeInfo requestInfo) {
			if (!requestInfo.getIdSolicitudes().isEmpty()) {
				return requestInfo.getIdSolicitudes().size();
			}
			String numElementos = requestInfo.getNumElementos();
			if (numElementos == null || numElementos.trim().length() == 0) {
				return 0;
			}
			try {
				return Integer.parseInt(numElementos.trim());
			} catch (NumberFormatException e) {
				return 0;
			}
		}

		private List<String> resolveResponseIds(SoapEnvelopeInfo requestInfo, int expectedTransmissions) {
			if (!requestInfo.getIdSolicitudes().isEmpty()) {
				return requestInfo.getIdSolicitudes();
			}
			List<String> ids = new ArrayList<String>();
			for (int i = 1; i <= expectedTransmissions; i++) {
				ids.add(String.format("%06d", i));
			}
			return ids;
		}

		private void normalizeEncryptedSpecificData(Document document) {
			List<Element> specificDataNodes = findAllByLocalName(document.getDocumentElement(), "DatosEspecificos");
			for (Element specificData : specificDataNodes) {
				if (hasElementChild(specificData, "EncryptedData")) {
					replaceWithDefaultRetorno(document, specificData);
				}
			}
		}

		private boolean hasElementChild(Element root, String localName) {
			NodeList children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element && localName.equals(child.getLocalName())) {
					return true;
				}
			}
			return false;
		}

		private void replaceWithDefaultRetorno(Document document, Element specificData) {
			removeAllChildren(specificData);
			specificData.removeAttribute("Id");
			Element retorno = document.createElementNS(DATOS_ESPECIFICOS_NS, qualifyName(specificData.getPrefix(), "Retorno"));
			Element codigo = document.createElementNS(DATOS_ESPECIFICOS_NS, qualifyName(specificData.getPrefix(), "Codigo"));
			codigo.setTextContent("0000");
			Element resultado = document.createElementNS(DATOS_ESPECIFICOS_NS, qualifyName(specificData.getPrefix(), "Resultado"));
			resultado.setTextContent("SI");
			Element descripcion = document.createElementNS(DATOS_ESPECIFICOS_NS, qualifyName(specificData.getPrefix(), "Descripcion"));
			descripcion.setTextContent("Resposta fake sense xifrat");
			retorno.appendChild(codigo);
			retorno.appendChild(resultado);
			retorno.appendChild(descripcion);
			specificData.appendChild(retorno);
		}

		private void normalizeAsyncResponseStatus(Document document, SoapEnvelopeInfo requestInfo) {
			if (!"SolicitudRespuesta".equals(requestInfo.getBodyRoot())) {
				return;
			}
			updateFirstElementByLocalName(document.getDocumentElement(), "CodigoEstado", "0003");
			updateFirstElementByLocalName(document.getDocumentElement(), "LiteralError", "Tramitada");
			updateFirstElementByLocalName(document.getDocumentElement(), "TiempoEstimadoRespuesta", "0");
		}

		private void ensureIdTransmisionValues(Document document) {
			List<Element> transmissionIds = findAllByLocalName(document.getDocumentElement(), "IdTransmision");
			int index = 1;
			for (Element transmissionId : transmissionIds) {
				String value = transmissionId.getTextContent();
				if (value == null || value.trim().length() == 0) {
					transmissionId.setTextContent("TRANS" + System.currentTimeMillis() + String.format("%03d", index));
				}
				index++;
			}
		}

		private void removeAllChildren(Element element) {
			while (element.hasChildNodes()) {
				element.removeChild(element.getFirstChild());
			}
		}

		private String qualifyName(String prefix, String localName) {
			return prefix == null || prefix.length() == 0 ? localName : prefix + ":" + localName;
		}

		private Element findFirstByLocalName(Element root, String localName) {
			if (root == null) {
				return null;
			}
			if (localName.equals(root.getLocalName())) {
				return root;
			}
			NodeList children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element) {
					Element found = findFirstByLocalName((Element) child, localName);
					if (found != null) {
						return found;
					}
				}
			}
			return null;
		}

		private List<Element> findAllByLocalName(Element root, String localName) {
			List<Element> elements = new ArrayList<Element>();
			collectByLocalName(root, localName, elements);
			return elements;
		}

		private List<Element> findDirectChildrenByLocalName(Element root, String localName) {
			List<Element> elements = new ArrayList<Element>();
			if (root == null) {
				return elements;
			}
			NodeList children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element && localName.equals(child.getLocalName())) {
					elements.add((Element) child);
				}
			}
			return elements;
		}

		private void collectByLocalName(Element root, String localName, List<Element> elements) {
			if (root == null) {
				return;
			}
			if (localName.equals(root.getLocalName())) {
				elements.add(root);
			}
			NodeList children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element) {
					collectByLocalName((Element) child, localName, elements);
				}
			}
		}

		private boolean hasMultipleElements(SoapEnvelopeInfo requestInfo) {
			return requestInfo.getNumElementos() != null
				&& requestInfo.getNumElementos().trim().length() > 0
				&& !"1".equals(requestInfo.getNumElementos().trim());
		}

		private boolean isAsync(String path) {
			return path != null && path.toLowerCase().contains("asincrona");
		}

		private String buildStatus() {
			StringBuilder builder = new StringBuilder();
			builder.append("Fake SCSP actiu\n");
			for (Map.Entry<String, ServiceExamples> entry : catalog.getServices().entrySet()) {
				ServiceExamples service = entry.getValue();
				builder.append("- ").append(service.getServiceCode());
				if (service.getSyncResponse() != null) {
					builder.append(" sync");
				}
				if (service.getAsyncConfirmation() != null) {
					builder.append(" async-confirm");
				}
				if (service.getAsyncResponse() != null) {
					builder.append(" async-response");
				}
				builder.append('\n');
			}
			return builder.toString();
		}

		private void writeXml(HttpExchange exchange, int status, String body) throws IOException {
			byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
			Headers headers = exchange.getResponseHeaders();
			headers.set("Content-Type", "text/xml; charset=utf-8");
			exchange.sendResponseHeaders(status, bytes.length);
			OutputStream output = exchange.getResponseBody();
			output.write(bytes);
			output.flush();
		}

		private void writeText(HttpExchange exchange, int status, String body) throws IOException {
			byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
			Headers headers = exchange.getResponseHeaders();
			headers.set("Content-Type", "text/plain; charset=utf-8");
			exchange.sendResponseHeaders(status, bytes.length);
			OutputStream output = exchange.getResponseBody();
			output.write(bytes);
			output.flush();
		}

		private String readBody(InputStream input) throws IOException {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int read;
			while ((read = input.read(buffer)) != -1) {
				output.write(buffer, 0, read);
			}
			return new String(output.toByteArray(), StandardCharsets.UTF_8);
		}
	}

	private static final class SoapResponseSigner {

		private final PrivateKey privateKey;
		private final X509Certificate certificate;
		private final String keyAlias;

		private SoapResponseSigner() {
			try {
				this.keyAlias = System.getProperty("fake.scsp.keyAlias", DEFAULT_KEY_ALIAS);
				String keystorePassword = System.getProperty("fake.scsp.keystorePass", DEFAULT_KEYSTORE_PASSWORD);
				String keyPassword = System.getProperty("fake.scsp.keyPassword", keystorePassword);
				KeyStore keystore = loadKeyStore(keystorePassword);
				this.privateKey = (PrivateKey) keystore.getKey(keyAlias, keyPassword.toCharArray());
				this.certificate = (X509Certificate) keystore.getCertificate(keyAlias);
				if (privateKey == null || certificate == null) {
					throw new IllegalStateException("No s'ha trobat la clau privada o el certificat per a l'alias " + keyAlias);
				}
			} catch (Exception e) {
				throw new IllegalStateException("No s'ha pogut inicialitzar la firma del fake SCSP", e);
			}
		}

		private String sign(String xml) {
			try {
				Document document = parse(xml);
				removeSecurityHeader(document);
				Element securityHeader = ensureSecurityHeader(document);
				String idSeed = UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ROOT);
				String tokenId = "CertId-" + idSeed;
				String keyInfoId = "KeyId-" + idSeed;
				String securityTokenReferenceId = "STRId-" + idSeed;
				appendBinarySecurityToken(document, securityHeader, tokenId);
				appendSignature(document, securityHeader, tokenId, keyInfoId, securityTokenReferenceId);
				return toXml(document);
			} catch (Exception e) {
				throw new IllegalStateException("No s'ha pogut firmar la resposta SOAP fake", e);
			}
		}

		private KeyStore loadKeyStore(String keystorePassword) throws Exception {
			String resource = System.getProperty("fake.scsp.keystoreResource", DEFAULT_KEYSTORE_RESOURCE);
			InputStream input = FakeScspServer.class.getClassLoader().getResourceAsStream(resource);
			if (input == null) {
				throw new IllegalStateException("No s'ha trobat el keystore al classpath: " + resource);
			}
			try {
				KeyStore keyStore = KeyStore.getInstance("JKS");
				keyStore.load(input, keystorePassword.toCharArray());
				return keyStore;
			} finally {
				input.close();
			}
		}

		private Document parse(String xml) throws Exception {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new StringReader(xml)));
		}

		private Element ensureSecurityHeader(Document document) {
			Element envelope = document.getDocumentElement();
			Element header = findFirstByLocalName(envelope, "Header");
			if (header == null) {
				header = document.createElementNS(SOAPENV_NS, qualifyName(envelope.getPrefix(), "Header"));
				Element body = findFirstByLocalName(envelope, "Body");
				envelope.insertBefore(header, body);
			}
			Element security = document.createElementNS(WSSE_NS, "wsse:Security");
			header.appendChild(security);
			return security;
		}

		private void appendBinarySecurityToken(Document document, Element securityHeader, String tokenId) throws Exception {
			Element binarySecurityToken = document.createElementNS(WSSE_NS, "wsse:BinarySecurityToken");
			binarySecurityToken.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:wsu", WSU_NS);
			binarySecurityToken.setAttribute("EncodingType", BASE64_BINARY_TYPE);
			binarySecurityToken.setAttribute("ValueType", X509_V3_TYPE);
			binarySecurityToken.setAttributeNS(WSU_NS, "wsu:Id", tokenId);
			binarySecurityToken.setTextContent(Base64.getEncoder().encodeToString(certificate.getEncoded()));
			securityHeader.appendChild(binarySecurityToken);
		}

		private void appendSignature(
			Document document,
			Element securityHeader,
			String tokenId,
			String keyInfoId,
			String securityTokenReferenceId) throws Exception {
			Element body = findFirstByLocalName(document.getDocumentElement(), "Body");
			if (body == null) {
				throw new IllegalStateException("No s'ha trobat el Body del SOAP");
			}
			body.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:wsu", WSU_NS);
			if (!body.hasAttribute("Id")) {
				body.setAttribute("Id", "Body");
			}
			String bodyId = body.getAttributeNS(WSU_NS, "Id");
			if (bodyId == null || bodyId.length() == 0) {
				bodyId = "id-" + positiveRandomNumber();
				body.setAttributeNS(WSU_NS, "wsu:Id", bodyId);
			}
			body.setIdAttributeNS(WSU_NS, "Id", true);
			body.setIdAttribute("Id", true);

			XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");
			List<Transform> transforms = Arrays.asList(
				signatureFactory.newTransform(CanonicalizationMethod.EXCLUSIVE, (javax.xml.crypto.dsig.spec.TransformParameterSpec) null)
			);
			Reference reference = signatureFactory.newReference(
				"#" + bodyId,
				signatureFactory.newDigestMethod(DigestMethod.SHA1, null),
				transforms,
				null,
				null
			);
			SignedInfo signedInfo = signatureFactory.newSignedInfo(
				signatureFactory.newCanonicalizationMethod(
					CanonicalizationMethod.EXCLUSIVE,
					(javax.xml.crypto.dsig.spec.C14NMethodParameterSpec) null),
				signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
				Collections.singletonList(reference)
			);

			KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
			Element securityTokenReference = createSecurityTokenReference(document, tokenId, securityTokenReferenceId);
			KeyInfo keyInfo = keyInfoFactory.newKeyInfo(
				Collections.singletonList(new DOMStructure(securityTokenReference)),
				keyInfoId
			);

			DOMSignContext signContext = new DOMSignContext(privateKey, securityHeader);
			signContext.setDefaultNamespacePrefix("ds");
			signContext.putNamespacePrefix(WSSE_NS, "wsse");
			signContext.putNamespacePrefix(WSU_NS, "wsu");
			XMLSignature signature = signatureFactory.newXMLSignature(
				signedInfo,
				keyInfo,
				null,
				"Signature-" + positiveRandomNumber(),
				null);
			signature.sign(signContext);
		}

		private Element createSecurityTokenReference(Document document, String tokenId, String securityTokenReferenceId) {
			Element securityTokenReference = document.createElementNS(WSSE_NS, "wsse:SecurityTokenReference");
			securityTokenReference.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:wsu", WSU_NS);
			securityTokenReference.setAttributeNS(WSU_NS, "wsu:Id", securityTokenReferenceId);
			Element reference = document.createElementNS(WSSE_NS, "wsse:Reference");
			reference.setAttribute("URI", "#" + tokenId);
			reference.setAttribute("ValueType", X509_V3_TYPE);
			securityTokenReference.appendChild(reference);
			return securityTokenReference;
		}

		private void removeSecurityHeader(Document document) {
			Element envelope = document.getDocumentElement();
			Element header = findFirstByLocalName(envelope, "Header");
			if (header == null) {
				return;
			}
			List<Element> securityHeaders = findAllByNamespaceAndLocalName(header, WSSE_NS, "Security");
			for (Element securityHeader : securityHeaders) {
				header.removeChild(securityHeader);
			}
		}

		private String toXml(Document document) throws Exception {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			return writer.toString();
		}

		private Element findFirstByLocalName(Element root, String localName) {
			if (root == null) {
				return null;
			}
			if (localName.equals(root.getLocalName())) {
				return root;
			}
			NodeList children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element) {
					Element found = findFirstByLocalName((Element) child, localName);
					if (found != null) {
						return found;
					}
				}
			}
			return null;
		}

		private List<Element> findAllByNamespaceAndLocalName(Element root, String namespace, String localName) {
			List<Element> elements = new ArrayList<Element>();
			collectByNamespaceAndLocalName(root, namespace, localName, elements);
			return elements;
		}

		private void collectByNamespaceAndLocalName(
			Element root,
			String namespace,
			String localName,
			List<Element> elements) {
			if (root == null) {
				return;
			}
			if (localName.equals(root.getLocalName()) && namespace.equals(root.getNamespaceURI())) {
				elements.add(root);
			}
			NodeList children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element) {
					collectByNamespaceAndLocalName((Element) child, namespace, localName, elements);
				}
			}
		}

		private String qualifyName(String prefix, String localName) {
			return prefix == null || prefix.length() == 0 ? localName : prefix + ":" + localName;
		}

		private long positiveRandomNumber() {
			return Math.abs(UUID.randomUUID().getMostSignificantBits());
		}
	}
}
