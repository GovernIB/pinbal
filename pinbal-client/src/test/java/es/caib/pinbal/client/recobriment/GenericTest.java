/**
 * 
 */
package es.caib.pinbal.client.recobriment;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import es.caib.pinbal.client.recobriment.model.ScspFuncionario;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.ScspSolicitante.ScspConsentimiento;
import es.caib.pinbal.client.recobriment.model.ScspTitular;
import es.caib.pinbal.client.recobriment.model.ScspTitular.ScspTipoDocumentacion;
import es.caib.pinbal.client.recobriment.model.Solicitud;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

/**
 * Test del client genèric del recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GenericTest {

	private static final String URL_BASE = "http://localhost:8080/pinbalapi";
	private static final String USUARI = "user";
	private static final String CONTRASENYA = "passwd";
	private static final String SERVEI_SCSP = "pruebaPMI";
	private static final String PETICION_SCSP_ID = "PBL0000000001292";
//	private static final boolean ENABLE_LOGGING = true;

	private ClientGeneric client = new ClientGeneric(URL_BASE, USUARI, CONTRASENYA);

//	public void peticioSincrona() throws Exception {
//
//		ClientGeneric client = new ClientGeneric(URL_BASE, USUARI, CONTRASENYA);
//
//		// Funcionari
//		ScspFuncionario funcionario = new ScspFuncionario();
//		funcionario.setNifFuncionario("00000000T");
//		funcionario.setNombreCompletoFuncionario("Funcionari CAIB");
//
//		// Titular
//		ScspTitular titular = new ScspTitular();
//		titular.setTipoDocumentacion(ScspTipoDocumentacion.NIF);
//		titular.setDocumentacion("12345678Z");
//
//		// Dades especifiqeus
//		String xmlDadesEspecifiques = "";
//		StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//		xmlBuilder.append("<DatosEspecificos>");
//		xmlBuilder.append("<Consulta>");
//		xmlBuilder.append("<CodigoProvincia>07</CodigoProvincia>");
//		xmlBuilder.append("</Consulta>");
//		xmlBuilder.append("</DatosEspecificos>");
//		xmlDadesEspecifiques = xmlBuilder.toString();
//
//
//		Solicitud solicitud = new Solicitud();
//
//		// Solicitant
//		solicitud.setIdentificadorSolicitante("B07167448");
//		solicitud.setNombreSolicitante("Solicitant");
//		// Procediment
//		solicitud.setCodigoProcedimiento("Procediment");
//		// Unitat tramitadora
//		solicitud.setUnidadTramitadora("Unitat de test");
//		solicitud.setCodigoUnidadTramitadora("A04123456");
//		solicitud.setFinalidad("Finalitat solicitut");
//		solicitud.setConsentimiento(ScspConsentimiento.Si);
//		solicitud.setIdExpediente("ID-Expedient");
//
//		solicitud.setFuncionario(funcionario);
//		solicitud.setTitular(titular);
//
//		solicitud.setDatosEspecificos(xmlDadesEspecifiques);
//
//		if (ENABLE_LOGGING) {
//			client.enableLogginFilter();
//		}
//
//		ScspRespuesta respuesta = client.peticionSincrona(SERVEI_SCSP, Arrays.asList(solicitud));
//
//	}

	@Test
	public void peticionSincrona() throws UniformInterfaceException, ClientHandlerException, IOException {
		Solicitud solicitud = new Solicitud();
		solicitud.setIdentificadorSolicitante("B07167448");
		//solicitud.setNombreSolicitante("Limit Tecnologies");
		solicitud.setCodigoProcedimiento("ProvaConcepte");
		solicitud.setUnidadTramitadora("Departament de test");
		//solicitud.setCodigoUnidadTramitadora(codigoUnidadTramitadora);
		solicitud.setFinalidad("Test peticionSincrona");
		solicitud.setConsentimiento(ScspConsentimiento.Si);
		ScspFuncionario funcionario = new ScspFuncionario();
		funcionario.setNifFuncionario("00000000T");
		funcionario.setNombreCompletoFuncionario("Funcionari CAIB");
		solicitud.setFuncionario(funcionario);
		ScspTitular titular = new ScspTitular();
		titular.setTipoDocumentacion(ScspTipoDocumentacion.NIF);
		titular.setDocumentacion("12345678Z");
		solicitud.setTitular(titular);
		ScspRespuesta respuesta = client.peticionSincrona(
				SERVEI_SCSP,
				Arrays.asList(solicitud));
		assertNotNull(respuesta);
		System.out.println("-> peticionSincrona(" + SERVEI_SCSP + ") = " + objectToJsonString(respuesta));
	}

	@Test
	public void getRespuesta() throws IOException {
		ScspRespuesta respuesta = client.getRespuesta(PETICION_SCSP_ID);
		assertNotNull(respuesta);
		System.out.println("-> getRespuesta(" + PETICION_SCSP_ID + ") = " + objectToJsonString(respuesta));
	}

	@Test
	public void getJustificante() throws IOException {
		ScspJustificante justificante = client.getJustificante(PETICION_SCSP_ID);
		assertNotNull(justificante);
		System.out.println("-> getJustificante");
		System.out.println("\tnom: " + justificante.getNom());
		System.out.println("\tcontentType: " + justificante.getContentType());
		System.out.println("\tcontingut: " + justificante.getContingut());
	}

	private String objectToJsonString(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		//mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		//mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setSerializationInclusion(Include.NON_NULL);
		return mapper.writeValueAsString(obj);
	}

}
