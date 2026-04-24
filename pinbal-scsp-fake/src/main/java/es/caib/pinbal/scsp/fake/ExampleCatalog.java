package es.caib.pinbal.scsp.fake;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ExampleCatalog {

	private static final Pattern SERVICE_PATTERN =
		Pattern.compile("(?m)^\\s*(\\d+)\\.\\s*Servei:\\s*(.+?)\\s*$");
	private static final Pattern LABEL_PATTERN =
		Pattern.compile("(?m)^\\s*\\d+(?:\\.\\d+)*\\s+([^\\n:]+?):?\\s*$");

	private final Map<String, ServiceExamples> services;

	private ExampleCatalog(Map<String, ServiceExamples> services) {
		this.services = services;
	}

	static ExampleCatalog loadDefault() throws IOException {
		InputStream input = ExampleCatalog.class.getClassLoader().getResourceAsStream("xml_peticions.txt");
		if (input == null) {
			throw new IOException("No s'ha trobat el recurs xml_peticions.txt al classpath");
		}
		return parse(input);
	}

	static ExampleCatalog parse(InputStream input) throws IOException {
		String text = readAll(input);
		Map<String, ServiceExamples> byService = new LinkedHashMap<String, ServiceExamples>();
		Matcher servicesMatcher = SERVICE_PATTERN.matcher(text);
		int start = -1;
		String currentService = null;

		while (servicesMatcher.find()) {
			if (currentService != null) {
				parseServiceBlock(currentService, text.substring(start, servicesMatcher.start()), byService);
			}
			currentService = servicesMatcher.group(2).trim();
			start = servicesMatcher.end();
		}
		if (currentService != null) {
			parseServiceBlock(currentService, text.substring(start), byService);
		}
		return new ExampleCatalog(Collections.unmodifiableMap(byService));
	}

	ServiceExamples getService(String serviceCode) {
		return services.get(serviceCode);
	}

	Map<String, ServiceExamples> getServices() {
		return services;
	}

	private static void parseServiceBlock(String serviceCode, String block, Map<String, ServiceExamples> byService)
		throws IOException {

		ServiceExamples examples = new ServiceExamples(serviceCode);
		Matcher labelMatcher = LABEL_PATTERN.matcher(block);
		String pendingRequestRoot = null;
		int cursor = 0;
		String currentLabel = null;

		while (labelMatcher.find()) {
			if (currentLabel != null) {
				String xml = extractXml(block.substring(cursor, labelMatcher.start()));
				if (xml != null) {
					pendingRequestRoot = registerExample(examples, xml, pendingRequestRoot);
				}
			}
			currentLabel = labelMatcher.group(1).trim();
			cursor = labelMatcher.end();
		}

		if (cursor > 0 && currentLabel != null) {
			String xml = extractXml(block.substring(cursor));
			if (xml != null) {
				registerExample(examples, xml, pendingRequestRoot);
			}
		}

		byService.put(serviceCode, examples);
	}

	private static String registerExample(
		ServiceExamples examples,
		String xml,
		String pendingRequestRoot) throws IOException {

		SoapEnvelopeInfo info = SoapEnvelopeInfo.fromXml(xml);
		if ("Peticion".equals(info.getBodyRoot()) || "SolicitudRespuesta".equals(info.getBodyRoot())) {
			return info.getBodyRoot();
		}
		if ("ConfirmacionPeticion".equals(info.getBodyRoot())) {
			examples.setAsyncConfirmation(xml);
			return null;
		}
		if ("SolicitudRespuesta".equals(pendingRequestRoot) && "Respuesta".equals(info.getBodyRoot())) {
			examples.setAsyncResponse(xml);
			return null;
		}
		if ("Peticion".equals(pendingRequestRoot) && "Respuesta".equals(info.getBodyRoot())) {
			examples.setSyncResponse(xml);
			return null;
		}
		if ("Respuesta".equals(info.getBodyRoot()) && examples.getSyncResponse() == null) {
			examples.setSyncResponse(xml);
			return null;
		}
		if ("Respuesta".equals(info.getBodyRoot())) {
			examples.setAsyncResponse(xml);
		}
		return null;
	}

	private static String extractXml(String raw) {
		String trimmed = raw.trim();
		if (trimmed.length() == 0) {
			return null;
		}
		int xmlStart = trimmed.indexOf('<');
		if (xmlStart < 0) {
			return null;
		}
		return trimmed.substring(xmlStart).trim();
	}

	private static String readAll(InputStream input) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line).append('\n');
		}
		return builder.toString();
	}

	static final class ServiceExamples {

		private final String serviceCode;
		private String syncResponse;
		private String asyncConfirmation;
		private String asyncResponse;

		ServiceExamples(String serviceCode) {
			this.serviceCode = serviceCode;
		}

		String getServiceCode() {
			return serviceCode;
		}

		String getSyncResponse() {
			return syncResponse;
		}

		void setSyncResponse(String syncResponse) {
			this.syncResponse = syncResponse;
		}

		String getAsyncConfirmation() {
			return asyncConfirmation;
		}

		void setAsyncConfirmation(String asyncConfirmation) {
			this.asyncConfirmation = asyncConfirmation;
		}

		String getAsyncResponse() {
			return asyncResponse;
		}

		void setAsyncResponse(String asyncResponse) {
			this.asyncResponse = asyncResponse;
		}
	}

	static final class SoapEnvelopeInfo {

		private final String bodyRoot;
		private final String serviceCode;
		private final String numElementos;
		private final String idPeticion;
		private final List<String> idSolicitudes;

		private SoapEnvelopeInfo(
			String bodyRoot,
			String serviceCode,
			String numElementos,
			String idPeticion,
			List<String> idSolicitudes) {
			this.bodyRoot = bodyRoot;
			this.serviceCode = serviceCode;
			this.numElementos = numElementos;
			this.idPeticion = idPeticion;
			this.idSolicitudes = idSolicitudes;
		}

		static SoapEnvelopeInfo fromXml(String xml) throws IOException {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
				Element bodyChild = firstElementChild(findByLocalName(document.getDocumentElement(), "Body"));
				if (bodyChild == null) {
					throw new IOException("No s'ha trobat el cos SOAP");
				}
				String code = findTextByLocalName(bodyChild, "CodigoCertificado");
				String num = findTextByLocalName(bodyChild, "NumElementos");
				String idPeticion = findTextByLocalName(bodyChild, "IdPeticion");
				List<String> idSolicitudes = findTextsByLocalName(bodyChild, "IdSolicitud");
				return new SoapEnvelopeInfo(bodyChild.getLocalName(), code, num, idPeticion, idSolicitudes);
			} catch (Exception e) {
				throw new IOException("No s'ha pogut analitzar l'XML SCSP", e);
			}
		}

		String getBodyRoot() {
			return bodyRoot;
		}

		String getServiceCode() {
			return serviceCode;
		}

		String getNumElementos() {
			return numElementos;
		}

		String getIdPeticion() {
			return idPeticion;
		}

		List<String> getIdSolicitudes() {
			return idSolicitudes;
		}

		private static Element findByLocalName(Element root, String localName) {
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
					Element found = findByLocalName((Element) child, localName);
					if (found != null) {
						return found;
					}
				}
			}
			return null;
		}

		private static String findTextByLocalName(Element root, String localName) {
			Element found = findByLocalName(root, localName);
			return found == null ? null : found.getTextContent().trim();
		}

		private static List<String> findTextsByLocalName(Element root, String localName) {
			List<String> values = new ArrayList<String>();
			collectTextsByLocalName(root, localName, values);
			return values;
		}

		private static void collectTextsByLocalName(Element root, String localName, List<String> values) {
			if (root == null) {
				return;
			}
			if (localName.equals(root.getLocalName())) {
				values.add(root.getTextContent().trim());
			}
			NodeList children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element) {
					collectTextsByLocalName((Element) child, localName, values);
				}
			}
		}

		private static Element firstElementChild(Element parent) {
			if (parent == null) {
				return null;
			}
			NodeList children = parent.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element) {
					return (Element) child;
				}
			}
			return null;
		}
	}
}
