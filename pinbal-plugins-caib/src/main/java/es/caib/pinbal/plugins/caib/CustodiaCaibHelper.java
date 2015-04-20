/**
 * 
 */
package es.caib.pinbal.plugins.caib;

import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.signatura.cliente.services.custodia.CustodiaServiceLocator;
import es.caib.signatura.cliente.services.custodia.CustodiaSoapBindingStub;
import es.caib.signatura.cliente.services.custodia.Custodia_PortType;

/**
 * Utilitats per a fer peticions a custòdia i parsejar les respostes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CustodiaCaibHelper {

	public static Custodia_PortType getCustodiaClient(
			String url,
			String username,
			String password) throws Exception {
		CustodiaServiceLocator locator = new CustodiaServiceLocator();
		Custodia_PortType custodiaClient = locator.getCustodia(new URL(url));
		((CustodiaSoapBindingStub)custodiaClient).setUsername(username);
		((CustodiaSoapBindingStub)custodiaClient).setPassword(password);
		return custodiaClient;
	}

	public static CustodiaResponse parseResponse(byte[] response) throws Exception {
		CustodiaResponse resposta = new CustodiaResponse();
		String xml = new String(response);
		LOGGER.debug("Parsejant resposta de custòdia (xml=" + xml +")");
		Document document = DocumentHelper.parseText(xml);
		Element resultMajorElement = null;
		Element resultMinorElement = null;
		Element resultMessageElement = null;
		if ("CustodiaResponse".equals(document.getRootElement().getName())) {
			resultMajorElement = document.getRootElement().element("VerifyResponse").element("Result").element("ResultMajor");
			resultMinorElement = document.getRootElement().element("VerifyResponse").element("Result").element("ResultMinor");
			resultMessageElement = document.getRootElement().element("VerifyResponse").element("Result").element("ResultMessage");
		} else if ("EliminacionResponse".equals(document.getRootElement().getName())) {
			resultMajorElement = document.getRootElement().element("Result").element("ResultMajor");
			resultMinorElement = document.getRootElement().element("Result").element("ResultMinor");
			resultMessageElement = document.getRootElement().element("Result").element("ResultMessage");
		} else if ("ConsultaResponse".equals(document.getRootElement().getName())) {
			resultMajorElement = document.getRootElement().element("Result").element("ResultMajor");
			resultMinorElement = document.getRootElement().element("Result").element("ResultMinor");
			resultMessageElement = document.getRootElement().element("Result").element("ResultMessage");
		} else if ("VerificacionResponse".equals(document.getRootElement().getName())) {
			resultMajorElement = document.getRootElement().element("Result").element("ResultMajor");
			resultMinorElement = document.getRootElement().element("Result").element("ResultMinor");
			resultMessageElement = document.getRootElement().element("Result").element("ResultMessage");
		} else {
			resultMajorElement = document.getRootElement().element("Result").element("ResultMajor");
			resultMinorElement = document.getRootElement().element("Result").element("ResultMinor");
			resultMessageElement = document.getRootElement().element("Result").element("ResultMessage");
		}
		if (resultMajorElement == null) {
			throw new DocumentException("No s'ha trobat el ResultMajor");
		}
		boolean hasErrors = hasErrors(resultMajorElement.getText());
		resposta.setError(hasErrors);
		if (hasErrors) {
			resposta.setErrorCodi(resultMinorElement.getText());
			resposta.setErrorDescripcio(resultMessageElement.getText());
		}
		return resposta;
	}
	public static boolean hasErrors(String resultMajor) {
		return (resultMajor.contains("error") ||
				resultMajor.contains("Error") ||
				resultMajor.contains("ERROR"));
	}

	public static boolean isXmlResponse(byte[] response) {
		byte[] iniciXml = new byte[5];
		for (int i = 0; i < 5; i++)
			iniciXml[i] = response[i];
		return "<?xml".equals(new String(iniciXml));
	}

	public static class CustodiaResponse {
		private boolean error;
		private String errorCodi;
		private String errorDescripcio;
		public boolean isError() {
			return error;
		}
		public void setError(boolean error) {
			this.error = error;
		}
		public String getErrorCodi() {
			return errorCodi;
		}
		public void setErrorCodi(String errorCodi) {
			this.errorCodi = errorCodi;
		}
		public String getErrorDescripcio() {
			return errorDescripcio;
		}
		public void setErrorDescripcio(String errorDescripcio) {
			this.errorDescripcio = errorDescripcio;
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CustodiaCaibHelper.class);

}
