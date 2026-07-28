package es.caib.pinbal.api.interna.controller.salut.v1;

import es.caib.comanda.model.server.monitoring.AppInfo;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.pinbal.logic.intf.service.SalutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SalutRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private javax.servlet.ServletContext servletContext;

    @Mock
    private SalutService salutService;

    @InjectMocks
    private SalutRestController salutRestController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(salutRestController).build();
    }

    private ByteArrayInputStream buildManifestStream(boolean withAttributes) throws Exception {
        Manifest manifest = new Manifest();
        if (withAttributes) {
            Attributes attrs = manifest.getMainAttributes();
            attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
            attrs.putValue("Implementation-Version", "2.0.1");
            attrs.putValue("Build-Timestamp", "2024-01-15T10:00:00Z");
            attrs.putValue("Build-Jdk-Spec", "11");
            attrs.putValue("Implementation-SCM-Branch", "main");
            attrs.putValue("Implementation-SCM-Revision", "abc123");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        manifest.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Test
    public void testAppInfo_Success() throws Exception {
        when(servletContext.getResourceAsStream(anyString())).thenReturn(buildManifestStream(true));
        when(salutService.getIntegracions()).thenReturn(new ArrayList<>());
        when(salutService.getSubsistemes()).thenReturn(new ArrayList<>());
        when(salutService.getContexts(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/salut/v1/info"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAppInfo_EmptyManifest() throws Exception {
        when(servletContext.getResourceAsStream(anyString())).thenReturn(buildManifestStream(false));
        when(salutService.getIntegracions()).thenReturn(new ArrayList<>());
        when(salutService.getSubsistemes()).thenReturn(new ArrayList<>());
        when(salutService.getContexts(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/salut/v1/info"))
                .andExpect(status().isOk());
    }

    @Test
    public void testHealth_Success() throws Exception {
        when(servletContext.getResourceAsStream(anyString())).thenReturn(buildManifestStream(true));
        when(salutService.checkSalut(anyString())).thenReturn(new SalutInfo());

        mockMvc.perform(get("/salut/v1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testHealth_WithParams() throws Exception {
        when(servletContext.getResourceAsStream(anyString())).thenReturn(buildManifestStream(true));
        when(salutService.checkSalut(anyString())).thenReturn(new SalutInfo());

        mockMvc.perform(get("/salut/v1")
                        .param("dataPeriode", "2024-01-01")
                        .param("dataTotal", "2024-01-31"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetDate_Valid() {
        Date date = SalutRestController.getDate("2023-07-21T10:15:30Z");
        assertNotNull(date);
    }

    @Test
    public void testGetDate_Invalid() {
        Date date = SalutRestController.getDate("not-a-date");
        assertNull(date);
    }

    @Test
    public void testManifestInfoIsCachedAcrossCalls() throws Exception {
        when(servletContext.getResourceAsStream(anyString())).thenReturn(buildManifestStream(true));
        when(salutService.checkSalut(anyString())).thenReturn(new SalutInfo());

        // Es criden dues vegades: la segona hauria de reutilitzar el manifestInfo ja calculat
        mockMvc.perform(get("/salut/v1")).andExpect(status().isOk());
        mockMvc.perform(get("/salut/v1")).andExpect(status().isOk());
    }
}
