package es.caib.pinbal.api.interna.controller;

import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.logic.intf.service.RecobrimentService;
import es.caib.pinbal.logic.intf.service.exception.RecobrimentScspException;
import es.caib.pinbal.logic.intf.service.exception.RecobrimentScspValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RecobrimentRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecobrimentService recobrimentService;

    @InjectMocks
    private RecobrimentRestController recobrimentRestController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(recobrimentRestController).build();
    }

    @Test
    public void testTest_Success() throws Exception {
        mockMvc.perform(get("/recobriment/test"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPeticionSincrona_Success() throws Exception {
        when(recobrimentService.peticionSincrona(any(ScspPeticion.class))).thenReturn(new ScspRespuesta());

        mockMvc.perform(post("/recobriment/peticionSincrona")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPeticionSincrona_ScspException() throws Exception {
        when(recobrimentService.peticionSincrona(any(ScspPeticion.class))).thenThrow(new RecobrimentScspException("error scsp"));

        mockMvc.perform(post("/recobriment/peticionSincrona")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testPeticionSincrona_ValidationException() throws Exception {
        when(recobrimentService.peticionSincrona(any(ScspPeticion.class))).thenThrow(new RecobrimentScspValidationException("invalid"));

        mockMvc.perform(post("/recobriment/peticionSincrona")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPeticionAsincrona_Success() throws Exception {
        when(recobrimentService.peticionAsincrona(any(ScspPeticion.class))).thenReturn(new ScspConfirmacionPeticion());

        mockMvc.perform(post("/recobriment/peticionAsincrona")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetRespuesta_Success() throws Exception {
        when(recobrimentService.getRespuesta(anyString())).thenReturn(new ScspRespuesta());

        mockMvc.perform(get("/recobriment/getRespuesta").param("idPeticion", "123"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetJustificante_Success() throws Exception {
        ScspJustificante justificante = new ScspJustificante();
        justificante.setNom("justificant.pdf");
        justificante.setContentType("application/pdf");
        justificante.setContingut("contingut".getBytes());
        when(recobrimentService.getJustificante(anyString(), anyString())).thenReturn(justificante);

        mockMvc.perform(get("/recobriment/getJustificante")
                        .param("idPeticion", "123")
                        .param("idSolicitud", "456"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetJustificante_NullContentType() throws Exception {
        ScspJustificante justificante = new ScspJustificante();
        justificante.setNom("justificant.txt");
        justificante.setContentType(null);
        justificante.setContingut("contingut".getBytes());
        when(recobrimentService.getJustificante(anyString(), anyString())).thenReturn(justificante);

        // Sense contentType el controlador recorre a MimetypesFileTypeMap, que en aquest
        // classpath de test no disposa de com.sun.activation i acaba delegant a l'@ExceptionHandler.
        mockMvc.perform(get("/recobriment/getJustificante")
                        .param("idPeticion", "123")
                        .param("idSolicitud", "456"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void testGetJustificanteImprimible_Success() throws Exception {
        ScspJustificante justificante = new ScspJustificante();
        justificante.setNom("justificant.pdf");
        justificante.setContentType("application/pdf");
        justificante.setContingut("contingut".getBytes());
        when(recobrimentService.getJustificanteImprimible(anyString(), anyString())).thenReturn(justificante);

        mockMvc.perform(get("/recobriment/getJustificanteImprimible")
                        .param("idPeticion", "123")
                        .param("idSolicitud", "456"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetJustificanteCsv_Success() throws Exception {
        when(recobrimentService.getJustificanteCsv(anyString(), anyString())).thenReturn("CSV123");

        mockMvc.perform(get("/recobriment/getJustificanteCsv")
                        .param("idPeticion", "123")
                        .param("idSolicitud", "456"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetJustificanteUuid_Success() throws Exception {
        when(recobrimentService.getJustificanteUuid(anyString(), anyString())).thenReturn("UUID123");

        mockMvc.perform(get("/recobriment/getJustificanteUuId")
                        .param("idPeticion", "123")
                        .param("idSolicitud", "456"))
                .andExpect(status().isOk());
    }
}
