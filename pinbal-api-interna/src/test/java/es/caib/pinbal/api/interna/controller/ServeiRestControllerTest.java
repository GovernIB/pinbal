package es.caib.pinbal.api.interna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.core.service.GestioRestService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ServeiRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GestioRestService gestioRestService;

    @InjectMocks
    private ServeiRestController serveiRestController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Configurar el MockMvc amb el convertidor personalitzat
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper());

        // Configurem MockMvc directament sense context de WebApplicationContext
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(serveiRestController)
                .setMessageConverters(converter)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

//    private void setUpSecurityContext() {
//        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
//                "user",
//                "password",
//                AuthorityUtils.createAuthorityList("ROLE_PBL_WS")
//        ));
//    }

    // GET Serveis
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetServeis_withResults() throws Exception {
        Servei servei = new Servei();
        servei.setCodi("testCodi");
        servei.setDescripcio("testDescripcio");

        Pageable pageable = new PageRequest(0, 10);
        Page<Servei> serveiPage = new PageImpl<>(Collections.singletonList(servei), pageable, 1);
        given(gestioRestService.findServeisPaginat(anyString(), anyString(), any(Pageable.class))).willReturn(serveiPage);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/serveis")
                        .param("codi", "testCodi")
                        .param("descripcio", "testDescripcio")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(jsonResponse);
    }

    @Test
    public void testGetServeis_noResults() throws Exception {
        Pageable pageable = new PageRequest(0, 10);
        Page<Servei> serveiPage = new PageImpl<>(new ArrayList<Servei>(), pageable, 0);
        given(gestioRestService.findServeisPaginat(anyString(), anyString(), any(Pageable.class)))
                .willReturn(serveiPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/serveis")
                        .param("codi", "")
                        .param("descripcio", "")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetServeis_withException() throws Exception {
        given(gestioRestService.findServeisPaginat(anyString(), anyString(), any(Pageable.class)))
                .willThrow(new RuntimeException("Service exception"));

        mockMvc.perform(MockMvcRequestBuilders.get("/serveis")
                        .param("codi", "invalid")
                        .param("descripcio", "invalid")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }


    // GET Servei
    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void testGetServei_found() throws Exception {
        Servei servei = new Servei();
        servei.setCodi("testCodi");
        servei.setDescripcio("testDescripcio");

        given(gestioRestService.getServeiByCodi(anyString())).willReturn(servei);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/serveis/testCodi")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(jsonResponse);
    }

    @Test
    public void testGetServei_notFound() throws Exception {
        given(gestioRestService.getServeiByCodi(anyString())).willReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/serveis/invalidCodi")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetServei_withException() throws Exception {
        given(gestioRestService.getServeiByCodi(anyString()))
                .willThrow(new RuntimeException("Service exception"));

        mockMvc.perform(MockMvcRequestBuilders.get("/serveis/invalidCodi")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}