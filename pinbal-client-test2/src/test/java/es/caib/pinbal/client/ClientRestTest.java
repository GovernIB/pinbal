package es.caib.pinbal.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import es.caib.pinbal.client.recobriment.model.ScspFuncionario;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.ScspSolicitante;
import es.caib.pinbal.client.recobriment.model.ScspTitular;
import es.caib.pinbal.client.recobriment.svdccaacpasws01.ClientSvdccaacpasws01;
import es.caib.pinbal.client.recobriment.svdccaacpasws01.ClientSvdccaacpasws01.SolicitudSvdccaacpasws01;
import es.caib.pinbal.client.recobriment.svddgpciws02.ClientSvddgpciws02;
import es.caib.pinbal.client.recobriment.svddgpciws02.ClientSvddgpciws02.SolicitudSvddgpciws02;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClientRestTest {

    private static final String ENTITAT_CIF = "12345678Z";
//    private static final String URL_BASE = "https://proves.caib.es/pinbalapi";
//    private static final String USUARI = "$ripea_pinbal";
//    private static final String CONTRASENYA = "ripea_pinbal";
    private static final String URL_BASE = "http://localhost:8180/pinbalapi";
    private static final String USUARI = "admin";
    private static final String CONTRASENYA = "admin";
    private static final String CODIGO_PROCEDIMIENTO = "TEST";
    private static final String PETICION_SCSP_ID = "PINBAL00000000000000265474";
    private static final boolean ENABLE_LOGGING = true;
    private static final boolean BASIC_AUTH = true;

    private ClientSvddgpciws02 client_ = new ClientSvddgpciws02(URL_BASE, USUARI, CONTRASENYA, BASIC_AUTH, null, null);
    private ClientSvdccaacpasws01 client = new ClientSvdccaacpasws01(URL_BASE, USUARI, CONTRASENYA, BASIC_AUTH, null, null);

    @Test
    public void peticionSincrona() throws UniformInterfaceException, ClientHandlerException, IOException {
        SolicitudSvdccaacpasws01 solicitud = new SolicitudSvdccaacpasws01();
//        SolicitudSvddgpciws02 solicitud = new SolicitudSvddgpciws02();
        solicitud.setIdentificadorSolicitante(ENTITAT_CIF);
        solicitud.setCodigoProcedimiento(CODIGO_PROCEDIMIENTO);
        solicitud.setUnidadTramitadora("Departament de test");
        solicitud.setCodigoUnidadTramitadora("A04003127");
        solicitud.setFinalidad("Test peticionSincrona");
        solicitud.setConsentimiento(ScspSolicitante.ScspConsentimiento.Si);
        ScspFuncionario funcionario = new ScspFuncionario();
        funcionario.setNifFuncionario("00000000T");
        funcionario.setNombreCompletoFuncionario("Funcionari CAIB");
        solicitud.setFuncionario(funcionario);
        ScspTitular titular = new ScspTitular();
        titular.setTipoDocumentacion(ScspTitular.ScspTipoDocumentacion.DNI);
        titular.setDocumentacion("12345678Z");
        titular.setNombre("Antoni");
        titular.setApellido1("Garau");
        titular.setApellido2("Jaume");
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
        System.out.println("\tcontingut tamany: " + justificante.getContingut().length);
        System.out.println("\tcontingut: " + new String(justificante.getContingut()));
    }

    private String objectToJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsString(obj);
    }

}