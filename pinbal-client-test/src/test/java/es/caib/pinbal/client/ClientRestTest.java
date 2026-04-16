package es.caib.pinbal.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.recobriment.model.*;
import es.caib.pinbal.client.recobriment.svddgpciws02.ClientSvddgpciws02;
import es.caib.pinbal.client.recobriment.v2.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ClientRestTest {

//    private static final String ENTITAT_CIF = "S0711001H";
//    private static final String URL_BASE = "https://proves.caib.es/pinbalapi";
//    private static final String USUARI = "$ripea_pinbal";
//    private static final String CONTRASENYA = "******";
//    private static final String CODIGO_PROCEDIMIENTO = "CODSVDR_GBA_20121107";
    private static final String CODIGO_PROCEDIMIENTO = "TEST";
    private static final String SERVEI_CODI = "SCDCPAJU";
    private static final String ENTITAT_CIF = "12345678Z";
    private static final String URL_BASE = "http://localhost:8180/pinbalapi";
    private static final String USUARI = "admin";
    private static final String CONTRASENYA = "admin";
//    private static final String PETICION_SCSP_ID = "PINBAL00000000000000265474";
//    private static final String PETICION_SCSP_ID = "PINPRE00000000000000004447";
    private static final String PETICION_SCSP_ID = "PINBAL00000000000000390676";
    private static final boolean ENABLE_LOGGING = true;
    private static final boolean BASIC_AUTH = true;

    private ClientSvddgpciws02 client = new ClientSvddgpciws02(URL_BASE, USUARI, CONTRASENYA, BASIC_AUTH, null, null);

    @Test
    public void peticionSincrona() throws UniformInterfaceException, ClientHandlerException, IOException {
        ClientSvddgpciws02.SolicitudSvddgpciws02 solicitud = new ClientSvddgpciws02.SolicitudSvddgpciws02();
        solicitud.setIdentificadorSolicitante(ENTITAT_CIF);
        solicitud.setCodigoProcedimiento(CODIGO_PROCEDIMIENTO);
        solicitud.setUnidadTramitadora("Departament de test");
        solicitud.setCodigoUnidadTramitadora("Departament codi");
        solicitud.setFinalidad("Test peticionSincrona");
        solicitud.setConsentimiento(ScspSolicitante.ScspConsentimiento.Si);
        ScspFuncionario funcionario = new ScspFuncionario();
        funcionario.setNifFuncionario("00000000T");
        funcionario.setNombreCompletoFuncionario("Funcionari CAIB");
        solicitud.setFuncionario(funcionario);
        ScspTitular titular = new ScspTitular();
        titular.setTipoDocumentacion(ScspTitular.ScspTipoDocumentacion.DNI);
        titular.setDocumentacion("12345678Z");
        titular.setNombre("Nom");
        titular.setApellido1("Llinatge1");
        titular.setApellido2("Llinatge2");
        solicitud.setTitular(titular);
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


    @Test
    public void peticionSincrona_success() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = ENTITAT_CIF;
        String procedimentCodi = CODIGO_PROCEDIMIENTO;
        String serveiCodi = SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);

        try {
            ClientRecobriment clientRecobriment = new ClientRecobriment(URL_BASE, USUARI, CONTRASENYA, LogLevel.DEBUG);
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertFalse(respuesta.isError(), "La resposta indica que s'ha produit un error en l'enviament");
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    private static PeticioSincrona getPeticioSincrona(
            String entitatCif,
            String procedimentCodi,
            String serveiCodi,
            Map<String, String> dadesEspecifiques) {

        PeticioSincrona peticio = PeticioSincrona.builder()
                .dadesComunes(DadesComunes.builder()
                        .entitatCif(entitatCif)
                        .procedimentCodi(procedimentCodi)
                        .serveiCodi(serveiCodi)
                        .funcionari(Funcionari.builder()
//                                .codi(FUNCIONARI_CODI)
                                .nif("44444444A")
                                .nom("FUNCIONARI_NOM")
                                .build())
                        .departament("Departament de test")
                        .consentiment(DadesComunes.Consentiment.Si)
                        .finalitat("Test peticionSincrona")
                        .build())
                .solicitud(SolicitudSimple.builder()
                        .titular(Titular.builder()
                                .documentTipus(Titular.DocumentTipus.DNI)
                                .documentNumero("43105084W")
                                .nom("Usuari")
                                .llinatge1("Test")
                                .build())
                        .expedient("testPinbal/999")
                        .dadesEspecifiques(dadesEspecifiques)
                        .build())
                .build();
        return peticio;
    }

}
