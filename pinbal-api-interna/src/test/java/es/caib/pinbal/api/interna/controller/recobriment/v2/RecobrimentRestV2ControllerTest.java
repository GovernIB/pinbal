package es.caib.pinbal.api.interna.controller.recobriment.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.pinbal.client.procediments.ProcedimentBasic;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.DadesComunesResposta;
import es.caib.pinbal.client.recobriment.v2.Entitat;
import es.caib.pinbal.client.recobriment.v2.PeticioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioConfirmacioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaSincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioSincrona;
import es.caib.pinbal.client.recobriment.v2.ValorEnum;
import es.caib.pinbal.client.serveis.ServeiBasic;
import es.caib.pinbal.core.dto.apiresponse.ServiceExecutionException;
import es.caib.pinbal.core.service.RecobrimentService;
import es.caib.pinbal.core.service.exception.AccessDenegatException;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class RecobrimentRestV2ControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecobrimentService recobrimentService;

    @InjectMocks
    private RecobrimentRestV2Controller recobrimentRestV2Controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Configurar el MockMvc amb el convertidor personalitzat
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper());

        // Configurem MockMvc directament sense context de WebApplicationContext
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(recobrimentRestV2Controller)
                .setMessageConverters(converter)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    // TESTS getEntitats
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetEntitats_ReturnsOkWithEntitats() throws Exception {
        List<Entitat> entitats = Arrays.asList(
                Entitat.builder().codi("ENTITAT_001").nom("Entitat 1").build(),
                Entitat.builder().codi("ENTITAT_002").nom("Entitat 2").build()
        );

        when(recobrimentService.getEntitats()).thenReturn(entitats);

        mockMvc.perform(get("/recobriment/v2/entitats")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].codi").value("ENTITAT_001"))
                .andExpect(jsonPath("$.[0].nom").value("Entitat 1"))
                .andExpect(jsonPath("$.[1].codi").value("ENTITAT_002"))
                .andExpect(jsonPath("$.[1].nom").value("Entitat 2"));
    }

    @Test
    public void testGetEntitats_ReturnsNoContentWhenEmpty() throws Exception {
        when(recobrimentService.getEntitats()).thenReturn(new ArrayList<Entitat>());

        mockMvc.perform(get("/recobriment/v2/entitats")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetEntitats_ReturnsForbiddenWhenAccessDenied() throws Exception {
        when(recobrimentService.getEntitats()).thenThrow(new AccessDenegatException(Arrays.asList("PBL_WS")));

        mockMvc.perform(get("/recobriment/v2/entitats")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetEntitats_ReturnsInternalServerErrorWhenServiceException() throws Exception {
        when(recobrimentService.getEntitats()).thenThrow(new ServiceExecutionException("Error occurred"));

        mockMvc.perform(get("/recobriment/v2/entitats")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // TESTS getProcediments
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetProcediments_ReturnsOkWithProcediments() throws Exception {
        List<ProcedimentBasic> procediments = Arrays.asList(
                ProcedimentBasic.builder().codi("PROC_001").nom("Procediment 1").build(),
                ProcedimentBasic.builder().codi("PROC_002").nom("Procediment 2").build()
        );

        when(recobrimentService.getProcediments("ENTITAT01")).thenReturn(procediments);

        mockMvc.perform(get("/recobriment/v2/entitats/ENTITAT01/procediments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].codi").value("PROC_001"))
                .andExpect(jsonPath("$.[0].nom").value("Procediment 1"))
                .andExpect(jsonPath("$.[1].codi").value("PROC_002"))
                .andExpect(jsonPath("$.[1].nom").value("Procediment 2"));
    }

    @Test
    public void testGetProcediments_ReturnsNoContentWhenEmpty() throws Exception {
        when(recobrimentService.getProcediments("ENTITAT01")).thenReturn(new ArrayList<ProcedimentBasic>());

        mockMvc.perform(get("/recobriment/v2/entitats/ENTITAT01/procediments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetProcediments_ReturnsNotFoundWhenEntitatNotFound() throws Exception {
        when(recobrimentService.getProcediments("ENTITAT01")).thenThrow(new EntitatNotFoundException("Entity not found"));

        mockMvc.perform(get("/recobriment/v2/entitats/ENTITAT01/procediments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetProcediments_ReturnsForbiddenWhenAccessDenied() throws Exception {
        when(recobrimentService.getProcediments("ENTITAT01")).thenThrow(new AccessDenegatException(Arrays.asList("PBL_WS")));

        mockMvc.perform(get("/recobriment/v2/entitats/ENTITAT01/procediments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetProcediments_ReturnsInternalServerErrorWhenServiceException() throws Exception {
        when(recobrimentService.getProcediments("ENTITAT01")).thenThrow(new ServiceExecutionException("Error occurred"));

        mockMvc.perform(get("/recobriment/v2/entitats/ENTITAT01/procediments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // TESTS getServeis
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetServeis_ReturnsOkWithServeis() throws Exception {
        List<ServeiBasic> serveis = Arrays.asList(
                ServeiBasic.builder().codi("SERVEI_001").descripcio("Servei 1").build(),
                ServeiBasic.builder().codi("SERVEI_002").descripcio("Servei 2").build()
        );

        when(recobrimentService.getServeis()).thenReturn(serveis);

        mockMvc.perform(get("/recobriment/v2/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].codi").value("SERVEI_001"))
                .andExpect(jsonPath("$.[0].descripcio").value("Servei 1"))
                .andExpect(jsonPath("$.[1].codi").value("SERVEI_002"))
                .andExpect(jsonPath("$.[1].descripcio").value("Servei 2"));
    }

    @Test
    public void testGetServeis_ReturnsNoContentWhenEmpty() throws Exception {
        when(recobrimentService.getServeis()).thenReturn(new ArrayList<ServeiBasic>());

        mockMvc.perform(get("/recobriment/v2/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetServeis_ReturnsForbiddenWhenAccessDenied() throws Exception {
        when(recobrimentService.getServeis()).thenThrow(new AccessDenegatException(Arrays.asList("PBL_WS")));

        mockMvc.perform(get("/recobriment/v2/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetServeis_ReturnsInternalServerErrorWhenServiceException() throws Exception {
        when(recobrimentService.getServeis()).thenThrow(new ServiceExecutionException("Error occurred"));

        mockMvc.perform(get("/recobriment/v2/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // TESTS getServeisByEntitat
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetServeisPerEntitat_ReturnsOkWithServeis() throws Exception {
        List<ServeiBasic> serveis = Arrays.asList(
                ServeiBasic.builder().codi("SERVEI_001").descripcio("Servei 1").build(),
                ServeiBasic.builder().codi("SERVEI_002").descripcio("Servei 2").build()
        );

        when(recobrimentService.getServeisByEntitat("ENTITAT01")).thenReturn(serveis);

        mockMvc.perform(get("/recobriment/v2/entitats/ENTITAT01/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].codi").value("SERVEI_001"))
                .andExpect(jsonPath("$.[0].descripcio").value("Servei 1"))
                .andExpect(jsonPath("$.[1].codi").value("SERVEI_002"))
                .andExpect(jsonPath("$.[1].descripcio").value("Servei 2"));
    }

    @Test
    public void testGetServeisPerEntitat_ReturnsNoContentWhenEmpty() throws Exception {
        when(recobrimentService.getServeisByEntitat("ENTITAT01")).thenReturn(new ArrayList<ServeiBasic>());

        mockMvc.perform(get("/recobriment/v2/entitats/ENTITAT01/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetServeisPerEntitat_ReturnsNotFoundWhenEntityNotFound() throws Exception {
        when(recobrimentService.getServeisByEntitat("ENTITAT01")).thenThrow(new EntitatNotFoundException("Entity not found"));

        mockMvc.perform(get("/recobriment/v2/entitats/ENTITAT01/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetServeisPerEntitat_ReturnsForbiddenWhenAccessDenied() throws Exception {
        when(recobrimentService.getServeisByEntitat("ENTITAT01")).thenThrow(new AccessDenegatException(Arrays.asList("PBL_WS")));

        mockMvc.perform(get("/recobriment/v2/entitats/ENTITAT01/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetServeisPerEntitat_ReturnsInternalServerErrorWhenServiceException() throws Exception {
        when(recobrimentService.getServeisByEntitat("ENTITAT01")).thenThrow(new ServiceExecutionException("Error occurred"));

        mockMvc.perform(get("/recobriment/v2/entitats/ENTITAT01/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // TESTS getServeisByProcediment
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetServeisPerProcediment_ReturnsOkWithServeis() throws Exception {
        List<ServeiBasic> serveis = Arrays.asList(
                ServeiBasic.builder().codi("SERVEI_001").descripcio("Servei 1").build(),
                ServeiBasic.builder().codi("SERVEI_002").descripcio("Servei 2").build()
        );

        when(recobrimentService.getServeisByProcediment("ENT001", "PROC001")).thenReturn(serveis);

        mockMvc.perform(get("/recobriment/v2/entitats/ENT001/procediments/PROC001/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].codi").value("SERVEI_001"))
                .andExpect(jsonPath("$.[0].descripcio").value("Servei 1"))
                .andExpect(jsonPath("$.[1].codi").value("SERVEI_002"))
                .andExpect(jsonPath("$.[1].descripcio").value("Servei 2"));
    }

    @Test
    public void testGetServeisPerProcediment_ReturnsNoContentWhenEmpty() throws Exception {
        when(recobrimentService.getServeisByProcediment("ENT001", "PROC001")).thenReturn(new ArrayList<ServeiBasic>());

        mockMvc.perform(get("/recobriment/v2/entitats/ENT001/procediments/PROC001/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetServeisPerProcediment_ReturnsNotFoundWhenProcedimentNotFound() throws Exception {
        when(recobrimentService.getServeisByProcediment("ENT001", "PROC001")).thenThrow(new ProcedimentNotFoundException("Procediment not found"));

        mockMvc.perform(get("/recobriment/v2/entitats/ENT001/procediments/PROC001/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetServeisPerProcediment_ReturnsForbiddenWhenAccessDenied() throws Exception {
        when(recobrimentService.getServeisByProcediment("ENT001", "PROC001")).thenThrow(new AccessDenegatException(Arrays.asList("PBL_WS")));

        mockMvc.perform(get("/recobriment/v2/entitats/ENT001/procediments/PROC001/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetServeisPerProcediment_ReturnsInternalServerErrorWhenServiceException() throws Exception {
        when(recobrimentService.getServeisByProcediment("ENT001", "PROC001")).thenThrow(new ServiceExecutionException("Error occurred"));

        mockMvc.perform(get("/recobriment/v2/entitats/ENT001/procediments/PROC001/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // TESTS getDadesEspecifiques
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetDadesEspecifiques() throws Exception {
        List<DadaEspecifica> dadesEspecifiques = Arrays.asList(
                DadaEspecifica.builder().codi("DADA_001").etiqueta("Dada 1").build(),
                DadaEspecifica.builder().codi("DADA_002").etiqueta("Dada 2").build()
        );

        when(recobrimentService.getDadesEspecifiquesByServei("SERVEI001")).thenReturn(dadesEspecifiques);

        mockMvc.perform(get("/recobriment/v2/serveis/SERVEI001/dadesEspecifiques")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].codi").value("DADA_001"))
                .andExpect(jsonPath("$.[0].etiqueta").value("Dada 1"))
                .andExpect(jsonPath("$.[1].codi").value("DADA_002"))
                .andExpect(jsonPath("$.[1].etiqueta").value("Dada 2"));
    }

    @Test
    public void testGetDadesEspecifiques_ReturnsNoContentWhenEmpty() throws Exception {
        when(recobrimentService.getDadesEspecifiquesByServei("SERVEI001")).thenReturn(new ArrayList<DadaEspecifica>());

        mockMvc.perform(get("/recobriment/v2/serveis/SERVEI001/dadesEspecifiques")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetDadesEspecifiques_ReturnsNotFoundWhenServeiNotFound() throws Exception {
        when(recobrimentService.getDadesEspecifiquesByServei("SERVEI001")).thenThrow(new ServeiNotFoundException("Servei not found"));

        mockMvc.perform(get("/recobriment/v2/serveis/SERVEI001/dadesEspecifiques")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetDadesEspecifiques_ReturnsAccessDenied() throws Exception {
        when(recobrimentService.getDadesEspecifiquesByServei("SERVEI001")).thenThrow(new AccessDenegatException(Arrays.asList("PBL_WS")));

        mockMvc.perform(get("/recobriment/v2/serveis/SERVEI001/dadesEspecifiques")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetDadesEspecifiques_ReturnsInternalServerErrorWhenServiceException() throws Exception {
        when(recobrimentService.getDadesEspecifiquesByServei("SERVEI001")).thenThrow(new ServiceExecutionException("Error occurred"));

        mockMvc.perform(get("/recobriment/v2/serveis/SERVEI001/dadesEspecifiques")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // TESTS getValorsEnum
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetValorsEnum_ReturnsOkWithValors() throws Exception {
        List<ValorEnum> valorsEnum = Arrays.asList(
                ValorEnum.builder().codi("ENUM_001").valor("Valor 1").build(),
                ValorEnum.builder().codi("ENUM_002").valor("Valor 2").build()
        );

        when(recobrimentService.getValorsEnumByServei("SERVEI001", "CAMP001", "ENUM001", "filtre"))
                .thenReturn(valorsEnum);

        mockMvc.perform(get("/recobriment/v2/serveis/SERVEI001/camps/CAMP001/enumerat/ENUM001")
                        .param("filtre", "filtre")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].codi").value("ENUM_001"))
                .andExpect(jsonPath("$.[0].valor").value("Valor 1"))
                .andExpect(jsonPath("$.[1].codi").value("ENUM_002"))
                .andExpect(jsonPath("$.[1].valor").value("Valor 2"));
    }

    @Test
    public void testGetValorsEnum_ReturnsNoContentWhenEmpty() throws Exception {
        when(recobrimentService.getValorsEnumByServei("SERVEI001", "CAMP001", "ENUM001", "filtre"))
                .thenReturn(new ArrayList<ValorEnum>());

        mockMvc.perform(get("/recobriment/v2/serveis/SERVEI001/camps/CAMP001/enumerat/ENUM001")
                        .param("filtre", "filtre")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetValorsEnum_ReturnsForbiddenWhenAccessDenied() throws Exception {
        when(recobrimentService.getValorsEnumByServei("SERVEI001", "CAMP001", "ENUM001", "filtre"))
                .thenThrow(new AccessDenegatException(Arrays.asList("PBL_WS")));

        mockMvc.perform(get("/recobriment/v2/serveis/SERVEI001/camps/CAMP001/enumerat/ENUM001")
                        .param("filtre", "filtre")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetValorsEnum_ReturnsNotFoundWhenServeiNotFound() throws Exception {
        when(recobrimentService.getValorsEnumByServei("SERVEI001", "CAMP001", "ENUM001", "filtre"))
                .thenThrow(new ServeiNotFoundException("Servei not found"));

        mockMvc.perform(get("/recobriment/v2/serveis/SERVEI001/camps/CAMP001/enumerat/ENUM001")
                        .param("filtre", "filtre")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetValorsEnum_ReturnsInternalServerErrorWhenServiceException() throws Exception {
        when(recobrimentService.getValorsEnumByServei("SERVEI001", "CAMP001", "ENUM001", "filtre"))
                .thenThrow(new ServiceExecutionException("Error occurred"));

        mockMvc.perform(get("/recobriment/v2/serveis/SERVEI001/camps/CAMP001/enumerat/ENUM001")
                        .param("filtre", "filtre")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // TESTS peticioSincrona
    // /////////////////////////////////////////////////////////

    @Test
    public void testPeticioSincrona_ReturnsOkWithValidRequest() throws Exception {
        PeticioSincrona peticio = PeticioSincrona.builder().build();
        PeticioRespostaSincrona resposta = PeticioRespostaSincrona.builder().error(false).build();
        Map<String, List<String>> respostaValidacio = new java.util.HashMap<>();

        when(recobrimentService.validatePeticio(anyString(), eq(peticio))).thenReturn(respostaValidacio);
        when(recobrimentService.peticionSincrona(peticio)).thenReturn(resposta);

        mockMvc.perform(post("/recobriment/v2/serveis/SERVEI001/peticioSincrona")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(peticio)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false));
    }

    @Test
    public void testPeticioSincrona_ReturnsOkWithValidationErrors() throws Exception {
        PeticioSincrona peticio = PeticioSincrona.builder().build();
        java.util.Map<String, List<String>> errors = new java.util.HashMap<>();
        errors.put("field1", Arrays.asList("Error 1", "Error 2"));

        PeticioRespostaSincrona resposta = PeticioRespostaSincrona.builder()
                .error(true)
                .errorsValidacio(errors)
                .messageError("S'han produït errors en la validació de les dades de la petició.")
                .build();

        when(recobrimentService.validatePeticio(anyString(), eq(peticio))).thenReturn(errors);

        mockMvc.perform(post("/recobriment/v2/serveis/SERVEI001/peticioSincrona")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(peticio)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.messageError").value(containsString("errors en la validaci")));
    }

    @Test
    public void testPeticioSincrona_ReturnsInternalServerErrorWhenExceptionOccurs() throws Exception {
        PeticioSincrona peticio = PeticioSincrona.builder().build();

        when(recobrimentService.validatePeticio(anyString(), eq(peticio))).thenThrow(new ServiceExecutionException("Error occurred"));

        mockMvc.perform(post("/recobriment/v2/serveis/SERVEI001/peticioSincrona")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(peticio)))
                .andExpect(status().isInternalServerError());
    }


    // TESTS peticioAsincrona
    // /////////////////////////////////////////////////////////

    @Test
    public void testPeticioAsincrona_ReturnsOkWithValidRequest() throws Exception {
        PeticioAsincrona peticio = PeticioAsincrona.builder().build();
        PeticioConfirmacioAsincrona resposta = PeticioConfirmacioAsincrona.builder().error(false).build();
        Map<String, List<String>> respostaValidacio = new java.util.HashMap<>();

        when(recobrimentService.validatePeticio(anyString(), eq(peticio))).thenReturn(respostaValidacio);
        when(recobrimentService.peticionAsincrona(peticio)).thenReturn(resposta);

        mockMvc.perform(post("/recobriment/v2/serveis/SERVEI001/peticioAsincrona")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(peticio)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false));
    }

    @Test
    public void testPeticioAsincrona_ReturnsOkWithValidationErrors() throws Exception {
        PeticioAsincrona peticio = PeticioAsincrona.builder().build();
        java.util.Map<String, List<String>> errors = new java.util.HashMap<>();
        errors.put("field1", Arrays.asList("Error 1", "Error 2"));

        PeticioRespostaAsincrona resposta = PeticioRespostaAsincrona.builder()
                .error(true)
                .errorsValidacio(errors)
                .messageError("S'han produït errors en la validació de les dades de la petició.")
                .build();

        when(recobrimentService.validatePeticio(anyString(), eq(peticio))).thenReturn(errors);

        mockMvc.perform(post("/recobriment/v2/serveis/SERVEI001/peticioAsincrona")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(peticio)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.messageError").value(containsString("errors en la validaci")));
    }

    @Test
    public void testPeticioAsincrona_ReturnsInternalServerErrorWhenExceptionOccurs() throws Exception {
        PeticioAsincrona peticio = PeticioAsincrona.builder().build();

        when(recobrimentService.validatePeticio(anyString(), eq(peticio))).thenThrow(new ServiceExecutionException("Error occurred"));

        mockMvc.perform(post("/recobriment/v2/serveis/SERVEI001/peticioAsincrona")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(peticio)))
                .andExpect(status().isInternalServerError());
    }


    // TESTS getResposta
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetResposta_ReturnsOkWithResponse() throws Exception {
        PeticioRespostaAsincrona mockResposta = new PeticioRespostaAsincrona();
        mockResposta.setDadesComunes(DadesComunesResposta.builder().idPeticio("001").build());

        when(recobrimentService.getResposta("REQUEST001")).thenReturn(mockResposta);

        mockMvc.perform(get("/recobriment/v2/consultes/REQUEST001/resposta")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.dadesComunes.idPeticio").value("001"));
    }

    @Test
    public void testGetResposta_ReturnsNoContentWhenEmpty() throws Exception {
        when(recobrimentService.getResposta("REQUEST001")).thenReturn(null);

        mockMvc.perform(get("/recobriment/v2/consultes/REQUEST001/resposta")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetResposta_ReturnsNotFoundWhenConsultaNotFound() throws Exception {
        when(recobrimentService.getResposta("REQUEST001")).thenThrow(new ConsultaNotFoundException());

        mockMvc.perform(get("/recobriment/v2/consultes/REQUEST001/resposta")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetResposta_ReturnsInternalServerErrorWhenServiceException() throws Exception {
        when(recobrimentService.getResposta("REQUEST001")).thenThrow(new ServiceExecutionException("An error occurred"));

        mockMvc.perform(get("/recobriment/v2/consultes/REQUEST001/resposta")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }


    // TESTS getJustificant
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetJustificant_ReturnsOkWithValidResponse() throws Exception {
        ScspJustificante justificant = new ScspJustificante();
        justificant.setNom("Justificant");
        justificant.setContentType("application/pdf");
        justificant.setContingut(new byte[]{1, 2, 3, 4});

        when(recobrimentService.getJustificant("idPeticio", "idSolicitud")).thenReturn(justificant);

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificant")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Justificant"))
                .andExpect(jsonPath("$.contentType").value("application/pdf"))
                .andExpect(jsonPath("$.contingut").exists());
    }

    @Test
    public void testGetJustificant_ReturnsNoContentWhenEmpty() throws Exception {
        when(recobrimentService.getJustificant("idPeticio", "idSolicitud")).thenReturn(null);

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificant")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetJustificant_ReturnsNotFoundWhenConsultaNotFound() throws Exception {
        when(recobrimentService.getJustificant("idPeticio", "idSolicitud")).thenThrow(new ConsultaNotFoundException());

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificant")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetJustificant_ReturnsInternalServerErrorWhenServiceException() throws Exception {
        when(recobrimentService.getJustificant("idPeticio", "idSolicitud")).thenThrow(new ServiceExecutionException("Error occurred"));

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificant")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // TESTS getJustificantImprimible
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetJustificantImprimible_ReturnsOkWithValidResponse() throws Exception {
        ScspJustificante justificant = new ScspJustificante();
        justificant.setNom("Justificant Imprimible");
        justificant.setContentType("application/pdf");
        justificant.setContingut(new byte[]{1, 2, 3, 4});

        when(recobrimentService.getJustificantImprimible("idPeticio", "idSolicitud")).thenReturn(justificant);

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificantImprimible")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Justificant Imprimible"))
                .andExpect(jsonPath("$.contentType").value("application/pdf"))
                .andExpect(jsonPath("$.contingut").exists());
    }

    @Test
    public void testGetJustificantImprimible_ReturnsNoContentWhenEmpty() throws Exception {
        when(recobrimentService.getJustificantImprimible("idPeticio", "idSolicitud")).thenReturn(null);

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificantImprimible")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetJustificantImprimible_ReturnsNotFoundWhenConsultaNotFound() throws Exception {
        when(recobrimentService.getJustificantImprimible("idPeticio", "idSolicitud"))
                .thenThrow(new ConsultaNotFoundException());

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificantImprimible")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetJustificantImprimible_ReturnsInternalServerErrorWhenServiceException() throws Exception {
        when(recobrimentService.getJustificantImprimible("idPeticio", "idSolicitud"))
                .thenThrow(new ServiceExecutionException("Error occurred"));

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificantImprimible")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }


    // TESTS getJustificantCsv
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetJustificantCsv_ReturnsOkWithValidCsv() throws Exception {
        String expectedCsv = "field1,field2\nvalue1,value2";
        when(recobrimentService.getJustificantCsv("idPeticio", "idSolicitud")).thenReturn(expectedCsv);

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificantCsv")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedCsv));
    }

    @Test
    public void testGetJustificantCsv_ReturnsNoContentWhenEmpty() throws Exception {
        when(recobrimentService.getJustificantCsv("idPeticio", "idSolicitud")).thenReturn(null);

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificantCsv")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetJustificantCsv_ReturnsNotFoundWhenConsultaNotFound() throws Exception {
        when(recobrimentService.getJustificantCsv("idPeticio", "idSolicitud")).thenThrow(new ConsultaNotFoundException());

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificantCsv")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetJustificantCsv_ReturnsInternalServerErrorWhenServiceException() throws Exception {
        when(recobrimentService.getJustificantCsv("idPeticio", "idSolicitud")).thenThrow(new ServiceExecutionException("Error occurred"));

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificantCsv")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // TESTS getJustificantUuid
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetJustificantUuid_ReturnsOkWithValidResponse() throws Exception {
        String expectedUuid = "123e4567-e89b-12d3-a456-426614174000";

        when(recobrimentService.getJustificantUuid("idPeticio", "idSolicitud")).thenReturn(expectedUuid);

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificantUuid")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedUuid));
    }

    @Test
    public void testGetJustificantUuid_ReturnsNoContentWhenEmpty() throws Exception {
        when(recobrimentService.getJustificantUuid("idPeticio", "idSolicitud")).thenReturn(null);

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificantUuid")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetJustificantUuid_ReturnsNotFoundWhenConsultaNotFound() throws Exception {
        when(recobrimentService.getJustificantUuid("idPeticio", "idSolicitud")).thenThrow(new ConsultaNotFoundException());

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificantUuid")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetJustificantUuid_ReturnsInternalServerErrorWhenServiceException() throws Exception {
        when(recobrimentService.getJustificantUuid("idPeticio", "idSolicitud")).thenThrow(new ServiceExecutionException("Error occurred"));

        mockMvc.perform(get("/recobriment/v2/consultes/idPeticio/solicitud/idSolicitud/justificantUuid")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}