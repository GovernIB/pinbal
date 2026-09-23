package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.AuditoriaGenerarCommand;
import es.caib.pinbal.back.command.ConsultaFiltreCommand;
import es.caib.pinbal.logic.intf.dto.ArbreRespostaDto;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.EstatTipus;
import es.caib.pinbal.logic.intf.dto.FitxerDto;
import es.caib.pinbal.logic.intf.dto.JustificantDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.HistoricConsultaService;
import es.caib.pinbal.logic.intf.service.ProcedimentService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.exception.ConsultaNotFoundException;
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
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
        when(consultaService.findByFiltrePaginatPerSuperauditor(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new ConsultaDto())));

        var resposta = controller.datatable(request, new ExtendedModelMap());

        assertEquals(1, resposta.getRecordsFiltered());
    }

    @Test
    public void datatableUsaLEntitatSeleccionadaPelSuperauditorINoLaDeLEntitatHelper() throws Exception {
        // L'entitat que l'usuari té "activa" (EntitatHelper) és diferent de la que el
        // superauditor ha seleccionat explícitament per auditar: ha de prevaler aquesta darrera.
        EntitatDto entitatSeleccionadaAuditoria = new EntitatDto();
        entitatSeleccionadaAuditoria.setId(2L);
        when(session.getAttribute("SuperauditorController.session.entitat")).thenReturn(entitatSeleccionadaAuditoria);
        EntitatDto entitatPropiaUsuari = new EntitatDto();
        entitatPropiaUsuari.setId(99L);
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitatPropiaUsuari));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        ControllerTestSupport.mockDatatableParams(request);
        when(consultaService.findByFiltrePaginatPerSuperauditor(eq(2L), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new ConsultaDto())));

        controller.datatable(request, new ExtendedModelMap());

        verify(consultaService).findByFiltrePaginatPerSuperauditor(eq(2L), any(), any());
    }

    @Test
    public void excelRetornaVista() throws Exception {
        entitatSeleccionada();
        when(consultaService.findByFiltrePaginatPerSuperauditor(any(), any(), any()))
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

    // ------------------------- info -------------------------

    @Test
    public void infoConsultaSimpleRetornaVistaSimple() throws Exception {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setId(10L);
        consulta.setMultiple(false);
        consulta.setServeiCodi("SVC1");
        consulta.setEstat(EstatTipus.Tramitada.name());
        when(consultaService.findOneSuperauditor(10L)).thenReturn(consulta);
        when(serveiService.findAmbCodiPerAdminORepresentant("SVC1")).thenReturn(new ServeiDto());
        when(serveiService.generarArbreDadesEspecifiques("SVC1")).thenReturn(new es.caib.pinbal.logic.intf.dto.ArbreDto<>());
        when(serveiService.findServeiCamps("SVC1")).thenReturn(List.of());
        when(serveiService.findServeiCampGrups("SVC1")).thenReturn(List.of());
        when(consultaService.generarArbreResposta(10L)).thenReturn(new ArbreRespostaDto());

        Model model = new ExtendedModelMap();
        assertEquals("superauditorConsultaInfo", controller.info(request, 10L, null, model));
        assertEquals(consulta, model.asMap().get("consulta"));
    }

    @Test
    public void infoConsultaMultipleRetornaVistaMultipleAmbFilles() throws Exception {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setId(20L);
        consulta.setMultiple(true);
        consulta.setServeiCodi("SVC1");
        when(consultaService.findOneSuperauditor(20L)).thenReturn(consulta);
        when(serveiService.findAmbCodiPerAdminORepresentant("SVC1")).thenReturn(new ServeiDto());
        List<ConsultaDto> filles = List.of(new ConsultaDto());
        when(consultaService.findAmbPare(20L)).thenReturn(filles);

        Model model = new ExtendedModelMap();
        assertEquals("superauditorConsultaMultipleInfo", controller.info(request, 20L, null, model));
        assertEquals(filles, model.asMap().get("filles"));
    }

    @Test
    public void infoNoTrobatDelegaAHistoricConsultaService() throws Exception {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setId(30L);
        consulta.setMultiple(false);
        consulta.setServeiCodi("SVC1");
        consulta.setEstat(EstatTipus.Tramitada.name());
        when(consultaService.findOneSuperauditor(30L)).thenThrow(new ConsultaNotFoundException());
        when(historicConsultaService.findOneSuperauditor(30L)).thenReturn(consulta);
        when(serveiService.findAmbCodiPerAdminORepresentant("SVC1")).thenReturn(new ServeiDto());
        when(serveiService.generarArbreDadesEspecifiques("SVC1")).thenReturn(new es.caib.pinbal.logic.intf.dto.ArbreDto<>());
        when(serveiService.findServeiCamps("SVC1")).thenReturn(List.of());
        when(serveiService.findServeiCampGrups("SVC1")).thenReturn(List.of());
        when(consultaService.generarArbreResposta(30L)).thenReturn(new ArbreRespostaDto());

        Model model = new ExtendedModelMap();
        assertEquals("superauditorConsultaInfo", controller.info(request, 30L, null, model));
    }

    // ------------------------- justificant / justificantpdf / justificantzip -------------------------

    @Test
    public void justificantConsultaSimpleEscriuFitxer() throws Exception {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setId(40L);
        consulta.setMultiple(false);
        when(consultaService.findOneSuperauditor(40L)).thenReturn(consulta);
        JustificantDto justificant = JustificantDto.builder().nom("just.pdf").contingut(new byte[]{1}).error(false).build();
        when(consultaService.obtenirJustificant(40L, true)).thenReturn(justificant);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        assertEquals(null, controller.justificant(request, response, 40L, new ExtendedModelMap()));
    }

    @Test
    public void justificantConsultaMultipleDescarreguaConcatenat() throws Exception {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setId(41L);
        consulta.setMultiple(true);
        when(consultaService.findOneSuperauditor(41L)).thenReturn(consulta);
        FitxerDto fitxer = FitxerDto.builder().nom("concat.pdf").contingut(new byte[]{1}).build();
        when(consultaService.obtenirJustificantMultipleConcatenat(41L)).thenReturn(fitxer);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        assertEquals(null, controller.justificant(request, response, 41L, new ExtendedModelMap()));
    }

    @Test
    public void justificantAmbErrorRedirigeix() throws Exception {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setId(42L);
        consulta.setMultiple(false);
        when(consultaService.findOneSuperauditor(42L)).thenReturn(consulta);
        JustificantDto justificant = JustificantDto.builder().error(true).build();
        when(consultaService.obtenirJustificant(42L, true)).thenReturn(justificant);

        HttpServletResponse response = mock(HttpServletResponse.class);
        assertEquals("redirect:../../superauditor", controller.justificant(request, response, 42L, new ExtendedModelMap()));
    }

    @Test
    public void justificantpdfDescarreguaFitxerConcatenat() throws Exception {
        FitxerDto fitxer = FitxerDto.builder().nom("concat.pdf").contingut(new byte[]{1}).build();
        when(consultaService.obtenirJustificantMultipleConcatenat(50L)).thenReturn(fitxer);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        assertEquals(null, controller.justificantPdf(request, response, 50L, new ExtendedModelMap()));
    }

    @Test
    public void justificantzipDescarreguaFitxerZip() throws Exception {
        FitxerDto fitxer = FitxerDto.builder().nom("concat.zip").contingut(new byte[]{1}).build();
        when(consultaService.obtenirJustificantMultipleZip(60L)).thenReturn(fitxer);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        assertEquals(null, controller.justificantZip(request, response, 60L, new ExtendedModelMap()));
    }

    // ------------------------- xmlPeticio / xmlResposta -------------------------

    @Test
    public void xmlPeticioOmpleModelIRetornaVista() throws Exception {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setId(70L);
        when(consultaService.findOneSuperauditor(70L)).thenReturn(consulta);

        HttpServletResponse response = mock(HttpServletResponse.class);
        Model model = new ExtendedModelMap();
        assertEquals("consultaXml", controller.xmlPeticio(request, response, 70L, model));
        assertEquals(Boolean.TRUE, model.asMap().get("mostrarPeticio"));
        assertEquals(Boolean.FALSE, model.asMap().get("mostrarResposta"));
    }

    @Test
    public void xmlRespostaOmpleModelIRetornaVista() throws Exception {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setId(71L);
        when(consultaService.findOneSuperauditor(71L)).thenReturn(consulta);

        HttpServletResponse response = mock(HttpServletResponse.class);
        Model model = new ExtendedModelMap();
        assertEquals("consultaXml", controller.xmlResposta(request, response, 71L, model));
        assertEquals(Boolean.FALSE, model.asMap().get("mostrarPeticio"));
        assertEquals(Boolean.TRUE, model.asMap().get("mostrarResposta"));
    }

    // ------------------------- justificant/arxiu/detall -------------------------

    @Test
    public void justificantArxiuDetallRetornaVista() {
        when(consultaService.obtenirArxiuInfo(80L)).thenReturn(null);

        HttpServletResponse response = mock(HttpServletResponse.class);
        assertEquals("contingutArxiu", controller.justificantArxiuDetall(request, response, 80L, new ExtendedModelMap()));
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
