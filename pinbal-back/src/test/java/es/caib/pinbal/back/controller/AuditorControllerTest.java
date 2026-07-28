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
import es.caib.pinbal.logic.intf.service.UsuariService;
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

public class AuditorControllerTest {

    private AuditorController controller;
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
        controller = new AuditorController();
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

    private EntitatDto entitatAmbAuditor() {
        EntitatDto entitat = new EntitatDto();
        entitat.setId(1L);
        when(session.getAttribute("EntitatHelper.entitat.actual.auditor")).thenReturn(Boolean.TRUE);
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitat));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        // EntitatHelper.getEntitatActual(request, entitatService) delega en el servei quan aquest
        // no és null; cal el mock del servei perquè no torni null i les branques "entitat != null"
        // s'executin.
        when(entitatService.findById(1L)).thenReturn(entitat);
        return entitat;
    }

    // ------------------------- get / post -------------------------

    @Test
    public void getSenseRolAuditorRetornaNoAutoritzat() throws Exception {
        assertEquals("auditorNoAutoritzat", controller.get(request, new ExtendedModelMap()));
    }

    @Test
    public void getAmbRolAuditorMostraLlistat() throws Exception {
        entitatAmbAuditor();
        when(usuariService.getDades()).thenReturn(new es.caib.pinbal.logic.intf.dto.UsuariDto());

        assertEquals("auditorConsultes", controller.get(request, new ExtendedModelMap()));
    }

    @Test
    public void postAmbErrorsTornaAlLlistat() throws Exception {
        entitatAmbAuditor();
        when(usuariService.getDades()).thenReturn(new es.caib.pinbal.logic.intf.dto.UsuariDto());
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("auditorConsultes", controller.post(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void postSenseErrorsGuardaFiltreIRedirigeix() throws Exception {
        entitatAmbAuditor();
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:.", controller.post(request, command, bindingResult, new ExtendedModelMap()));
        verify(session).setAttribute("AuditorController.session.filtre", command);
    }

    // ------------------------- datatable -------------------------

    @Test
    public void datatableSenseEntitatLlancaExcepcio() {
        assertThrows(EntitatNotFoundException.class, () -> controller.datatable(request, new ExtendedModelMap()));
    }

    @Test
    public void datatableAmbEntitatRetornaResposta() throws Exception {
        entitatAmbAuditor();
        ControllerTestSupport.mockDatatableParams(request);
        Page<ConsultaDto> page = new PageImpl<>(List.of(new ConsultaDto()));
        when(consultaService.findByFiltrePaginatPerAuditor(any(), any(), any())).thenReturn(page);

        var resposta = controller.datatable(request, new ExtendedModelMap());

        assertEquals(1, resposta.getRecordsFiltered());
    }

    // ------------------------- excel / excelConsultes / csvConsultes -------------------------

    @Test
    public void excelRetornaVistaAmbConsultes() throws Exception {
        EntitatDto entitat = entitatAmbAuditor();
        when(consultaService.findByFiltrePaginatPerAuditor(any(), any(), any())).thenReturn(new PageImpl<>(List.of(new ConsultaDto())));

        Model model = new ExtendedModelMap();
        assertEquals("consultaAuditorExcelView", controller.excel(request, model));
        assertTrue(model.containsAttribute("consultaList"));
    }

    @Test
    public void excelConsultesSenseEntitatLlancaExcepcio() {
        assertThrows(EntitatNotFoundException.class, () -> controller.excelConsultes(request, new ExtendedModelMap()));
    }

    @Test
    public void excelConsultesAmbEntitatRetornaVista() throws Exception {
        entitatAmbAuditor();
        when(consultaService.findByFiltrePerAuditor(any(), any())).thenReturn(List.of(new ConsultaDto()));

        assertEquals("auditorGenerarExcelView", controller.excelConsultes(request, new ExtendedModelMap()));
    }

    @Test
    public void csvConsultesAmbEntitatRetornaVista() throws Exception {
        entitatAmbAuditor();
        when(consultaService.findByFiltrePerAuditor(any(), any())).thenReturn(List.of(new ConsultaDto()));

        assertEquals("auditorGenerarCsvView", controller.csvConsultes(request, new ExtendedModelMap()));
    }

    // ------------------------- serveisPerProcediment -------------------------

    @Test
    public void serveisPerProcedimentSenseRolNoOmpleModel() throws Exception {
        Model model = new ExtendedModelMap();
        assertEquals("serveiSelectJson", controller.serveisPerProcediment(request, null, 1L, model));
        assertTrue(model.asMap().isEmpty());
    }

    @Test
    public void serveisPerProcedimentAmbRolOmpleModel() throws Exception {
        entitatAmbAuditor();
        when(serveiService.findAmbEntitatIProcediment(1L, 2L)).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("serveiSelectJson", controller.serveisPerProcediment(request, null, 2L, model));
        assertTrue(model.containsAttribute("serveis"));
    }

    // ------------------------- generar -------------------------

    @Test
    public void generarGetSenseRolRetornaNoAutoritzat() throws Exception {
        assertEquals("auditorNoAutoritzat", controller.generarGet(request, new ExtendedModelMap()));
    }

    @Test
    public void generarGetSenseFormulariASessioCreaCommandNou() throws Exception {
        entitatAmbAuditor();

        Model model = new ExtendedModelMap();
        assertEquals("auditorGenerar", controller.generarGet(request, model));
        AuditoriaGenerarCommand command = (AuditoriaGenerarCommand) model.asMap().values().stream()
                .filter(v -> v instanceof AuditoriaGenerarCommand).findFirst().orElseThrow();
        assertEquals(1, command.getNumEntitats());
    }

    @Test
    public void generarPostAmbErrorsMostraMissatge() throws Exception {
        entitatAmbAuditor();
        AuditoriaGenerarCommand command = new AuditoriaGenerarCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("auditorGenerar", controller.generarPost(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void generarPostSenseErrorsAmbResultatsGuardaIds() throws Exception {
        entitatAmbAuditor();
        AuditoriaGenerarCommand command = AuditoriaGenerarCommand.builder()
                .dataInici(new java.util.Date(0))
                .dataFi(new java.util.Date())
                .numConsultes(5)
                .numEntitats(1)
                .build();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        when(consultaService.auditoriaGenerarAuditor(eq(1L), any(), any(), eq(5))).thenReturn(List.of(1L, 2L));

        assertEquals("auditorGenerar", controller.generarPost(request, command, bindingResult, new ExtendedModelMap()));
        verify(session).setAttribute("AuditorController.session.genids", List.of(1L, 2L));
    }

    @Test
    public void generarPostSenseResultatsMostraError() throws Exception {
        entitatAmbAuditor();
        AuditoriaGenerarCommand command = AuditoriaGenerarCommand.builder()
                .dataInici(new java.util.Date(0))
                .dataFi(new java.util.Date())
                .numConsultes(5)
                .numEntitats(1)
                .build();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        when(consultaService.auditoriaGenerarAuditor(eq(1L), any(), any(), eq(5))).thenReturn(List.of());

        assertEquals("auditorGenerar", controller.generarPost(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void generarExcelSenseIdsASessioNoOmpleModel() throws Exception {
        entitatAmbAuditor();
        Model model = new ExtendedModelMap();
        assertEquals("auditorGenerarExcelView", controller.generarExcel(request, model));
        assertTrue(model.asMap().isEmpty());
    }
}
