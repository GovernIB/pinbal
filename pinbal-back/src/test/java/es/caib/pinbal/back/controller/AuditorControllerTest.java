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
import java.io.ByteArrayOutputStream;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    // ------------------------- info -------------------------

    @Test
    public void infoConsultaSimpleRetornaVistaSimple() throws Exception {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setId(10L);
        consulta.setMultiple(false);
        consulta.setServeiCodi("SVC1");
        consulta.setEstat(EstatTipus.Tramitada.name());
        when(consultaService.findOneAuditor(10L)).thenReturn(consulta);
        when(serveiService.findAmbCodiPerAdminORepresentant("SVC1")).thenReturn(new ServeiDto());
        when(serveiService.generarArbreDadesEspecifiques("SVC1")).thenReturn(new es.caib.pinbal.logic.intf.dto.ArbreDto<>());
        when(serveiService.findServeiCamps("SVC1")).thenReturn(List.of());
        when(serveiService.findServeiCampGrups("SVC1")).thenReturn(List.of());
        when(consultaService.generarArbreResposta(10L)).thenReturn(new ArbreRespostaDto());

        Model model = new ExtendedModelMap();
        assertEquals("auditorConsultaInfo", controller.info(request, 10L, null, model));
        assertEquals(consulta, model.asMap().get("consulta"));
    }

    @Test
    public void infoConsultaMultipleRetornaVistaMultipleAmbFilles() throws Exception {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setId(20L);
        consulta.setMultiple(true);
        consulta.setServeiCodi("SVC1");
        when(consultaService.findOneAuditor(20L)).thenReturn(consulta);
        when(serveiService.findAmbCodiPerAdminORepresentant("SVC1")).thenReturn(new ServeiDto());
        List<ConsultaDto> filles = List.of(new ConsultaDto());
        when(consultaService.findAmbPare(20L)).thenReturn(filles);

        Model model = new ExtendedModelMap();
        assertEquals("auditorConsultaMultipleInfo", controller.info(request, 20L, null, model));
        assertEquals(filles, model.asMap().get("filles"));
    }

    @Test
    public void infoNoTrobatDelegaAHistoricConsultaService() throws Exception {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setId(30L);
        consulta.setMultiple(false);
        consulta.setServeiCodi("SVC1");
        consulta.setEstat(EstatTipus.Tramitada.name());
        when(consultaService.findOneAuditor(30L)).thenThrow(new ConsultaNotFoundException());
        when(historicConsultaService.findOneAuditor(30L)).thenReturn(consulta);
        when(serveiService.findAmbCodiPerAdminORepresentant("SVC1")).thenReturn(new ServeiDto());
        when(serveiService.generarArbreDadesEspecifiques("SVC1")).thenReturn(new es.caib.pinbal.logic.intf.dto.ArbreDto<>());
        when(serveiService.findServeiCamps("SVC1")).thenReturn(List.of());
        when(serveiService.findServeiCampGrups("SVC1")).thenReturn(List.of());
        when(consultaService.generarArbreResposta(30L)).thenReturn(new ArbreRespostaDto());

        Model model = new ExtendedModelMap();
        assertEquals("auditorConsultaInfo", controller.info(request, 30L, null, model));
    }

    // ------------------------- justificant / justificantpdf / justificantzip -------------------------

    @Test
    public void justificantConsultaSimpleEscriuFitxer() throws Exception {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setId(40L);
        consulta.setMultiple(false);
        when(consultaService.findOneAuditor(40L)).thenReturn(consulta);
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
        when(consultaService.findOneAuditor(41L)).thenReturn(consulta);
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
        when(consultaService.findOneAuditor(42L)).thenReturn(consulta);
        JustificantDto justificant = JustificantDto.builder().error(true).build();
        when(consultaService.obtenirJustificant(42L, true)).thenReturn(justificant);

        HttpServletResponse response = mock(HttpServletResponse.class);
        assertEquals("redirect:../../auditor", controller.justificant(request, response, 42L, new ExtendedModelMap()));
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
        when(consultaService.findOneAuditor(70L)).thenReturn(consulta);

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
        when(consultaService.findOneAuditor(71L)).thenReturn(consulta);

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
