/**
 * 
 */
package es.caib.pinbal.client.recobriment;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
import es.caib.pinbal.client.recobriment.svdccaacpcws01.ClientSvdccaacpcws01;
import es.caib.pinbal.client.recobriment.svdccaacpcws01.ClientSvdccaacpcws01.SolicitudSvdccaacpcws01;

/**
 * Test del client genèric del recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Svdccaacpcws01Test {

//	private static final String URL_BASE = "http://localhost:8080/pinbalapi";
//	private static final String USUARI = "user";
//	private static final String CONTRASENYA = "passwd";
//	private static final String ENTITAT_CIF = "B07167448";
//	private static final String CODIGO_PROCEDIMIENTO = "ProvaConcepte";
//	private static final String PETICION_SCSP_ID = "PBL0000000001292";
	private static final String ENTITAT_CIF = "S0711001H";
	private static final String URL_BASE = "https://proves.caib.es/pinbalapi";
	private static final String USUARI = "xxxxxx";
	private static final String CONTRASENYA = "xxxxxx";
	private static final String CODIGO_PROCEDIMIENTO = "CODSVDR_GBA_20121107";
	private static final String PETICION_SCSP_ID = "PINBAL00000000000000263715";
	private static final boolean ENABLE_LOGGING = false;
	private static final boolean IS_JBOSS = true;

	private final ClientSvdccaacpcws01 client = new ClientSvdccaacpcws01(URL_BASE, USUARI, CONTRASENYA, !IS_JBOSS, null, null);

	@Test
	public void peticionSincrona() throws UniformInterfaceException, ClientHandlerException, IOException {
		SolicitudSvdccaacpcws01 solicitud = new SolicitudSvdccaacpcws01();
		solicitud.setIdentificadorSolicitante(ENTITAT_CIF);
		solicitud.setCodigoProcedimiento(CODIGO_PROCEDIMIENTO);
		solicitud.setUnidadTramitadora("Departament de test");
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
		solicitud.setCodigoComunidadAutonoma("04");
		solicitud.setCodigoProvincia("07");
		if (ENABLE_LOGGING) {
			client.enableLogginFilter();
		}
		ScspRespuesta respuesta = client.peticionSincrona(Arrays.asList(solicitud));
		assertNotNull(respuesta);
		System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
	}

	@Test
	public void getRespuesta() throws IOException {
		if (ENABLE_LOGGING) {
			client.enableLogginFilter();
		}
		ScspRespuesta respuesta = client.getRespuesta(PETICION_SCSP_ID);
		assertNotNull(respuesta);
		System.out.println("-> getRespuesta(" + PETICION_SCSP_ID + ") = " + objectToJsonString(respuesta));
	}

	@Test
	public void getJustificante() throws IOException {
		if (ENABLE_LOGGING) {
			client.enableLogginFilter();
		}
		ScspJustificante justificante = client.getJustificante(PETICION_SCSP_ID);
		assertNotNull(justificante);
		System.out.println("-> getJustificante");
		System.out.println("\tnom: " + justificante.getNom());
		System.out.println("\tcontentType: " + justificante.getContentType());
		System.out.println("\tcontingut: " + justificante.getContingut());
	}

	private String objectToJsonString(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper.writeValueAsString(obj);
	}

}
