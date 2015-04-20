/**
 * 
 */
package es.caib.pinbal.plugins.caib.test;

import junit.framework.Assert;

import org.junit.Test;

import es.caib.pinbal.plugins.caib.CustodiaCaibHelper;
import es.caib.pinbal.plugins.caib.CustodiaCaibHelper.CustodiaResponse;
import es.caib.signatura.cliente.custodia.CustodiaRequestBuilder;

/**
 * Test per a comprovar la connexió amb la custòdia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CustodiaCaibTest {

	private static final String URL = "https://proves.caib.es/signatura/services/CustodiaDocumentos";
	private static final String USERNAME = "HELIUM";
	private static final String PASSWORD = "HELIUMABC";
	private static final String TIPUS_DOC = "HEL_SDP_CIT";
	private static final String RESOURCE_FILE_NAME = "/signat.pdf";
	private static final String DOCUMENT_ID = "PINBAL_TEST_1";

	@Test
	public void desarDocument() throws Exception {
		CustodiaRequestBuilder custodiaRequestBuilder = new CustodiaRequestBuilder(
				USERNAME,
				PASSWORD);
		String documentId = "PINBAL_TEST_" + System.currentTimeMillis();
		byte[] xml = custodiaRequestBuilder.buildXML(
				getClass().getResourceAsStream(RESOURCE_FILE_NAME),
				RESOURCE_FILE_NAME.substring(1),
				documentId,
				TIPUS_DOC);
		byte[] response = CustodiaCaibHelper.getCustodiaClient(
				URL,
				USERNAME,
				PASSWORD).custodiarPDFFirmado_v2(xml);
		CustodiaResponse resposta = CustodiaCaibHelper.parseResponse(response);
		if (resposta.isError())
			Assert.fail("Error al custodiar PDF signat: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
	}

	@Test
	public void obtenirDocument() throws Exception {
		byte[] response = CustodiaCaibHelper.getCustodiaClient(
				URL,
				USERNAME,
				PASSWORD).consultarDocumento_v2(
				USERNAME,
				PASSWORD,
				DOCUMENT_ID);
		byte[] iniciXml = new byte[5];
		for (int i = 0; i < 5; i++)
			iniciXml[i] = response[i];
		if ("<?xml".equals(new String(iniciXml))) {
			CustodiaResponse resposta = CustodiaCaibHelper.parseResponse(response);
			Assert.fail("Error al llegir document de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
		} else {
			Assert.assertTrue("El document és buit", response.length > 0);
		}
	}

	@Test
	public void esborrarDocument() throws Exception {
		byte[] response = CustodiaCaibHelper.getCustodiaClient(
				URL,
				USERNAME,
				PASSWORD).eliminarDocumento_v2(
				USERNAME,
				PASSWORD,
				DOCUMENT_ID);
		byte[] iniciXml = new byte[5];
		for (int i = 0; i < 5; i++)
			iniciXml[i] = response[i];
		if ("<?xml".equals(new String(iniciXml))) {
			CustodiaResponse resposta = CustodiaCaibHelper.parseResponse(response);
			Assert.fail("Error al eliminar document de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
		}
	}

}
