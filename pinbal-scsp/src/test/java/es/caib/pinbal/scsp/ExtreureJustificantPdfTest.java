/**
 * 
 */
package es.caib.pinbal.scsp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Test per a extreure el justificant de dins les dades específiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExtreureJustificantPdfTest {

	public static void main(String[] args) {
		try {
			new ExtreureJustificantPdfTest().test();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void test() throws Exception {
		Map<String, String> dades = new HashMap<String, String>();
		Document doc = xmlToDocument(
				getClass().getResourceAsStream("/es/caib/pinbal/scsp/resposta-justificant-pdf.xml"));
		NodeList nl = doc.getElementsByTagNameNS("*", "DatosEspecificos");
		if (nl.getLength() > 0) {
			List<String> path = new ArrayList<String>();
			recorrerDocument(
					nl.item(0),
					path,
					dades,
					true);
			for (String clau: dades.keySet()) {
				String dada = dades.get(clau);
				if (dada.length() > 64)
					System.out.println(">>> " + clau + ": " + dades.get(clau).substring(0, 64));
				else
					System.out.println(">>> " + clau + ": " + dades.get(clau));
			}
		}
	}

	private void recorrerDocument(
			org.w3c.dom.Node node,
			List<String> path,
			Map<String, String> dades,
			boolean incloureAlPath) {
		if (incloureAlPath) {
			if (node.getPrefix() != null) {
				path.add(node.getNodeName().substring(
						node.getPrefix().length() + 1));
			} else {
				path.add(node.getNodeName());
			}
		}
		if (node.hasChildNodes()) {
			NodeList fills = node.getChildNodes();
			for (int i = 0; i < fills.getLength(); i++) {
				org.w3c.dom.Node fill = fills.item(i);
				if (fill.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					recorrerDocument(
							fill,
							path,
							dades,
							true);
				}
				if (fill.getNodeType() == org.w3c.dom.Node.TEXT_NODE && fills.getLength() == 1) {
					dades.put(
							pathToString(path),
							node.getTextContent());
				}
			}
		} else {
			dades.put(
					pathToString(path),
					node.getTextContent());
		}
		if (incloureAlPath) {
			path.remove(path.size() - 1);
		}
	}

	private String pathToString(List<String> path) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < path.size(); i++) {
			sb.append("/");
			sb.append(path.get(i));
		}
		return sb.toString();
	}

	private Document xmlToDocument(InputStream is) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		builder = factory.newDocumentBuilder();
		Document doc = builder.parse(is);
		is.close();
		return doc;
	}

}
