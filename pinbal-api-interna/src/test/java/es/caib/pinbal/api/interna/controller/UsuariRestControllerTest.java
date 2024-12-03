package es.caib.pinbal.api.interna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.pinbal.client.usuaris.FiltreUsuaris;
import es.caib.pinbal.client.usuaris.PermisosServei;
import es.caib.pinbal.client.usuaris.ProcedimentServei;
import es.caib.pinbal.client.usuaris.UsuariEntitat;
import es.caib.pinbal.core.dto.apiresponse.ServiceExecutionException;
import es.caib.pinbal.core.service.GestioRestService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.MultiplesUsuarisExternsException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.UsuariExternNotFoundException;
import es.caib.pinbal.core.service.exception.UsuariNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class UsuariRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GestioRestService gestioRestService;

    @InjectMocks
    private UsuariRestController usuariRestController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Configurar el MockMvc amb el convertidor personalitzat
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper());

        // Configurem MockMvc directament sense context de WebApplicationContext
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(usuariRestController)
                .setMessageConverters(converter)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    // CREATE o UPDATE Usuari
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void createOrUpdateUser_Success() throws Exception {
        UsuariEntitat usuariEntitat = new UsuariEntitat();
        usuariEntitat.setEntitatCodi("testCodi");
        usuariEntitat.setCodi("usauriCodi");

        Mockito.doNothing().when(gestioRestService).createOrUpdateUsuari(Mockito.any(UsuariEntitat.class));

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/usuaris")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuariEntitat)))
                .andExpect(status().isCreated());
    }

    @Test
    public void createOrUpdateUser_InvalidInput() throws Exception {
        mockMvc.perform(post("/usuaris")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"entitatCodi\": \"\"}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errorMessage").value(containsString("Un, i només un, dels camps codi, nif or nom ha d'estar emplenat")));
    }

    @Test
    public void createOrUpdateUser_EntitatNotFoundException() throws Exception {
        UsuariEntitat usuariEntitat = new UsuariEntitat();
        usuariEntitat.setEntitatCodi("testCodi");
        usuariEntitat.setCodi("usauriCodi");

        Mockito.doThrow(new EntitatNotFoundException("testCodi")).when(gestioRestService).createOrUpdateUsuari(Mockito.any(UsuariEntitat.class));

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/usuaris")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuariEntitat)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.errorMessage").value(containsString("Entitat no trobada")));
    }

    @Test
    public void createOrUpdateUser_UsuariExternNotFoundException() throws Exception {
        UsuariEntitat usuariEntitat = new UsuariEntitat();
        usuariEntitat.setEntitatCodi("testCodi");
        usuariEntitat.setCodi("usauriCodi");

        Mockito.doThrow(new UsuariExternNotFoundException("Usuari extern not found")).when(gestioRestService).createOrUpdateUsuari(Mockito.any(UsuariEntitat.class));

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/usuaris")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuariEntitat)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.errorMessage").value(containsString("Usuari extern no trobat")));
    }

    @Test
    public void createOrUpdateUser_MultiplesUsuarisExternsException() throws Exception {
        UsuariEntitat usuariEntitat = new UsuariEntitat();
        usuariEntitat.setEntitatCodi("testCodi");
        usuariEntitat.setNom("usauriNom");

        Mockito.doThrow(new MultiplesUsuarisExternsException()).when(gestioRestService).createOrUpdateUsuari(Mockito.any(UsuariEntitat.class));

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/usuaris")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuariEntitat)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errorMessage").value("Multiples usuaris coincideixen amb les dades aportades"));
    }

    @Test
    public void createOrUpdateUser_ServiceExecutionException() throws Exception {
        UsuariEntitat usuariEntitat = new UsuariEntitat();
        usuariEntitat.setEntitatCodi("testCodi");
        usuariEntitat.setCodi("usauriCodi");

        Mockito.doThrow(new ServiceExecutionException("Service execution error")).when(gestioRestService).createOrUpdateUsuari(Mockito.any(UsuariEntitat.class));

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/usuaris")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuariEntitat)))
                .andExpect(status().is(500));
//                .andExpect(jsonPath("$.errorMessage").value("Service execution error"));
    }

    // GET Usuaris
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void getUsuaris_Success() throws Exception {
        UsuariEntitat user1 = new UsuariEntitat();
        user1.setCodi("testCodi1");

        UsuariEntitat user2 = new UsuariEntitat();
        user2.setCodi("testCodi2");

        Pageable pageable = new PageRequest(0, 10);

        Mockito.when(gestioRestService.findUsuarisPaginat(Mockito.anyString(), Mockito.any(FiltreUsuaris.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(user1, user2), pageable, 2));

        mockMvc.perform(get("/usuaris")
                        .param("entitatCodi", "testEntitatCodi")
                        .param("filtreUsuaris", "{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].codi").value("testCodi1"))
                .andExpect(jsonPath("$.content[1].codi").value("testCodi2"));
    }

    @Test
    public void getUsuaris_NoContent() throws Exception {
        Pageable pageable = new PageRequest(0, 10);

        Mockito.when(gestioRestService.findUsuarisPaginat(Mockito.anyString(), Mockito.any(FiltreUsuaris.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<UsuariEntitat>(), pageable, 0));

        mockMvc.perform(get("/usuaris")
                        .param("entitatCodi", "testEntitatCodi")
                        .param("filtreUsuaris", "{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getUsuaris_EntitatNotFoundException() throws Exception {
        Mockito.when(gestioRestService.findUsuarisPaginat(Mockito.anyString(), Mockito.any(FiltreUsuaris.class), Mockito.any(Pageable.class)))
                .thenThrow(new EntitatNotFoundException("Entitat not found"));

        mockMvc.perform(get("/usuaris")
                        .param("entitatCodi", "testEntitatCodi")
                        .param("filtreUsuaris", "{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", containsString("Entitat not found")));
    }

    // GET Usuari
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void getUsuari_Success() throws Exception {
        UsuariEntitat user = new UsuariEntitat();
        user.setCodi("testCodi");

        Mockito.when(gestioRestService.getUsuariAmbEntitatICodi(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(user);

        mockMvc.perform(get("/usuaris/{usuariCodi}", "testCodi")
                        .param("entitatCodi", "testEntitatCodi")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codi").value("testCodi"));
    }

    @Test
    public void getUsuari_NotFound() throws Exception {
        Mockito.when(gestioRestService.getUsuariAmbEntitatICodi(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(null);

        mockMvc.perform(get("/usuaris/{usuariCodi}", "testCodi")
                        .param("entitatCodi", "testEntitatCodi")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getUsuari_EntitatNotFoundException() throws Exception {
        Mockito.when(gestioRestService.getUsuariAmbEntitatICodi(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new EntitatNotFoundException("Entitat not found"));

        mockMvc.perform(get("/usuaris/{usuariCodi}", "testCodi")
                        .param("entitatCodi", "testEntitatCodi")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", containsString("Entitat not found")));
    }

    @Test
    public void getUsuari_ServiceExecutionException() throws Exception {
        Mockito.when(gestioRestService.getUsuariAmbEntitatICodi(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new ServiceExecutionException("Service execution error"));

        mockMvc.perform(get("/usuaris/{usuariCodi}", "testCodi")
                        .param("entitatCodi", "testEntitatCodi")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    // GRANT permisos
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void grantPermissions_Success() throws Exception {
        PermisosServei permisos = new PermisosServei();
        permisos.setUsuariCodi("testCodi");
        permisos.setEntitatCodi("testEntitatCodi");
        ArrayList<ProcedimentServei> procedimentServeis = new ArrayList<>();
        procedimentServeis.add(ProcedimentServei.builder().procedimentCodi("procedimentCodi").serveiCodi("serveiCodi").build());
        permisos.setProcedimentServei(procedimentServeis);

        Mockito.doNothing().when(gestioRestService).serveiGrantPermis(Mockito.any(PermisosServei.class));

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/usuaris/testCodi/permisos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permisos)))
                .andExpect(status().isOk());
    }

    @Test
    public void grantPermissions_InvalidInput() throws Exception {
        mockMvc.perform(post("/usuaris/testCodi/permisos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuariCodi\": \"\", \"entitatCodi\": \"testEntitatCodi\", \"procedimentServei\": []}"))
                .andExpect(status().is(400));
    }

    @Test
    public void grantPermissions_EntitatNotFoundException() throws Exception {
        PermisosServei permisos = new PermisosServei();
        permisos.setUsuariCodi("testCodi");
        permisos.setEntitatCodi("testEntitatCodi");
        ArrayList<ProcedimentServei> procedimentServeis = new ArrayList<>();
        procedimentServeis.add(ProcedimentServei.builder().procedimentCodi("procedimentCodi").serveiCodi("serveiCodi").build());
        permisos.setProcedimentServei(procedimentServeis);

        Mockito.doThrow(new EntitatNotFoundException("Entitat not found")).when(gestioRestService).serveiGrantPermis(Mockito.any(PermisosServei.class));

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/usuaris/testCodi/permisos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permisos)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", containsString("Entitat not found")));
    }

    @Test
    public void grantPermissions_EntitatUsuariNotFoundException() throws Exception {
        PermisosServei permisos = new PermisosServei();
        permisos.setUsuariCodi("testCodi");
        permisos.setEntitatCodi("testEntitatCodi");
        ArrayList<ProcedimentServei> procedimentServeis = new ArrayList<>();
        procedimentServeis.add(ProcedimentServei.builder().procedimentCodi("procedimentCodi").serveiCodi("serveiCodi").build());
        permisos.setProcedimentServei(procedimentServeis);

        Mockito.doThrow(new EntitatUsuariNotFoundException()).when(gestioRestService).serveiGrantPermis(Mockito.any(PermisosServei.class));

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/usuaris/testCodi/permisos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permisos)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Entitat-usuari no trobat: testEntitatCodi - testCodi"));
    }

    @Test
    public void grantPermissions_ProcedimentServeiNotFoundException() throws Exception {
        PermisosServei permisos = new PermisosServei();
        permisos.setUsuariCodi("testCodi");
        permisos.setEntitatCodi("testEntitatCodi");
        ArrayList<ProcedimentServei> procedimentServeis = new ArrayList<>();
        procedimentServeis.add(ProcedimentServei.builder().procedimentCodi("procedimentCodi").serveiCodi("serveiCodi").build());
        permisos.setProcedimentServei(procedimentServeis);

        Mockito.doThrow(new ProcedimentServeiNotFoundException("ProcedimentCodi", "ServeiCodi")).when(gestioRestService).serveiGrantPermis(Mockito.any(PermisosServei.class));

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/usuaris/testCodi/permisos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permisos)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", containsString("Procediment-servei no trobat")));
    }

    @Test
    public void grantPermissions_ServiceExecutionException() throws Exception {
        PermisosServei permisos = new PermisosServei();
        permisos.setUsuariCodi("testCodi");
        permisos.setEntitatCodi("testEntitatCodi");
        permisos.setProcedimentServei(new ArrayList<ProcedimentServei>());ArrayList<ProcedimentServei> procedimentServeis = new ArrayList<>();
        procedimentServeis.add(ProcedimentServei.builder().procedimentCodi("procedimentCodi").serveiCodi("serveiCodi").build());
        permisos.setProcedimentServei(procedimentServeis);

        Mockito.doThrow(new ServiceExecutionException("Service execution error")).when(gestioRestService).serveiGrantPermis(Mockito.any(PermisosServei.class));

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/usuaris/testCodi/permisos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permisos)))
                .andExpect(status().is(500));
    }


    // GET Permisos
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void getUserPermissions_Success() throws Exception {
        PermisosServei permisos = new PermisosServei();
        permisos.setUsuariCodi("testCodi");
        permisos.setEntitatCodi("testEntitatCodi");
        ArrayList<ProcedimentServei> procedimentServeis = new ArrayList<>();
        procedimentServeis.add(ProcedimentServei.builder().procedimentCodi("procedimentCodi").serveiCodi("serveiCodi").build());
        permisos.setProcedimentServei(procedimentServeis);
        permisos.setProcedimentServei(procedimentServeis);

        Mockito.when(gestioRestService.permisosPerUsuariEntitat(Mockito.anyString(), Mockito.anyString())).thenReturn(permisos);

        mockMvc.perform(get("/usuaris/testCodi/permisos")
                        .param("entitatCodi", "testEntitatCodi")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuariCodi").value("testCodi"))
                .andExpect(jsonPath("$.entitatCodi").value("testEntitatCodi"));
    }

    @Test
    public void getUserPermissions_NoContent() throws Exception {
        Mockito.when(gestioRestService.permisosPerUsuariEntitat(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(new PermisosServei());

        mockMvc.perform(get("/usuaris/testCodi/permisos")
                        .param("entitatCodi", "testEntitatCodi")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getUserPermissions_UserNotFoundException() throws Exception {
        Mockito.when(gestioRestService.permisosPerUsuariEntitat(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new UsuariNotFoundException("Usuari not found"));

        mockMvc.perform(get("/usuaris/testCodi/permisos")
                        .param("entitatCodi", "testEntitatCodi")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUserPermissions_EntitatNotFoundException() throws Exception {
        Mockito.when(gestioRestService.permisosPerUsuariEntitat(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new EntitatNotFoundException("Entitat not found"));

        mockMvc.perform(get("/usuaris/testCodi/permisos")
                        .param("entitatCodi", "testEntitatCodi")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", containsString("Entitat not found")));
    }

    @Test
    public void getUserPermissions_ServiceExecutionException() throws Exception {
        Mockito.when(gestioRestService.permisosPerUsuariEntitat(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new ServiceExecutionException("Service execution error"));

        mockMvc.perform(get("/usuaris/testCodi/permisos")
                        .param("entitatCodi", "testEntitatCodi")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }
}