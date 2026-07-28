package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.ConsultaFiltreCommand;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.FitxerDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.HistoricConsultaService;
import es.caib.pinbal.logic.intf.service.ProcedimentService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.UsuariService;
import es.caib.pinbal.logic.intf.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConsultaMultipleControllerTest {

    private ConsultaMultipleController controller;
    private EntitatService entitatService;
    private ProcedimentService procedimentService;
    private ServeiService serveiService;
    private ConsultaService consultaService;
    private HistoricConsultaService historicConsultaService;
    private UsuariService usuariService;
    private HttpServletRequest request;
    private HttpSession session;

    @BeforeEach
    public void configurar() {
        controller = new ConsultaMultipleController();
        entitatService = mock(EntitatService.class);
        procedimentService = mock(ProcedimentService.class);
        serveiService = mock(ServeiService.class);
        consultaService = mock(ConsultaService.class);
        historicConsultaService = mock(HistoricConsultaService.class);
        usuariService = mock(UsuariService.class);
        ControllerTestSupport.setField(controller, "entitatService", entitatService);
        ControllerTestSupport.setField(controller, "procedimentService", procedimentService);
        ControllerTestSupport.setField(controller, "serveiService", serveiService);
        ControllerTestSupport.setField(controller, "consultaService", consultaService);
        ControllerTestSupport.setField(controller, "historicConsultaService", historicConsultaService);
        ControllerTestSupport.setField(controller, "usuariService", usuariService);
        controller.setMessageSource(ControllerTestSupport.mockMessageSourceEcoDeLaClau());
        request = ControllerTestSupport.mockRequest();
        session = request.getSession();
    }

    private EntitatDto entitatDelegat() {
        EntitatDto entitat = new EntitatDto();
        entitat.setId(1L);
        when(session.getAttribute("EntitatHelper.entitat.actual.delegat")).thenReturn(Boolean.TRUE);
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitat));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        when(entitatService.findById(1L)).thenReturn(entitat);
        return entitat;
    }

    // ------------------------- get / post -------------------------

    @Test
    public void getSenseDelegatRetornaNoAutoritzat() throws Exception {
        assertEquals("delegatNoAutoritzat", controller.get(request, new ExtendedModelMap()));
    }

    @Test
    public void getAmbDelegatMostraLlistat() throws Exception {
        entitatDelegat();
        when(usuariService.getDades()).thenReturn(new UsuariDto());

        assertEquals("consultaMultiple", controller.get(request, new ExtendedModelMap()));
    }

    @Test
    public void postAmbAccioNetejarEsborraElFiltreIRedirigeix() throws Exception {
        entitatDelegat();
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:multiple", controller.post(request, command, bindingResult, "netejar", new ExtendedModelMap()));
        verify(session).removeAttribute("ConsultaMultipleController.session.filtre");
    }

    @Test
    public void postSenseErrorsGuardaElFiltreIRedirigeix() throws Exception {
        entitatDelegat();
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:multiple", controller.post(request, command, bindingResult, null, new ExtendedModelMap()));
        verify(session).setAttribute("ConsultaMultipleController.session.filtre", command);
    }

    // ------------------------- datatable -------------------------

    @Test
    public void datatableSenseEntitatLlancaExcepcio() {
        assertThrows(EntitatNotFoundException.class, () -> controller.datatable(request, new ExtendedModelMap()));
    }

    @Test
    public void datatableAmbEntitatRetornaResposta() throws Exception {
        entitatDelegat();
        ControllerTestSupport.mockDatatableParams(request, "scspPeticionId", "creacioData", "procedimentCodiNom", "serveiCodiNom");
        when(consultaService.findMultiplesByFiltrePaginatPerDelegat(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new ConsultaDto())));

        var resposta = controller.datatable(request, new ExtendedModelMap());

        assertEquals(1, resposta.getRecordsFiltered());
    }

    // ------------------------- excel -------------------------

    @Test
    public void excelRetornaVista() throws Exception {
        entitatDelegat();
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);
        when(consultaService.findMultiplesByFiltrePaginatPerDelegat(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new ConsultaDto())));

        Model model = new ExtendedModelMap();
        assertEquals("consultaMultipleExcelView", controller.excel(request, model));
        assertTrue(model.containsAttribute("consultaList"));
    }

    // ------------------------- info -------------------------

    @Test
    public void infoSenseDelegatRetornaNoAutoritzat() throws Exception {
        assertEquals("delegatNoAutoritzat", controller.info(request, 1L, new ExtendedModelMap()));
    }

    @Test
    public void infoAmbConsultaExistentMostraInfo() throws Exception {
        entitatDelegat();
        ConsultaDto consulta = new ConsultaDto();
        consulta.setServeiCodi("SERV1");
        when(consultaService.findOneDelegat(1L)).thenReturn(consulta);
        when(consultaService.findAmbPare(1L)).thenReturn(List.of());
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(new es.caib.pinbal.logic.intf.dto.ServeiDto());

        assertEquals("consultaMultipleInfo", controller.info(request, 1L, new ExtendedModelMap()));
    }

    // ------------------------- recuperarResposta -------------------------

    @Test
    public void recuperarRespostaHistoricMostraError() throws Exception {
        entitatDelegat();
        when(session.getAttribute("consulta_multiple")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../1", controller.recuperarResposta(request, 1L));
    }

    @Test
    public void recuperarRespostaNoHistoricRecuperaResposta() throws Exception {
        entitatDelegat();

        assertEquals("redirect:../1", controller.recuperarResposta(request, 1L));
        verify(consultaService).recuperarRespostaConsultaMultiple(1L);
    }

    @Test
    public void recuperarRespostaAmbConsultaInexistentLlancaExcepcio() throws Exception {
        entitatDelegat();
        when(consultaService.findOneDelegat(1L)).thenThrow(new ConsultaNotFoundException());

        assertThrows(ConsultaNotFoundException.class, () -> controller.recuperarResposta(request, 1L));
    }

    // ------------------------- justificantpdf / justificantzip -------------------------

    @Test
    public void justificantPdfSenseDelegatRetornaNoAutoritzat() throws Exception {
        assertEquals("delegatNoAutoritzat", controller.justificantPdf(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void justificantPdfEscriuElFitxerAlResponse() throws Exception {
        entitatDelegat();
        FitxerDto fitxer = FitxerDto.builder().nom("just.pdf").contingut(new byte[]{1, 2, 3}).build();
        when(consultaService.obtenirJustificantMultipleConcatenat(1L)).thenReturn(fitxer);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream());

        assertEquals(null, controller.justificantPdf(request, response, 1L, new ExtendedModelMap()));
        verify(response).setHeader("Content-Disposition", "attachment; filename=\"just.pdf\"");
    }

    @Test
    public void justificantZipAmbErrorMostraAlertaIRedirigeix() throws Exception {
        entitatDelegat();
        when(consultaService.obtenirJustificantMultipleZip(1L)).thenThrow(new RuntimeException("error"));
        when(historicConsultaService.obtenirJustificantMultipleZip(1L)).thenThrow(new RuntimeException("error"));

        assertEquals("redirect:../../../consulta/multiple", controller.justificantZip(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    /**
     * OutputStream mínim per capturar {@code response.getOutputStream().write(...)} sense haver
     * de mockejar totes les crides de {@link javax.servlet.ServletOutputStream}.
     */
    private static class DelegatingServletOutputStream extends javax.servlet.ServletOutputStream {
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        @Override
        public void write(int b) {
            buffer.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(javax.servlet.WriteListener writeListener) {
        }
    }
}
