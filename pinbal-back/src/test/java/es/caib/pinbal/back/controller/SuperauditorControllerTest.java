package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.AuditoriaGenerarCommand;
import es.caib.pinbal.back.command.ConsultaFiltreCommand;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.HistoricConsultaService;
import es.caib.pinbal.logic.intf.service.ProcedimentService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SuperauditorControllerTest {

    private SuperauditorController controller;
    private EntitatService entitatService;
    private ProcedimentService procedimentService;
    private ServeiService serveiService;
    private ConsultaService consultaService;
    private HistoricConsultaService historicConsultaService;
    private HttpServletRequest request;
    private HttpSession session;

    @BeforeEach
    public void configurar() {
        controller = new SuperauditorController();
        entitatService = mock(EntitatService.class);
        procedimentService = mock(ProcedimentService.class);
        serveiService = mock(ServeiService.class);
        consultaService = mock(ConsultaService.class);
        historicConsultaService = mock(HistoricConsultaService.class);
        ControllerTestSupport.setField(controller, "entitatService", entitatService);
        ControllerTestSupport.setField(controller, "procedimentService", procedimentService);
        ControllerTestSupport.setField(controller, "serveiService", serveiService);
        ControllerTestSupport.setField(controller, "consultaService", consultaService);
        ControllerTestSupport.setField(controller, "historicConsultaService", historicConsultaService);
        controller.setMessageSource(ControllerTestSupport.mockMessageSourceEcoDeLaClau());
        request = ControllerTestSupport.mockRequest();
        session = request.getSession();
    }

    private EntitatDto entitatSeleccionada() {
        EntitatDto entitat = new EntitatDto();
        entitat.setId(1L);
        when(session.getAttribute("SuperauditorController.session.entitat")).thenReturn(entitat);
        // El rol "actual" es determina a través de EntitatHelper amb les llistes d'entitats del
        // delegat/representant/auditor de sessió (independents de l'entitat seleccionada aquí).
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitat));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        when(entitatService.findById(1L)).thenReturn(entitat);
        return entitat;
    }

    // ------------------------- get -------------------------

    @Test
    public void getSenseEntitatSeleccionadaMostraLlistatDEntitats() throws Exception {
        when(entitatService.findAll()).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("superauditorConsultes", controller.get(request, model));
        assertTrue(model.containsAttribute("entitats"));
    }

    @Test
    public void getAmbEntitatSeleccionadaOmpleFiltreTaula() throws Exception {
        entitatSeleccionada();

        Model model = new ExtendedModelMap();
        assertEquals("superauditorConsultes", controller.get(request, model));
        assertTrue(model.containsAttribute("filtreCommand"));
    }

    // ------------------------- post -------------------------

    @Test
    public void postAmbErrorsTornaAlLlistat() throws Exception {
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("superauditorConsultes", controller.post(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void postSenseErrorsGuardaFiltreIRedirigeix() throws Exception {
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:.", controller.post(request, command, bindingResult, new ExtendedModelMap()));
        verify(session).setAttribute("SuperauditorController.session.filtre", command);
    }

    // ------------------------- datatable / excel -------------------------

    @Test
    public void datatableSenseEntitatLlancaExcepcio() {
        assertThrows(EntitatNotFoundException.class, () -> controller.datatable(request, new ExtendedModelMap()));
    }

    @Test
    public void datatableAmbEntitatRetornaResposta() throws Exception {
        entitatSeleccionada();
        ControllerTestSupport.mockDatatableParams(request);
        when(consultaService.findByFiltrePaginatPerAuditor(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new ConsultaDto())));

        var resposta = controller.datatable(request, new ExtendedModelMap());

        assertEquals(1, resposta.getRecordsFiltered());
    }

    @Test
    public void excelRetornaVista() throws Exception {
        entitatSeleccionada();
        when(consultaService.findByFiltrePaginatPerAuditor(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new ConsultaDto())));

        Model model = new ExtendedModelMap();
        assertEquals("consultaSuperauditorExcelView", controller.excel(request, model));
        assertTrue(model.containsAttribute("consultaList"));
    }

    // ------------------------- entitat seleccionar/deseleccionar -------------------------

    @Test
    public void entitatSeleccionarSenseIdMostraError() {
        assertEquals("redirect:../../superauditor", controller.entitatSeleccionar(request, null));
    }

    @Test
    public void entitatSeleccionarAmbIdValidGuardaLaEntitat() {
        EntitatDto entitat = new EntitatDto();
        entitat.setId(3L);
        when(entitatService.findById(3L)).thenReturn(entitat);

        assertEquals("redirect:../../superauditor", controller.entitatSeleccionar(request, 3L));
        verify(session).setAttribute("SuperauditorController.session.entitat", entitat);
    }

    @Test
    public void entitatDeseleccionarEsborraLaSessio() {
        assertEquals("redirect:../../superauditor", controller.entitatDeseleccionar(request));
        verify(session).removeAttribute("SuperauditorController.session.entitat");
    }

    // ------------------------- serveisPerProcediment -------------------------

    @Test
    public void serveisPerProcedimentSenseEntitatSeleccionadaNoOmpleModel() throws Exception {
        Model model = new ExtendedModelMap();
        assertEquals("serveiSelectJson", controller.serveisPerProcediment(request, null, 1L, model));
        assertTrue(model.asMap().isEmpty());
    }

    @Test
    public void serveisPerProcedimentAmbEntitatOmpleModel() throws Exception {
        entitatSeleccionada();
        when(serveiService.findAmbEntitatIProcediment(1L, 2L)).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("serveiSelectJson", controller.serveisPerProcediment(request, null, 2L, model));
        assertTrue(model.containsAttribute("serveis"));
    }

    // ------------------------- generar -------------------------

    @Test
    public void generarGetSenseFormulariCreaCommandNou() throws Exception {
        Model model = new ExtendedModelMap();
        assertEquals("superauditorGenerar", controller.generarGet(request, model));
        assertTrue(model.asMap().values().stream().anyMatch(v -> v instanceof AuditoriaGenerarCommand));
    }

    @Test
    public void generarPostAmbErrorsMostraMissatge() throws Exception {
        AuditoriaGenerarCommand command = new AuditoriaGenerarCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("superauditorGenerar", controller.generarPost(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void generarPostSenseErrorsAmbResultatsGuardaIds() throws Exception {
        AuditoriaGenerarCommand command = AuditoriaGenerarCommand.builder()
                .dataInici(new java.util.Date(0))
                .dataFi(new java.util.Date())
                .numConsultes(5)
                .numEntitats(2)
                .build();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        when(consultaService.auditoriaGenerarSuperauditor(any(), any(), eq(2), eq(5))).thenReturn(List.of(1L, 2L));

        assertEquals("superauditorGenerar", controller.generarPost(request, command, bindingResult, new ExtendedModelMap()));
        verify(session).setAttribute("SuperauditorController.session.genids", List.of(1L, 2L));
    }

    @Test
    public void generarExcelSenseIdsASessioNoOmpleModel() throws Exception {
        Model model = new ExtendedModelMap();
        assertEquals("auditorGenerarExcelView", controller.generarExcel(request, model));
        assertTrue(model.asMap().isEmpty());
    }
}
