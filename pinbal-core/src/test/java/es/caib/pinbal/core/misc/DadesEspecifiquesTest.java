/**
 * 
 */
package es.caib.pinbal.core.misc;

import static junit.framework.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author josepg
 *
 */
public class DadesEspecifiquesTest {

	private static final String DADES_ESPECIFIQUES_AMB_ERRORS = 
			"<ns1:DatosEspecificos xmlns:ns1=\"http://www.map.es/scsp/esquemas/datosespecificos\">" +
			"	<ns1:Estado>" +
			"		<ns1:CodigoEstado>0231</ns1:CodigoEstado>" +
			"		<ns1:CodigoEstadoSecundario />" +
			"		<ns1:LiteralError>FORMATO DE DOCUMENTO ERRÓNEO</ns1:LiteralError>" +
			"	</ns1:Estado>" +
			"	<ns1:Domicilio />" +
			"</ns1:DatosEspecificos>";

	@Test
	public void errorsXpath() throws Exception {
		Document doc = buildDocument(DADES_ESPECIFIQUES_AMB_ERRORS);
		testNodesDadesEspecifiques(doc.getFirstChild());
	}



	private Document buildDocument(String xml) throws Exception {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); 
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(xml.getBytes()));
	}

	private void testNodesDadesEspecifiques(Node rootNode) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NamespaceContext() {
			public String getNamespaceURI(String prefix) {
				return "http://www.map.es/scsp/esquemas/datosespecificos";
			}
			public Iterator<?> getPrefixes(String val) {
				return null;
			}
			public String getPrefix(String uri) {
				return null;
			}
		});
		String prefix = rootNode.getPrefix();
		System.out.println(">>> " + rootNode.getLocalName());
		XPathExpression expr = xpath.compile("//" + prefix + ":DatosEspecificos/" + prefix + ":Estado/*");
		Object result = expr.evaluate(rootNode, XPathConstants.NODESET);
		NodeList nodes = (NodeList)result;
		assertTrue("La llista de nodes és buida", nodes.getLength() > 0);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			System.out.println(">>> " + node.getLocalName() + ": " + node.getTextContent());
		}
	}

}
