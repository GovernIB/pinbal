/**
 * 
 */
package es.caib.pinbal.scsp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Test per a fer consultes amb xpath de les dades dels DatosEspecificos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class XpathDadesEspecifiquesTest {

	private static final String XML_DADES_ESPECIFIQUES = "<DatosEspecificos xmlns=\"http://www.map.es/scsp/esquemas/datosespecificos\"><Solicitud><Espanol>s</Espanol><Nacimiento><Fecha>29/09/75</Fecha></Nacimiento></Solicitud><SolicitanteDatos><Tipo>app</Tipo></SolicitanteDatos></DatosEspecificos>";

	public static void main(String[] args) {
		try {
			new XpathDadesEspecifiquesTest().test();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void test() throws Exception {
		Document doc = xmlToDocument(
				new ByteArrayInputStream(XML_DADES_ESPECIFIQUES.getBytes()));
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new UniversalNamespaceContext(doc));
		NodeList nodes = (NodeList)xPath.evaluate(
				"/:DatosEspecificos/:SolicitanteDatos/:Tipo",
		        doc.getDocumentElement(),
		        XPathConstants.NODESET);
		System.out.println(">>> " + nodes.getLength());
		for (int i = 0; i < nodes.getLength(); ++i) {
		    Element element = (Element)nodes.item(i);
		    System.out.println(">>> " + element.getTagName() + ": " + element.getTextContent());
		}
	}

	public Document xmlToDocument(InputStream is) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		builder = factory.newDocumentBuilder();
		Document doc = builder.parse(is);
		is.close();
		return doc;
	}

	public class UniversalNamespaceContext implements NamespaceContext {
		private Document sourceDocument;
	    public UniversalNamespaceContext(Document document) {
	        sourceDocument = document;
	    }
		@Override
		public String getNamespaceURI(String prefix) {
			if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
	            return sourceDocument.lookupNamespaceURI(null);
	        } else {
	            return sourceDocument.lookupNamespaceURI(prefix);
	        }
		}
		@Override
		public String getPrefix(String namespaceURI) {
			return sourceDocument.lookupPrefix(namespaceURI);
		}
		@SuppressWarnings("rawtypes")
		@Override
		public Iterator getPrefixes(String namespaceURI) {
			return null;
		}
	}
	
}
