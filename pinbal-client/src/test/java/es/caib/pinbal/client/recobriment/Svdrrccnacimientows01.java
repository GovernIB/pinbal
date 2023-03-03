package es.caib.pinbal.client.recobriment;

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
import es.caib.pinbal.client.recobriment.model.Sexe;
import es.caib.pinbal.client.recobriment.model.SolicitudBaseSvdrrcc;
import es.caib.pinbal.client.recobriment.model.SolicitudBaseSvdrrcc.FetRegistral;
import es.caib.pinbal.client.recobriment.model.SolicitudBaseSvdrrcc.Lloc;
import es.caib.pinbal.client.recobriment.model.SolicitudBaseSvdrrcc.TitularDadesAdicionals;
import es.caib.pinbal.client.recobriment.svdrrccdefuncionws01.ClientSvdrrccdefuncionws01;
import es.caib.pinbal.client.recobriment.svdrrccnacimientows01.ClientSvdrrccnacimientows01;
import es.caib.pinbal.client.recobriment.svdrrccnacimientows01.ClientSvdrrccnacimientows01.SolicitudSvdrrccnacimientows01;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class Svdrrccnacimientows01 {

    private static final String ENTITAT_CIF = "S0711001H";
    private static final String URL_BASE = "https://proves.caib.es/pinbalapi";
    private static final String USUARI = "$ripea_pinbal";
    private static final String CONTRASENYA = "ripea_pinbal";
    private static final String CODIGO_PROCEDIMIENTO = "CODSVDR_GBA_20121107";
    private static final String PETICION_SCSP_ID = "PINBAL00000000000000265512";
    private static final boolean ENABLE_LOGGING = true;
    private static final boolean BASIC_AUTH = true;

    private ClientSvdrrccnacimientows01 client = new ClientSvdrrccnacimientows01(URL_BASE, USUARI, CONTRASENYA, BASIC_AUTH, null, null);

    @Test
    public void peticionSincrona() throws UniformInterfaceException, ClientHandlerException, IOException, ParseException {
        SolicitudSvdrrccnacimientows01 solicitud = new SolicitudSvdrrccnacimientows01();
        solicitud.setIdentificadorSolicitante(ENTITAT_CIF);
        solicitud.setCodigoProcedimiento(CODIGO_PROCEDIMIENTO);
        solicitud.setUnidadTramitadora("Departament de test");
        solicitud.setFinalidad("Test peticionSincrona");
        solicitud.setIdExpediente("testPinbal/989");
        solicitud.setConsentimiento(ScspSolicitante.ScspConsentimiento.Si);
        ScspFuncionario funcionario = new ScspFuncionario();
        funcionario.setNifFuncionario("00000000T");
        funcionario.setNombreCompletoFuncionario("Funcionari CAIB");
        solicitud.setFuncionario(funcionario);
        ScspTitular titular = new ScspTitular();
        titular.setTipoDocumentacion(ScspTitular.ScspTipoDocumentacion.NIF);
        titular.setDocumentacion("12345678Z");
        titular.setNombre("Usuario");
        titular.setApellido1("Prueba");
        titular.setApellido2("Test");
        solicitud.setTitular(titular);

        solicitud.setTitularDadesAdicionals(TitularDadesAdicionals.builder()
                .fetregistral(FetRegistral.builder()
                        .data(new SimpleDateFormat("dd/MM/yyyy").parse("08/02/2012"))
                        .municipi(Lloc.builder()
                                .codi("07033")
                                .descripcio("Manacor")
                                .build())
                        .build())
                .naixement(SolicitudBaseSvdrrcc.Naixement.builder()
                        .data(new SimpleDateFormat("dd/MM/yyyy").parse("07/02/2012"))
                        .paisCodi("724")
                        .provincia(Lloc.builder()
//                                .codi("07")
                                .descripcio("Illes Balears")
                                .build())
                        .municipi(Lloc.builder()
                                .codi("07033")
                                .descripcio("Manacor")
                                .build())
                        .build())
                .ausenciaSegonLlinatge(false)
                .nomMare("Pepita")
                .nomPare("Josep")
                .sexe(Sexe.HOME)
                .build());
        solicitud.setDadesRegistrals(SolicitudBaseSvdrrcc.DadesRegistrals.builder()
                .registreCivil("012345678901")
                .tom("00002_AB")
                .pagina("25")
                .build());

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
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsString(obj);
    }
}
