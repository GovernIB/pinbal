/**
 * 
 */
package es.caib.pinbal.core.helper;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;

/**
 * Utilitats per a gestionar les dades dels documents XML.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class XmlHelper {

	public static String getSingleNodeValue(
			Object item,
			String expression) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node)(xpath.evaluate(
				expression,
				item,
				XPathConstants.NODE));
		if (node != null)
			return node.getFirstChild().getNodeValue();
		else
			return null;
	}

}
