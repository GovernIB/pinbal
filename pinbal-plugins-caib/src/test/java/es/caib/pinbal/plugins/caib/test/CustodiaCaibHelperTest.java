/**
 * 
 */
package es.caib.pinbal.plugins.caib.test;

import org.junit.Test;

import es.caib.pinbal.plugins.caib.CustodiaCaibHelper;
import es.caib.pinbal.plugins.caib.CustodiaCaibHelper.CustodiaResponse;

/**
 * Test per a comprovar el helper de custòdia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CustodiaCaibHelperTest {

	private static final String RESPOSTA_ERROR_LOGIN = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><con:ReservaResponse xmlns:con=\"http://www.caib.es.signatura.custodia\"><dss:Result xmlns:dss=\"urn:oasis:names:tc:dss:1.0:core:schema\"><dss:ResultMajor>RequesterError</dss:ResultMajor><dss:ResultMinor>ERROR_LOGIN</dss:ResultMinor><dss:ResultMessage xml:lang=\"es\">No se pudo identificar a la aplicacion PINBAL</dss:ResultMessage></dss:Result></con:ReservaResponse>";

	@Test
	public void parseRespostaError() throws Exception {
		CustodiaResponse response = CustodiaCaibHelper.parseResponse(RESPOSTA_ERROR_LOGIN.getBytes());
		if (response.isError()) {
			System.out.println(">>> Error [" + response.getErrorCodi() + "]: " + response.getErrorDescripcio());
		} else {
			System.out.println(">>> Ok");
		}
		
	}

}
