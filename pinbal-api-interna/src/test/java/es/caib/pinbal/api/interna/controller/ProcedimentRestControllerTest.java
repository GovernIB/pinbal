package es.caib.pinbal.api.interna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.pinbal.client.comu.OptionalField;
import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.procediments.ProcedimentPatch;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.core.dto.apiresponse.ServiceExecutionException;
import es.caib.pinbal.core.service.GestioRestService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcedimentRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GestioRestService gestioRestService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ProcedimentRestController procedimentRestController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

//        // Configuració manual de MappingJackson2HttpMessageConverter amb Jackson2HalModule
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new Jackson2HalModule());
//        converter.setObjectMapper(objectMapper);
        // Configurar el MockMvc amb el convertidor personalitzat
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper());

        // Configurem MockMvc directament sense context de WebApplicationContext
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(procedimentRestController)
                .setMessageConverters(converter)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    private void setUpSecurityContext() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "user",
                "password",
                AuthorityUtils.createAuthorityList("ROLE_PBL_WS")
        ));
    }

    // CREATE Procediment
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testCreateProcediment_Success() throws Exception {
        setUpSecurityContext();

        Procediment createdProcediment = Procediment.builder()
                .id(1L)
                .codi("PROC_001")
                .nom("Test Procediment")
                .entitatCodi("ENT_001")
                .organGestorDir3("ORG_001")
                .build();

        when(gestioRestService.create(any(Procediment.class))).thenReturn(createdProcediment);

        // Crear Resource del Procediment creat
//        Resource<Procediment> procedimentResource = new Resource<>(createdProcediment);
        Resource<Procediment> procedimentResource = new Resource<>(
                createdProcediment,
                Collections.singletonList(new Link("http://localhost/procediments/1", "self")));

        // Convertir Resource a JSON per la resposta esperada
        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new Jackson2HalModule());
        String expectedJson = objectMapper.writeValueAsString(procedimentResource);

        try {
            // Realitzar la petició POST amb MockMvc i verificar la resposta
            mockMvc.perform(post("/procediments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createdProcediment)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith("application/json"))
                    .andExpect(content().string(expectedJson));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateProcediment_InvalidInput() throws Exception {
        setUpSecurityContext();

        mockMvc.perform(post("/procediments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codi\":\"\",\"nom\":\"\",\"entitatCodi\":\"\",\"organGestorDir3\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateProcediment_ServiceException() throws Exception {
        setUpSecurityContext();

        Procediment procediment = Procediment.builder()
                .codi("PROC_001")
                .nom("Test Procediment")
                .entitatCodi("ENT_001")
                .organGestorDir3("ORG_001")
                .build();

        when(gestioRestService.create(any(Procediment.class))).thenThrow(new ServiceExecutionException("Service error"));

        mockMvc.perform(post("/procediments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codi\":\"PROC_001\",\"nom\":\"Test Procediment\",\"entitatCodi\":\"ENT_001\",\"organGestorDir3\":\"ORG_001\"}"))
                .andExpect(status().isInternalServerError());
    }


    // UPDATE procediment
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testUpdateProcediment_Success() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;
        Procediment updatedProcediment = Procediment.builder()
                .id(procedimentId)
                .codi("PROC_001")
                .nom("Updated Procediment")
                .entitatCodi("ENT_001")
                .organGestorDir3("ORG_001")
                .build();

        when(gestioRestService.update(any(Procediment.class))).thenReturn(updatedProcediment);

        // Create Resource for the updated Procediment
        Resource<Procediment> procedimentResource = new Resource<>(
                updatedProcediment,
                Collections.singletonList(new Link("http://localhost/procediments/1", "self")));

        // Convert Resource to JSON for the expected response
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(procedimentResource);

        // Perform POST request with MockMvc and verify response
        mockMvc.perform(post("/procediments/{procedimentId}", procedimentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProcediment)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().string(expectedJson));
    }

    @Test
    public void testUpdateProcediment_InvalidInput() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;

        mockMvc.perform(post("/procediments/{procedimentId}", procedimentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codi\":\"\",\"nom\":\"\",\"entitatCodi\":\"\",\"organGestorDir3\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateProcediment_ProcedimentNotFound() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;
        Procediment procediment = Procediment.builder()
                .id(procedimentId)
                .codi("PROC_001")
                .nom("Updated Procediment")
                .entitatCodi("ENT_001")
                .organGestorDir3("ORG_001")
                .build();

        when(gestioRestService.update(any(Procediment.class))).thenThrow(new ProcedimentNotFoundException());

        mockMvc.perform(post("/procediments/{procedimentId}", procedimentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"codi\":\"PROC_001\",\"nom\":\"Updated Procediment\",\"entitatCodi\":\"ENT_001\",\"organGestorDir3\":\"ORG_001\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateProcediment_ServiceException() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;
        Procediment procediment = Procediment.builder()
                .id(procedimentId)
                .codi("PROC_001")
                .nom("Updated Procediment")
                .entitatCodi("ENT_001")
                .organGestorDir3("ORG_001")
                .build();

        when(gestioRestService.update(any(Procediment.class))).thenThrow(new ServiceExecutionException("Service error"));

        mockMvc.perform(post("/procediments/{procedimentId}", procedimentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"codi\":\"PROC_001\",\"nom\":\"Updated Procediment\",\"entitatCodi\":\"ENT_001\",\"organGestorDir3\":\"ORG_001\"}"))
                .andExpect(status().isInternalServerError());
    }


    // PATCH Procediment
    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void testPatchProcediment_Success() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;
        ProcedimentPatch procedimentPatch = ProcedimentPatch.builder()
                .codi(OptionalField.of("PROC_001"))
                .nom(OptionalField.of("Updated Procediment"))
                .build();

        Procediment updatedProcediment = Procediment.builder()
                .id(procedimentId)
                .codi("PROC_001")
                .nom("Updated Procediment")
                .entitatCodi("ENT_001")
                .organGestorDir3("ORG_001")
                .build();

        when(gestioRestService.updateParcial(any(Long.class), any(ProcedimentPatch.class))).thenReturn(updatedProcediment);

        Resource<Procediment> procedimentResource = new Resource<>(
                updatedProcediment,
                Collections.singletonList(new Link("http://localhost/procediments/1", "self")));

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(procedimentResource);

        mockMvc.perform(patch("/procediments/{procedimentId}", procedimentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(procedimentPatch)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().string(expectedJson));
    }

    @Test
    public void testPatchProcediment_InvalidInput() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;
        ProcedimentPatch procedimentPatch = ProcedimentPatch.builder()
                .codi(OptionalField.of(""))
                .nom(OptionalField.of(""))
                .build();

        mockMvc.perform(patch("/procediments/{procedimentId}", procedimentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPatchProcediment_ProcedimentNotFound() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;
        ProcedimentPatch procedimentPatch = ProcedimentPatch.builder()
                .codi(OptionalField.of("PROC_001"))
                .nom(OptionalField.of("Updated Procediment"))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        when(gestioRestService.updateParcial(any(Long.class), any(ProcedimentPatch.class))).thenThrow(new ProcedimentNotFoundException());

        mockMvc.perform(patch("/procediments/{procedimentId}", procedimentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(procedimentPatch)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPatchProcediment_ServiceException() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;
        ProcedimentPatch procedimentPatch = ProcedimentPatch.builder()
                .codi(OptionalField.of("PROC_001"))
                .nom(OptionalField.of("Updated Procediment"))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        when(gestioRestService.updateParcial(any(Long.class), any(ProcedimentPatch.class))).thenThrow(new ServiceExecutionException("Service error"));

        mockMvc.perform(patch("/procediments/{procedimentId}", procedimentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(procedimentPatch)))
                .andExpect(status().isInternalServerError());
    }


    // ENABLE Servei en Procediment
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testEnableServeiToProcediment_Success() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;
        String serveiCodi = "SER_001";

        // No need to setup mocks since the method does not return anything

        mockMvc.perform(post("/procediments/{procedimentId}/serveis/{serveiCodi}/enable", procedimentId, serveiCodi)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testEnableServeiToProcediment_ProcedimentNotFound() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;
        String serveiCodi = "SER_001";

        doThrow(new ProcedimentNotFoundException()).when(gestioRestService).serveiEnable(procedimentId, serveiCodi);

        mockMvc.perform(post("/procediments/{procedimentId}/serveis/{serveiCodi}/enable", procedimentId, serveiCodi)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testEnableServeiToProcediment_ServeiNotFound() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;
        String serveiCodi = "SER_001";

        doThrow(new ServeiNotFoundException()).when(gestioRestService).serveiEnable(procedimentId, serveiCodi);

        mockMvc.perform(post("/procediments/{procedimentId}/serveis/{serveiCodi}/enable", procedimentId, serveiCodi)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testEnableServeiToProcediment_ServiceException() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;
        String serveiCodi = "SER_001";

        doThrow(new ServiceExecutionException("Service error")).when(gestioRestService).serveiEnable(procedimentId, serveiCodi);

        mockMvc.perform(post("/procediments/{procedimentId}/serveis/{serveiCodi}/enable", procedimentId, serveiCodi)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // GET Procediments
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetProcediments_Success() throws Exception {
        setUpSecurityContext();

        String entitatCodi = "ENT_001";
        String codi = "PROC";
        String nom = "Test";
        String organGestor = "ORG";

        List<Procediment> procedimentList = new ArrayList<>();
        procedimentList.add(Procediment.builder().id(1L).codi("PROC_001").nom("Test Procediment").entitatCodi(entitatCodi).organGestorDir3(organGestor).build());
        Page<Procediment> procedimentPage = new PageImpl<>(procedimentList, new PageRequest(0, 10), 1);

        when(gestioRestService.findProcedimentsPaginat(eq(entitatCodi), eq(codi), eq(nom), eq(organGestor), any(PageRequest.class))).thenReturn(procedimentPage);

        mockMvc.perform(get("/procediments")
                        .param("entitatCodi", entitatCodi)
                        .param("codi", codi)
                        .param("nom", nom)
                        .param("organGestor", organGestor)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    public void testGetProcediments_NoContent() throws Exception {
        setUpSecurityContext();

        String entitatCodi = "ENT_001";
        String codi = "";
        String nom = "";
        String organGestor = "";
        Page<Procediment> procedimentPage = new PageImpl<>(new ArrayList<Procediment>(), new PageRequest(0, 10), 0);

        when(gestioRestService.findProcedimentsPaginat(entitatCodi, codi, nom, organGestor, new PageRequest(0, 10))).thenReturn(procedimentPage);

        mockMvc.perform(get("/procediments")
                        .param("entitatCodi", entitatCodi)
                        .param("codi", codi)
                        .param("nom", nom)
                        .param("organGestor", organGestor)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetProcediments_EntitatNotFoundException() throws Exception {
        setUpSecurityContext();

        String entitatCodi = "ENT_INVALID";
        String codi = "";
        String nom = "";
        String organGestor = "";

        when(gestioRestService.findProcedimentsPaginat(entitatCodi, codi, nom, organGestor, new PageRequest(0, 10))).thenThrow(new EntitatNotFoundException());

        mockMvc.perform(get("/procediments")
                        .param("entitatCodi", entitatCodi)
                        .param("codi", codi)
                        .param("nom", nom)
                        .param("organGestor", organGestor)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetProcediments_ServiceExecutionException() throws Exception {
        setUpSecurityContext();

        String entitatCodi = "ENT_001";
        String codi = "";
        String nom = "";
        String organGestor = "";

        when(gestioRestService.findProcedimentsPaginat(entitatCodi, codi, nom, organGestor, new PageRequest(0, 10))).thenThrow(new ServiceExecutionException("Service error"));

        mockMvc.perform(get("/procediments")
                        .param("entitatCodi", entitatCodi)
                        .param("codi", codi)
                        .param("nom", nom)
                        .param("organGestor", organGestor)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isInternalServerError());
    }


    // GET Procediment by ID
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetProcediment_Success() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;
        Procediment procediment = Procediment.builder()
                .id(procedimentId)
                .codi("PROC_001")
                .nom("Test Procediment")
                .entitatCodi("ENT_001")
                .organGestorDir3("ORG_001")
                .build();

        when(gestioRestService.getProcedimentById(procedimentId)).thenReturn(procediment);

        mockMvc.perform(get("/procediments/{procedimentId}", procedimentId))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id").value(procedimentId.intValue()));
    }

    @Test
    public void testGetProcediment_NotFound() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 999L; // non-existing ID

        when(gestioRestService.getProcedimentById(procedimentId)).thenReturn(null);

        mockMvc.perform(get("/procediments/{procedimentId}", procedimentId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetProcediment_ServiceException() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;

        when(gestioRestService.getProcedimentById(procedimentId)).thenThrow(new ServiceExecutionException("Service error"));

        mockMvc.perform(get("/procediments/{procedimentId}", procedimentId))
                .andExpect(status().isInternalServerError());
    }


    // GET Procediment by codi
    // //////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetProcedimentByCodi_Success() throws Exception {
        setUpSecurityContext();

        String procedimentCodi = "PROC_001";
        String entitatCodi = "ENT_001";

        Procediment procediment = Procediment.builder()
                .id(1L)
                .codi(procedimentCodi)
                .nom("Test Procediment")
                .entitatCodi(entitatCodi)
                .organGestorDir3("ORG_001")
                .build();

        when(gestioRestService.getProcedimentAmbEntitatICodi(entitatCodi, procedimentCodi)).thenReturn(procediment);

        Resource<Procediment> resource = new Resource<>(
                procediment,
                Collections.singletonList(new Link("http://localhost/procediments/byCodi/" + procedimentCodi + "?entitatCodi=" + entitatCodi, "self"))
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(resource);

        mockMvc.perform(get("/procediments/byCodi/" + procedimentCodi)
                        .param("entitatCodi", entitatCodi))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().string(expectedJson));
    }

    @Test
    public void testGetProcedimentByCodi_NotFound() throws Exception {
        setUpSecurityContext();

        String procedimentCodi = "PROC_INVALID";
        String entitatCodi = "ENT_001";

        when(gestioRestService.getProcedimentAmbEntitatICodi(entitatCodi, procedimentCodi)).thenReturn(null);

        mockMvc.perform(get("/procediments/byCodi/" + procedimentCodi)
                        .param("entitatCodi", entitatCodi))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetProcedimentByCodi_ServiceException() throws Exception {
        setUpSecurityContext();

        String procedimentCodi = "PROC_001";
        String entitatCodi = "ENT_001";

        when(gestioRestService.getProcedimentAmbEntitatICodi(entitatCodi, procedimentCodi)).thenThrow(new ServiceExecutionException("Service error"));

        mockMvc.perform(get("/procediments/byCodi/" + procedimentCodi)
                        .param("entitatCodi", entitatCodi))
                .andExpect(status().isInternalServerError());
    }


    // GET Procediment Serveis
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetProcedimentServeis_Success() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;

        List<Servei> serveiList = new ArrayList<>();
        serveiList.add(Servei.builder().codi("SERV_001").descripcio("Servei 1").build());

        Page<Servei> serveiPage = new PageImpl<>(serveiList, new PageRequest(0, 10), 1);

        when(gestioRestService.findServeisByProcedimentPaginat(eq(procedimentId), any(PageRequest.class))).thenReturn(serveiPage);

        mockMvc.perform(get("/procediments/{procedimentId}/serveis", procedimentId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    public void testGetProcedimentServeis_NoContent() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;

        Page<Servei> serveiPage = new PageImpl<>(new ArrayList<Servei>(), new PageRequest(0, 10), 0);

        when(gestioRestService.findServeisByProcedimentPaginat(eq(procedimentId), any(PageRequest.class))).thenReturn(serveiPage);

        mockMvc.perform(get("/procediments/{procedimentId}/serveis", procedimentId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetProcedimentServeis_ProcedimentNotFoundException() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;

        when(gestioRestService.findServeisByProcedimentPaginat(eq(procedimentId), any(PageRequest.class))).thenThrow(new ProcedimentNotFoundException());

        mockMvc.perform(get("/procediments/{procedimentId}/serveis", procedimentId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetProcedimentServeis_ServiceExecutionException() throws Exception {
        setUpSecurityContext();

        Long procedimentId = 1L;

        when(gestioRestService.findServeisByProcedimentPaginat(eq(procedimentId), any(PageRequest.class))).thenThrow(new ServiceExecutionException("Service error"));

        mockMvc.perform(get("/procediments/{procedimentId}/serveis", procedimentId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isInternalServerError());
    }


    // GET Procediment Serveis by codi
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetProcedimentServeisByCodi_Success() throws Exception {
        setUpSecurityContext();

        String procedimentCodi = "PRO_001";
        String entitatCodi = "ENT_001";

        Procediment procediment = Procediment.builder().id(1L).build();
        List<Servei> serveiList = new ArrayList<>();
        serveiList.add(Servei.builder().codi("SERV_001").descripcio("Servei 1").build());

        Page<Servei> serveiPage = new PageImpl<>(serveiList, new PageRequest(0, 10), 1);

        when(gestioRestService.getProcedimentAmbEntitatICodi(eq(entitatCodi), eq(procedimentCodi))).thenReturn(procediment);
        when(gestioRestService.findServeisByProcedimentPaginat(eq(procediment.getId()), any(PageRequest.class))).thenReturn(serveiPage);

        mockMvc.perform(get("/procediments/byCodi/{procedimentCodi}/serveis", procedimentCodi)
                        .param("entitatCodi", entitatCodi)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    public void testGetProcedimentServeisByCodi_NoContent() throws Exception {
        setUpSecurityContext();

        String procedimentCodi = "PRO_001";
        String entitatCodi = "ENT_001";

        Procediment procediment = Procediment.builder().id(1L).build();
        Page<Servei> serveiPage = new PageImpl<>(new ArrayList<Servei>(), new PageRequest(0, 10), 0);

        when(gestioRestService.getProcedimentAmbEntitatICodi(eq(entitatCodi), eq(procedimentCodi))).thenReturn(procediment);
        when(gestioRestService.findServeisByProcedimentPaginat(eq(procediment.getId()), any(PageRequest.class))).thenReturn(serveiPage);

        mockMvc.perform(get("/procediments/byCodi/{procedimentCodi}/serveis", procedimentCodi)
                        .param("entitatCodi", entitatCodi)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetProcedimentServeisByCodi_ProcedimentNotFoundException() throws Exception {
        setUpSecurityContext();

        String procedimentCodi = "PRO_001";
        String entitatCodi = "ENT_001";

        when(gestioRestService.getProcedimentAmbEntitatICodi(eq(entitatCodi), eq(procedimentCodi))).thenReturn(null);

        mockMvc.perform(get("/procediments/byCodi/{procedimentCodi}/serveis", procedimentCodi)
                        .param("entitatCodi", entitatCodi)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetProcedimentServeisByCodi_ServiceExecutionException() throws Exception {
        setUpSecurityContext();

        String procedimentCodi = "PRO_001";
        String entitatCodi = "ENT_001";

        when(gestioRestService.getProcedimentAmbEntitatICodi(eq(entitatCodi), eq(procedimentCodi))).thenThrow(new ServiceExecutionException("Service error"));

        mockMvc.perform(get("/procediments/byCodi/{procedimentCodi}/serveis", procedimentCodi)
                        .param("entitatCodi", entitatCodi)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isInternalServerError());
    }
}