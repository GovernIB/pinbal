package es.caib.pinbal.api.interna.controller.log.v1;

import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import es.caib.comanda.ms.exception.ComandaApiException;
import es.caib.comanda.ms.log.helper.LogFileStream;
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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LogRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SalutService salutService;

    @InjectMocks
    private LogRestController logRestController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(logRestController).build();
    }

    @Test
    public void testGetFitxers_Success() throws Exception {
        List<FitxerInfo> fitxers = new ArrayList<>();
        fitxers.add(new FitxerInfo().nom("app.log"));
        when(salutService.getFitxersLog()).thenReturn(fitxers);

        mockMvc.perform(get("/logs/v1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFitxers_NoContent() throws Exception {
        when(salutService.getFitxersLog()).thenReturn(null);

        mockMvc.perform(get("/logs/v1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetFitxerByNom_Success() throws Exception {
        FitxerContingut fitxer = new FitxerContingut().nom("app.log");
        when(salutService.getFitxerLogByNom(anyString())).thenReturn(fitxer);

        mockMvc.perform(get("/logs/v1/app.log"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFitxerByNom_NotFound() throws Exception {
        when(salutService.getFitxerLogByNom(anyString())).thenReturn(null);

        mockMvc.perform(get("/logs/v1/app.log"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetFitxerByNom_ComandaApiException() throws Exception {
        when(salutService.getFitxerLogByNom(anyString())).thenThrow(new ComandaApiException("no trobat"));

        mockMvc.perform(get("/logs/v1/app.log"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDescarregarFitxerDirecte_Success() throws Exception {
        LogFileStream file = new LogFileStream(
                new ByteArrayInputStream("contingut de prova".getBytes()),
                "app.log",
                19L,
                "text/plain");
        when(salutService.getFitxerLogStream(anyString())).thenReturn(file);

        mockMvc.perform(get("/logs/v1/app.log/directe"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDescarregarFitxerDirecte_NullContentType() throws Exception {
        LogFileStream file = new LogFileStream(
                new ByteArrayInputStream("contingut".getBytes()),
                "app.log",
                9L,
                null);
        when(salutService.getFitxerLogStream(anyString())).thenReturn(file);

        mockMvc.perform(get("/logs/v1/app.log/directe"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDescarregarFitxerDirecte_NotFound() throws Exception {
        when(salutService.getFitxerLogStream(anyString())).thenReturn(null);

        mockMvc.perform(get("/logs/v1/app.log/directe"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDescarregarFitxerDirecte_ComandaApiException() throws Exception {
        when(salutService.getFitxerLogStream(anyString())).thenThrow(new ComandaApiException("no trobat"));

        mockMvc.perform(get("/logs/v1/app.log/directe"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetFitxerLinies_Success() throws Exception {
        List<String> linies = new ArrayList<>();
        linies.add("linia 1");
        when(salutService.getFitxerLogLinies(anyString(), any())).thenReturn(linies);

        mockMvc.perform(get("/logs/v1/app.log/linies/10"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFitxerLinies_NotFound() throws Exception {
        when(salutService.getFitxerLogLinies(anyString(), any())).thenReturn(null);

        mockMvc.perform(get("/logs/v1/app.log/linies/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetFitxerLinies_ComandaApiException() throws Exception {
        when(salutService.getFitxerLogLinies(anyString(), any())).thenThrow(new ComandaApiException("no trobat"));

        mockMvc.perform(get("/logs/v1/app.log/linies/10"))
                .andExpect(status().isNotFound());
    }
}
