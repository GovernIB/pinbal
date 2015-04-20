/**
 * 
 */
package es.caib.pinbal.scsp;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test per a fer consultes amb xpath sobre una petició SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExtreureTransmisionDatosTest {

	public static void main(String[] args) {
		try {
			new ExtreureTransmisionDatosTest().test();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void test() throws Exception {
		Document doc = xmlToDocument(
				getClass().getResourceAsStream("/es/caib/pinbal/scsp/peticio.xml"));
		NodeList nlDg = doc.getElementsByTagName("IdSolicitud");
		for (int i = 0; i < nlDg.getLength(); i++) {
			Node n = nlDg.item(i);
			if (n.getParentNode().getNodeName().equals("Transmision")) {
				System.out.println(">>> " + n.getParentNode().getParentNode().getParentNode().getNodeName());
				System.out.println(">>> " + n.getTextContent());
			}
		}
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
