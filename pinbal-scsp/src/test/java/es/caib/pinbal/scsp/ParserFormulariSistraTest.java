/**
 * 
 */
package es.caib.pinbal.scsp;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

/**
 * Test per a fer consultes amb xpath sobre una petició SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ParserFormulariSistraTest {

	public static void main(String[] args) {
		try {
			new ParserFormulariSistraTest().test();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void test() throws Exception {
		Document doc = xmlToDocument(
				getClass().getResourceAsStream("/es/caib/pinbal/scsp/form_sistra.xml"));
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		String resultat = xpath.evaluate(
				"/FORMULARIO/DATOS_ACCION_ESPECIAL/CDTIMPORTEAYUDACOFIVA",
				doc);
		System.out.println(">>> " + resultat);
	}

	private Document xmlToDocument(InputStream is) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		factory.setNamespaceAware(false);
		DocumentBuilder builder = null;
		builder = factory.newDocumentBuilder();
		Document doc = builder.parse(is);
		is.close();
		return doc;
	}

	/*private String documentToXml(Document doc) throws Exception {
		DOMSource domSource = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		return writer.toString();
	}*/

}
