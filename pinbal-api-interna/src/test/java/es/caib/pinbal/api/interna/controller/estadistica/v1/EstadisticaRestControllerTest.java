package es.caib.pinbal.api.interna.controller.estadistica.v1;

import es.caib.comanda.model.server.monitoring.EstadistiquesInfo;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import es.caib.pinbal.logic.intf.service.EstadisticaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EstadisticaRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EstadisticaService estadisticaService;

    @InjectMocks
    private EstadisticaRestController estadisticaRestController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(estadisticaRestController).build();
    }

    @Test
    public void testEstadistiquesInfo_Success() throws Exception {
        when(estadisticaService.getEstadistiquesInfo()).thenReturn(new EstadistiquesInfo());

        mockMvc.perform(get("/estadistiques/v1/info"))
                .andExpect(status().isOk());
    }

    @Test
    public void testEstadistiques_Success() throws Exception {
        when(estadisticaService.consultaUltimesEstadistiques()).thenReturn(new RegistresEstadistics());

        mockMvc.perform(get("/estadistiques/v1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testEstadistiquesOfData_Success() throws Exception {
        when(estadisticaService.consultaEstadistiques(any())).thenReturn(new RegistresEstadistics());

        mockMvc.perform(get("/estadistiques/v1/of/21-07-2023"))
                .andExpect(status().isOk());
    }

    @Test
    public void testEstadistiquesOfData_InvalidFormat() {
        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/estadistiques/v1/of/not-a-date")));
    }

    @Test
    public void testEstadistiquesFromTo_Success() throws Exception {
        List<RegistresEstadistics> resultat = new ArrayList<>();
        resultat.add(new RegistresEstadistics());
        when(estadisticaService.consultaEstadistiques(any(), any())).thenReturn(resultat);

        mockMvc.perform(get("/estadistiques/v1/from/01-07-2023/to/21-07-2023"))
                .andExpect(status().isOk());
    }

    @Test
    public void testEstadistiquesFromTo_InvalidFormat() {
        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/estadistiques/v1/from/not-a-date/to/21-07-2023")));
    }

    @Test
    public void testGenerarEstadistiques_Success() throws Exception {
        when(estadisticaService.generarEstadistiques(any(), any())).thenReturn("OK");

        mockMvc.perform(get("/estadistiques/v1/generar/from/01-07-2023/to/21-07-2023"))
                .andExpect(status().isOk());
    }
}
