package es.caib.pinbal.api.interna.controller.recobriment.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.ValorEnum;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.core.dto.apiresponse.ServiceExecutionException;
import es.caib.pinbal.core.service.RecobrimentService;
import es.caib.pinbal.core.service.exception.AccessDenegatException;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    // TESTS getProcediments
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetProcediments_ReturnsOkWithProcediments() throws Exception {
        List<Procediment> procediments = Arrays.asList(
                Procediment.builder().id(1L).codi("PROC_001").nom("Procediment 1").build(),
                Procediment.builder().id(1L).codi("PROC_002").nom("Procediment 2").build()
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
        when(recobrimentService.getProcediments("ENTITAT01")).thenReturn(new ArrayList<Procediment>());

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
        List<Servei> serveis = Arrays.asList(
                Servei.builder().codi("SERVEI_001").descripcio("Servei 1").build(),
                Servei.builder().codi("SERVEI_002").descripcio("Servei 2").build()
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
        when(recobrimentService.getServeis()).thenReturn(new ArrayList<Servei>());

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
        List<Servei> serveis = Arrays.asList(
                Servei.builder().codi("SERVEI_001").descripcio("Servei 1").build(),
                Servei.builder().codi("SERVEI_002").descripcio("Servei 2").build()
        );

        when(recobrimentService.getServeisByEntitat("ENTITAT01")).thenReturn(serveis);

        mockMvc.perform(get("/recobriment/v2/entitat/ENTITAT01/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].codi").value("SERVEI_001"))
                .andExpect(jsonPath("$.[0].descripcio").value("Servei 1"))
                .andExpect(jsonPath("$.[1].codi").value("SERVEI_002"))
                .andExpect(jsonPath("$.[1].descripcio").value("Servei 2"));
    }

    @Test
    public void testGetServeisPerEntitat_ReturnsNoContentWhenEmpty() throws Exception {
        when(recobrimentService.getServeisByEntitat("ENTITAT01")).thenReturn(new ArrayList<Servei>());

        mockMvc.perform(get("/recobriment/v2/entitat/ENTITAT01/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetServeisPerEntitat_ReturnsNotFoundWhenEntityNotFound() throws Exception {
        when(recobrimentService.getServeisByEntitat("ENTITAT01")).thenThrow(new EntitatNotFoundException("Entity not found"));

        mockMvc.perform(get("/recobriment/v2/entitat/ENTITAT01/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetServeisPerEntitat_ReturnsForbiddenWhenAccessDenied() throws Exception {
        when(recobrimentService.getServeisByEntitat("ENTITAT01")).thenThrow(new AccessDenegatException(Arrays.asList("PBL_WS")));

        mockMvc.perform(get("/recobriment/v2/entitat/ENTITAT01/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetServeisPerEntitat_ReturnsInternalServerErrorWhenServiceException() throws Exception {
        when(recobrimentService.getServeisByEntitat("ENTITAT01")).thenThrow(new ServiceExecutionException("Error occurred"));

        mockMvc.perform(get("/recobriment/v2/entitat/ENTITAT01/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // TESTS getServeisByProcediment
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetServeisPerProcediment_ReturnsOkWithServeis() throws Exception {
        List<Servei> serveis = Arrays.asList(
                Servei.builder().codi("SERVEI_001").descripcio("Servei 1").build(),
                Servei.builder().codi("SERVEI_002").descripcio("Servei 2").build()
        );

        when(recobrimentService.getServeisByProcediment("PROC001")).thenReturn(serveis);

        mockMvc.perform(get("/recobriment/v2/procediment/PROC001/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].codi").value("SERVEI_001"))
                .andExpect(jsonPath("$.[0].descripcio").value("Servei 1"))
                .andExpect(jsonPath("$.[1].codi").value("SERVEI_002"))
                .andExpect(jsonPath("$.[1].descripcio").value("Servei 2"));
    }

    @Test
    public void testGetServeisPerProcediment_ReturnsNoContentWhenEmpty() throws Exception {
        when(recobrimentService.getServeisByProcediment("PROC001")).thenReturn(new ArrayList<Servei>());

        mockMvc.perform(get("/recobriment/v2/procediment/PROC001/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetServeisPerProcediment_ReturnsNotFoundWhenProcedimentNotFound() throws Exception {
        when(recobrimentService.getServeisByProcediment("PROC001")).thenThrow(new ProcedimentNotFoundException("Procediment not found"));

        mockMvc.perform(get("/recobriment/v2/procediment/PROC001/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetServeisPerProcediment_ReturnsForbiddenWhenAccessDenied() throws Exception {
        when(recobrimentService.getServeisByProcediment("PROC001")).thenThrow(new AccessDenegatException(Arrays.asList("PBL_WS")));

        mockMvc.perform(get("/recobriment/v2/procediment/PROC001/serveis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetServeisPerProcediment_ReturnsInternalServerErrorWhenServiceException() throws Exception {
        when(recobrimentService.getServeisByProcediment("PROC001")).thenThrow(new ServiceExecutionException("Error occurred"));

        mockMvc.perform(get("/recobriment/v2/procediment/PROC001/serveis")
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

}