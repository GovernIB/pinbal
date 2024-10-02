package es.caib.pinbal.client.recobriment;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspFuncionario;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.ScspSolicitante.ScspConsentimiento;
import es.caib.pinbal.client.recobriment.model.ScspTitular;
import es.caib.pinbal.client.recobriment.model.ScspTitular.ScspTipoDocumentacion;
import es.caib.pinbal.client.recobriment.svddgpciws02.ClientSvddgpciws02;
import es.caib.pinbal.client.recobriment.svddgpciws02.ClientSvddgpciws02.SolicitudSvddgpciws02;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

/**
 * Test del client genèric del recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Svddgpciws02Test {

	private static final String ENTITAT_CIF = "S0711001H";
	private static final String URL_BASE = "https://proves.caib.es/pinbalapi";
	private static final String USUARI = "$ripea_pinbal";
	private static final String CONTRASENYA = "******";
	private static final String CODIGO_PROCEDIMIENTO = "CODSVDR_GBA_20121107";
//	private static final String ENTITAT_CIF = "12345678Z";
//	private static final String URL_BASE = "http://localhost:8180/pinbalapi";
//	private static final String USUARI = "admin";
//	private static final String CONTRASENYA = "******";
//	private static final String CODIGO_PROCEDIMIENTO = "TEST";

	private static final String PETICION_SCSP_ID = "PINBAL00000000000000336780";
	private static final boolean ENABLE_LOGGING = true;
	private static final boolean BASIC_AUTH = true;

	private ClientSvddgpciws02 client = new ClientSvddgpciws02(URL_BASE, USUARI, CONTRASENYA, BASIC_AUTH, null, null);

	@Test
	public void peticionSincrona() throws UniformInterfaceException, ClientHandlerException, IOException {
		SolicitudSvddgpciws02 solicitud = new SolicitudSvddgpciws02();
		solicitud.setIdentificadorSolicitante(ENTITAT_CIF);
		solicitud.setCodigoProcedimiento(CODIGO_PROCEDIMIENTO);
		solicitud.setUnidadTramitadora("Departament de test");
		solicitud.setFinalidad("Test peticionSincrona");
		solicitud.setIdExpediente("testPinbal/799");
		solicitud.setConsentimiento(ScspConsentimiento.Si);
		ScspFuncionario funcionario = new ScspFuncionario();
		funcionario.setNifFuncionario("00000000T");
		funcionario.setNombreCompletoFuncionario("Funcionari CAIB");
		solicitud.setFuncionario(funcionario);
		ScspTitular titular = new ScspTitular();
		titular.setTipoDocumentacion(ScspTipoDocumentacion.DNI);
		titular.setDocumentacion("12345678Z");
		titular.setNombre("Antoni");
		titular.setApellido1("Garau");
		titular.setApellido2("Jaume");
		solicitud.setTitular(titular);
		if (ENABLE_LOGGING) {
			client.enableLogginFilter();
		}
		solicitud.setAnioNacimiento("1970");
		ScspRespuesta respuesta = client.peticionSincrona(Arrays.asList(solicitud));
		assertNotNull(respuesta);
		System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));

	}

	@Test
	public void peticionAsincrona() throws UniformInterfaceException, ClientHandlerException, IOException {
		SolicitudSvddgpciws02 solicitud = new SolicitudSvddgpciws02();
		solicitud.setIdentificadorSolicitante(ENTITAT_CIF);
		solicitud.setCodigoProcedimiento(CODIGO_PROCEDIMIENTO);
		solicitud.setUnidadTramitadora("Departament de test");
		solicitud.setFinalidad("Test peticionSincrona");
		solicitud.setIdExpediente("testPinbal/799");
		solicitud.setConsentimiento(ScspConsentimiento.Si);
		ScspFuncionario funcionario = new ScspFuncionario();
		funcionario.setNifFuncionario("00000000T");
		funcionario.setNombreCompletoFuncionario("Funcionari CAIB");
		solicitud.setFuncionario(funcionario);
		ScspTitular titular = new ScspTitular();
		titular.setTipoDocumentacion(ScspTipoDocumentacion.DNI);
		titular.setDocumentacion("12345678Z");
		titular.setNombre("Antoni");
		titular.setApellido1("Garau");
		titular.setApellido2("Jaume");
		solicitud.setTitular(titular);
		if (ENABLE_LOGGING) {
			client.enableLogginFilter();
		}
		ScspConfirmacionPeticion confirmacionPeticion = client.peticionAsincrona(Arrays.asList(solicitud));
		assertNotNull(confirmacionPeticion);
		System.out.println("-> peticionSincrona = " + objectToJsonString(confirmacionPeticion));

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
		System.out.println("\tcontingut tamany: " + justificante.getContingut().length);
		System.out.println("\tcontingut: " + new String(justificante.getContingut()));
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
