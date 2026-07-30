package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.ConsultaFiltreCommand;
import es.caib.pinbal.logic.intf.dto.ArbreDto;
import es.caib.pinbal.logic.intf.dto.ArbreRespostaDto;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.FitxerDto;
import es.caib.pinbal.logic.intf.dto.JustificantDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.HistoricConsultaService;
import es.caib.pinbal.logic.intf.service.ProcedimentService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.UsuariService;
import es.caib.pinbal.logic.intf.service.exception.AccessDenegatException;
import es.caib.pinbal.logic.intf.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.JustificantGeneracioException;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConsultaAdminControllerTest {

    private ConsultaAdminController controller;
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
        controller = new ConsultaAdminController();
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

    private void filtreAmbEntitatGovern() {
        EntitatDto govern = new EntitatDto();
        govern.setId(9L);
        when(entitatService.findTopByTipus(EntitatDto.EntitatTipusDto.GOVERN)).thenReturn(govern);
        when(usuariService.getDades()).thenReturn(new UsuariDto());
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
    public void getSenseFiltreASessioMostraLlistat() throws Exception {
        filtreAmbEntitatGovern();
        when(entitatService.findAll()).thenReturn(List.of());
        when(procedimentService.findAmbEntitat(9L)).thenReturn(List.of());
        when(serveiService.findAmbEntitat(9L)).thenReturn(List.of());

        assertEquals("adminConsultes", controller.get(request, new ExtendedModelMap()));
    }

    @Test
    public void postAmbAccioNetejarEsborraElFiltre() throws Exception {
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:consulta", controller.post(request, command, bindingResult, "netejar", new ExtendedModelMap()));
        verify(session).removeAttribute("ConsultaAdminController.session.filtre");
    }

    @Test
    public void postSenseErrorsGuardaElFiltre() throws Exception {
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:consulta", controller.post(request, command, bindingResult, null, new ExtendedModelMap()));
        verify(session).setAttribute("ConsultaAdminController.session.filtre", command);
    }

    // ------------------------- datatable / excel -------------------------

    @Test
    public void datatableRetornaResposta() throws Exception {
        filtreAmbEntitatGovern();
        ControllerTestSupport.mockDatatableParams(request);
        when(consultaService.findByFiltrePaginatPerAdmin(any(), any())).thenReturn(new PageImpl<>(List.of(new ConsultaDto())));

        var resposta = controller.datatable(request, new ExtendedModelMap());

        assertEquals(1, resposta.getRecordsFiltered());
    }

    @Test
    public void excelRetornaVista() throws Exception {
        when(consultaService.findByFiltrePaginatPerAdmin(any(), any())).thenReturn(new PageImpl<>(List.of(new ConsultaDto())));

        Model model = new ExtendedModelMap();
        assertEquals("consultaAdminExcelView", controller.excel(request, model));
        assertTrue(model.containsAttribute("consultaList"));
    }

    // ------------------------- entitat seleccionar/deseleccionar -------------------------

    @Test
    public void entitatSeleccionarSenseIdMostraError() {
        assertEquals("redirect:../../consulta", controller.entitatSeleccionar(request, null));
    }

    @Test
    public void entitatSeleccionarAmbIdValidGuardaLaEntitat() {
        EntitatDto entitat = new EntitatDto();
        entitat.setId(3L);
        when(entitatService.findById(3L)).thenReturn(entitat);

        assertEquals("redirect:../../consulta", controller.entitatSeleccionar(request, 3L));
        verify(session).setAttribute("ConsultaAdminController.session.entitat", entitat);
    }

    @Test
    public void entitatDeseleccionarEsborraLaSessio() {
        assertEquals("redirect:../../consulta", controller.entitatDeseleccionar(request));
    }

    // ------------------------- get consultaId (info) -------------------------

    @Test
    public void getConsultaAmbConsultaSimpleMostraInfo() throws Exception {
        entitatDelegat();
        ConsultaDto consulta = new ConsultaDto();
        consulta.setServeiCodi("SERV1");
        consulta.setEstat(ConsultaDto.EstatTipus.Tramitada.name());
        when(consultaService.findOneAdmin(1L)).thenReturn(consulta);
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(new es.caib.pinbal.logic.intf.dto.ServeiDto());
        when(serveiService.generarArbreDadesEspecifiques("SERV1")).thenReturn(new es.caib.pinbal.logic.intf.dto.ArbreDto<>());
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of());
        when(serveiService.findServeiCampGrups("SERV1")).thenReturn(List.of());
        when(consultaService.generarArbreResposta(1L)).thenReturn(new es.caib.pinbal.logic.intf.dto.ArbreRespostaDto());

        assertEquals("adminConsultaInfo", controller.get(request, 1L, null, new ExtendedModelMap()));
    }

    @Test
    public void getConsultaMultipleMostraInfoDeMultiple() throws Exception {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setServeiCodi("SERV1");
        consulta.setMultiple(true);
        when(consultaService.findOneAdmin(1L)).thenReturn(consulta);
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(new es.caib.pinbal.logic.intf.dto.ServeiDto());
        when(consultaService.findAmbPare(1L)).thenReturn(List.of());

        assertEquals("adminConsultaMultipleInfo", controller.get(request, 1L, null, new ExtendedModelMap()));
    }

    // ------------------------- recuperarResposta -------------------------

    @Test
    public void recuperarRespostaHistoricMostraError() throws Exception {
        when(session.getAttribute("consulta_admin")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../1", controller.recuperarResposta(request, 1L));
    }

    @Test
    public void recuperarRespostaNoHistoricRecupera() throws Exception {
        assertEquals("redirect:../1", controller.recuperarResposta(request, 1L));
        verify(consultaService).recuperarRespostaConsultaMultiple(1L);
    }

    // ------------------------- justificant -------------------------

    @Test
    public void justificantArxiuDetallOmpleModel() {
        when(consultaService.obtenirArxiuInfo(1L)).thenReturn(null);

        Model model = new ExtendedModelMap();
        assertEquals("contingutArxiu", controller.justificantArxiuDetall(request, mock(HttpServletResponse.class), 1L, model));
        assertTrue(model.containsAttribute("mostrarArxiuInfo"));
    }

    @Test
    public void justificantSenseDelegatRetornaNoAutoritzat() throws Exception {
        assertEquals("delegatNoAutoritzat", controller.justificant(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void justificantAmbRolAdministradorSenseEntitatFuncionaCorrectament() throws Exception {
        when(session.getAttribute("RolHelper.rol.actual")).thenReturn("PBL_ADMIN");
        ConsultaDto consulta = new ConsultaDto();
        when(consultaService.findOneAdmin(1L)).thenReturn(consulta);
        JustificantDto justificant = JustificantDto.builder().nom("just.pdf").contingut(new byte[]{1}).error(false).build();
        when(consultaService.obtenirJustificant(1L, true)).thenReturn(justificant);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new TestOutputStream());

        assertEquals(null, controller.justificant(request, response, 1L, new ExtendedModelMap()));
    }

    @Test
    public void justificantAmbConsultaSimpleEscriuFitxer() throws Exception {
        entitatDelegat();
        ConsultaDto consulta = new ConsultaDto();
        when(consultaService.findOneAdmin(1L)).thenReturn(consulta);
        JustificantDto justificant = JustificantDto.builder().nom("just.pdf").contingut(new byte[]{1}).error(false).build();
        when(consultaService.obtenirJustificant(1L, true)).thenReturn(justificant);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new TestOutputStream());

        assertEquals(null, controller.justificant(request, response, 1L, new ExtendedModelMap()));
    }

    // ------------------------- justificantReintentar -------------------------

    @Test
    public void justificantReintentarSenseAccesRedirigeixAIndex() throws Exception {
        assertEquals("redirect:../../index", controller.justificantReintentar(request, mock(HttpServletResponse.class), 1L, null));
    }

    @Test
    public void justificantReintentarAmbConsultaInexistentLlancaExcepcio() {
        entitatDelegat();
        org.junit.jupiter.api.Assertions.assertThrows(ConsultaNotFoundException.class, () -> {
            when(consultaService.reintentarGeneracioJustificant(1L, false, true)).thenThrow(new ConsultaNotFoundException());
            controller.justificantReintentar(request, mock(HttpServletResponse.class), 1L, null);
        });
    }

    @Test
    public void justificantReintentarAmbRolAdministradorSenseEntitatFuncionaIRedirigeixAInfo() throws Exception {
        when(session.getAttribute("RolHelper.rol.actual")).thenReturn("PBL_ADMIN");
        JustificantDto justificant = JustificantDto.builder().error(false).build();
        when(consultaService.reintentarGeneracioJustificant(1L, false, true)).thenReturn(justificant);

        assertEquals("redirect:../../consulta/1",
                controller.justificantReintentar(request, mock(HttpServletResponse.class), 1L, true));
    }

    @Test
    public void justificantReintentarHistoricAmbExit() throws Exception {
        entitatDelegat();
        when(session.getAttribute("consulta_admin")).thenReturn(Boolean.TRUE);
        JustificantDto justificant = JustificantDto.builder().error(false).build();
        when(historicConsultaService.reintentarGeneracioJustificant(1L, false)).thenReturn(justificant);

        assertEquals("redirect:../../consulta",
                controller.justificantReintentar(request, mock(HttpServletResponse.class), 1L, null));
    }

    @Test
    public void justificantReintentarAmbErrorMostraMissatgeError() throws Exception {
        entitatDelegat();
        JustificantDto justificant = JustificantDto.builder().error(true).build();
        when(consultaService.reintentarGeneracioJustificant(1L, false, true)).thenReturn(justificant);

        assertEquals("redirect:../../consulta",
                controller.justificantReintentar(request, mock(HttpServletResponse.class), 1L, null));
    }

    @Test
    public void justificantReintentarAmbExcepcioGenericaCapturada() throws Exception {
        entitatDelegat();
        when(consultaService.reintentarGeneracioJustificant(1L, false, true)).thenThrow(new RuntimeException("error"));

        assertEquals("redirect:../../consulta",
                controller.justificantReintentar(request, mock(HttpServletResponse.class), 1L, null));
    }

    // ------------------------- justificant: branques addicionals -------------------------

    @Test
    public void justificantSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.delegat")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../index", controller.justificant(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void justificantAmbConsultaMultipleEscriuFitxerConcatenat() throws Exception {
        entitatDelegat();
        ConsultaDto consulta = new ConsultaDto();
        consulta.setMultiple(true);
        when(consultaService.findOneAdmin(1L)).thenReturn(consulta);
        FitxerDto fitxer = FitxerDto.builder().nom("multiple.pdf").contingut(new byte[]{1, 2}).build();
        when(consultaService.obtenirJustificantMultipleConcatenat(1L)).thenReturn(fitxer);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new TestOutputStream());

        assertNull(controller.justificant(request, response, 1L, new ExtendedModelMap()));
        verify(response).setHeader("Content-Disposition", "attachment; filename=\"multiple.pdf\"");
    }

    @Test
    public void justificantAmbErrorRedirigeix() throws Exception {
        entitatDelegat();
        ConsultaDto consulta = new ConsultaDto();
        when(consultaService.findOneAdmin(1L)).thenReturn(consulta);
        JustificantDto justificant = JustificantDto.builder().error(true).build();
        when(consultaService.obtenirJustificant(1L, true)).thenReturn(justificant);

        assertEquals("redirect:../../consulta", controller.justificant(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void justificantAmbConsultaNotFoundLlancaExcepcio() throws Exception {
        entitatDelegat();
        when(consultaService.findOneAdmin(1L)).thenThrow(new ConsultaNotFoundException());
        when(historicConsultaService.findOneAdmin(1L)).thenThrow(new ConsultaNotFoundException());

        assertThrows(ConsultaNotFoundException.class,
                () -> controller.justificant(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void justificantAmbExcepcioGenericaRedirigeix() throws Exception {
        entitatDelegat();
        when(consultaService.findOneAdmin(1L)).thenThrow(new RuntimeException("error"));
        when(historicConsultaService.findOneAdmin(1L)).thenThrow(new RuntimeException("error"));

        assertEquals("redirect:../../consulta", controller.justificant(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    // ------------------------- justificantPdf -------------------------

    @Test
    public void justificantPdfAmbExitEscriuElFitxer() throws Exception {
        FitxerDto fitxer = FitxerDto.builder().nom("just.pdf").contingut(new byte[]{1, 2}).build();
        when(consultaService.obtenirJustificantMultipleConcatenat(1L)).thenReturn(fitxer);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new TestOutputStream());

        assertNull(controller.justificantPdf(request, response, 1L, new ExtendedModelMap()));
        verify(response).setHeader("Content-Disposition", "attachment; filename=\"just.pdf\"");
    }

    @Test
    public void justificantPdfAmbConsultaNotFoundLlancaExcepcio() throws Exception {
        when(consultaService.obtenirJustificantMultipleConcatenat(1L)).thenThrow(new ConsultaNotFoundException());
        when(historicConsultaService.obtenirJustificantMultipleConcatenat(1L)).thenThrow(new ConsultaNotFoundException());

        assertThrows(ConsultaNotFoundException.class,
                () -> controller.justificantPdf(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void justificantPdfAmbExcepcioGenericaRedirigeix() throws Exception {
        when(consultaService.obtenirJustificantMultipleConcatenat(1L)).thenThrow(new RuntimeException("error"));
        when(historicConsultaService.obtenirJustificantMultipleConcatenat(1L)).thenThrow(new RuntimeException("error"));

        assertEquals("redirect:../../consulta", controller.justificantPdf(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    // ------------------------- justificantZip -------------------------

    @Test
    public void justificantZipAmbExitEscriuElFitxer() throws Exception {
        FitxerDto fitxer = FitxerDto.builder().nom("just.zip").contingut(new byte[]{1, 2}).build();
        when(consultaService.obtenirJustificantMultipleZip(1L)).thenReturn(fitxer);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new TestOutputStream());

        assertNull(controller.justificantZip(request, response, 1L, new ExtendedModelMap()));
        verify(response).setHeader("Content-Disposition", "attachment; filename=\"just.zip\"");
    }

    @Test
    public void justificantZipAmbConsultaNotFoundLlancaExcepcio() throws Exception {
        when(consultaService.obtenirJustificantMultipleZip(1L)).thenThrow(new ConsultaNotFoundException());
        when(historicConsultaService.obtenirJustificantMultipleZip(1L)).thenThrow(new ConsultaNotFoundException());

        assertThrows(ConsultaNotFoundException.class,
                () -> controller.justificantZip(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    // ------------------------- justificantInline -------------------------

    @Test
    public void justificantInlineSenseDelegatLlancaExcepcio() {
        assertThrows(AccessDenegatException.class,
                () -> controller.justificantInline(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void justificantInlineSenseEntitatLlancaExcepcio() {
        when(session.getAttribute("EntitatHelper.entitat.actual.delegat")).thenReturn(Boolean.TRUE);

        assertThrows(EntitatNotFoundException.class,
                () -> controller.justificantInline(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void justificantInlineAmbRolAdministradorSenseEntitatFuncionaCorrectament() throws Exception {
        when(session.getAttribute("RolHelper.rol.actual")).thenReturn("PBL_ADMIN");
        JustificantDto justificant = JustificantDto.builder().nom("just.pdf").contingut(new byte[]{1}).error(false).build();
        when(consultaService.obtenirJustificant(1L, true)).thenReturn(justificant);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new TestOutputStream());

        controller.justificantInline(request, response, 1L, new ExtendedModelMap());

        verify(response).setContentType("application/pdf");
    }

    @Test
    public void justificantInlineAmbExitEscriuElFitxer() throws Exception {
        entitatDelegat();
        JustificantDto justificant = JustificantDto.builder().nom("just.pdf").contingut(new byte[]{1}).error(false).build();
        when(consultaService.obtenirJustificant(1L, true)).thenReturn(justificant);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new TestOutputStream());

        controller.justificantInline(request, response, 1L, new ExtendedModelMap());

        verify(response).setContentType("application/pdf");
    }

    @Test
    public void justificantInlineAmbErrorLlancaExcepcio() throws Exception {
        entitatDelegat();
        JustificantDto justificant = JustificantDto.builder().error(true).build();
        when(consultaService.obtenirJustificant(1L, true)).thenReturn(justificant);

        assertThrows(JustificantGeneracioException.class,
                () -> controller.justificantInline(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    // ------------------------- xmlZip -------------------------

    @Test
    public void xmlZipAmbExitEscriuElFitxer() throws Exception {
        FitxerDto fitxer = FitxerDto.builder().nom("xmls.zip").contingut(new byte[]{1, 2}).build();
        when(consultaService.descarregarXmlTokensZip(1L)).thenReturn(fitxer);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new TestOutputStream());

        assertNull(controller.xmlZip(request, response, 1L, new ExtendedModelMap()));
    }

    @Test
    public void xmlZipAmbFitxerBuitMostraAvis() throws Exception {
        when(consultaService.descarregarXmlTokensZip(1L)).thenReturn(null);

        assertEquals("redirect:../../consulta", controller.xmlZip(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void xmlZipAmbExcepcioRedirigeix() throws Exception {
        when(consultaService.descarregarXmlTokensZip(1L)).thenThrow(new RuntimeException("error"));

        assertEquals("redirect:../../consulta", controller.xmlZip(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    // ------------------------- justificantPrevisualitzacio -------------------------

    @Test
    public void justificantPrevisualitzacioSenseDelegatRetornaError() {
        var resposta = controller.justificantPrevisualitzacio(request, 1L);
        assertTrue(resposta.isError());
    }

    @Test
    public void justificantPrevisualitzacioSenseEntitatRetornaError() {
        when(session.getAttribute("EntitatHelper.entitat.actual.delegat")).thenReturn(Boolean.TRUE);

        var resposta = controller.justificantPrevisualitzacio(request, 1L);
        assertTrue(resposta.isError());
    }

    @Test
    public void justificantPrevisualitzacioAmbRolAdministradorSenseEntitatFuncionaCorrectament() throws Exception {
        when(session.getAttribute("RolHelper.rol.actual")).thenReturn("PBL_ADMIN");
        JustificantDto justificant = JustificantDto.builder().nom("just.pdf").error(false).build();
        when(consultaService.obtenirJustificant(1L, true)).thenReturn(justificant);

        var resposta = controller.justificantPrevisualitzacio(request, 1L);
        assertTrue(!resposta.isError());
    }

    @Test
    public void justificantPrevisualitzacioAmbExit() throws Exception {
        entitatDelegat();
        JustificantDto justificant = JustificantDto.builder().nom("just.pdf").error(false).build();
        when(consultaService.obtenirJustificant(1L, true)).thenReturn(justificant);

        var resposta = controller.justificantPrevisualitzacio(request, 1L);
        assertTrue(!resposta.isError());
    }

    @Test
    public void justificantPrevisualitzacioAmbErrorIntern() throws Exception {
        entitatDelegat();
        JustificantDto justificant = JustificantDto.builder().error(true).build();
        when(consultaService.obtenirJustificant(1L, true)).thenReturn(justificant);

        var resposta = controller.justificantPrevisualitzacio(request, 1L);
        assertTrue(resposta.isError());
    }

    @Test
    public void justificantPrevisualitzacioAmbConsultaNotFound() throws Exception {
        entitatDelegat();
        when(consultaService.obtenirJustificant(1L, true)).thenThrow(new ConsultaNotFoundException());
        when(historicConsultaService.obtenirJustificant(1L, true)).thenThrow(new ConsultaNotFoundException());

        var resposta = controller.justificantPrevisualitzacio(request, 1L);
        assertTrue(resposta.isError());
    }

    @Test
    public void justificantPrevisualitzacioAmbExcepcioGenerica() throws Exception {
        entitatDelegat();
        when(consultaService.obtenirJustificant(1L, true)).thenThrow(new RuntimeException("error"));
        when(historicConsultaService.obtenirJustificant(1L, true)).thenThrow(new RuntimeException("error"));

        var resposta = controller.justificantPrevisualitzacio(request, 1L);
        assertTrue(resposta.isError());
    }

    // ------------------------- xmlPeticio / xmlResposta -------------------------

    @Test
    public void xmlPeticioMostraXml() throws Exception {
        when(consultaService.findOneAdmin(1L)).thenReturn(new ConsultaDto());

        Model model = new ExtendedModelMap();
        assertEquals("consultaXml", controller.xmlPeticio(request, mock(HttpServletResponse.class), 1L, model));
        assertEquals(Boolean.TRUE, model.getAttribute("mostrarPeticio"));
    }

    @Test
    public void xmlRespostaMostraXml() throws Exception {
        when(consultaService.findOneAdmin(1L)).thenReturn(new ConsultaDto());

        Model model = new ExtendedModelMap();
        assertEquals("consultaXml", controller.xmlResposta(request, mock(HttpServletResponse.class), 1L, model));
        assertEquals(Boolean.TRUE, model.getAttribute("mostrarResposta"));
    }

    // ------------------------- get(consultaId): branques addicionals -------------------------

    @Test
    public void getConsultaAmbEstatErrorNoMostraDadesResposta() throws Exception {
        entitatDelegat();
        ConsultaDto consulta = new ConsultaDto();
        consulta.setServeiCodi("SERV1");
        consulta.setEstat(ConsultaDto.EstatTipus.Error.name());
        when(consultaService.findOneAdmin(1L)).thenReturn(consulta);
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(new ServeiDto());
        when(serveiService.generarArbreDadesEspecifiques("SERV1")).thenReturn(new ArbreDto<>());
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of());
        when(serveiService.findServeiCampGrups("SERV1")).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("adminConsultaInfo", controller.get(request, 1L, null, model));
        assertTrue(!model.containsAttribute("dadesResposta"));
    }

    @Test
    public void getConsultaHistoricAmbRolAdministradorMostraFlagRecuperar() throws Exception {
        when(session.getAttribute("consulta_admin")).thenReturn(Boolean.TRUE);
        when(session.getAttribute("RolHelper.rol.actual")).thenReturn("PBL_ADMIN");
        ConsultaDto consulta = new ConsultaDto();
        consulta.setServeiCodi("SERV1");
        consulta.setEstat(ConsultaDto.EstatTipus.Tramitada.name());
        when(historicConsultaService.findOneAdmin(1L)).thenReturn(consulta);
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(new ServeiDto());
        when(serveiService.generarArbreDadesEspecifiques("SERV1")).thenReturn(new ArbreDto<>());
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of());
        when(serveiService.findServeiCampGrups("SERV1")).thenReturn(List.of());
        when(consultaService.generarArbreResposta(1L)).thenReturn(new ArbreRespostaDto());

        Model model = new ExtendedModelMap();
        assertEquals("adminConsultaInfo", controller.get(request, 1L, null, model));
        assertEquals(Boolean.TRUE, model.getAttribute("potRecuperarRespostaConsultaMultiple"));
    }

    // ------------------------- recuperarResposta: branques addicionals -------------------------

    @Test
    public void recuperarRespostaAmbExcepcioGenericaCapturada() throws Exception {
        org.mockito.Mockito.doThrow(new RuntimeException("error"))
                .when(consultaService).recuperarRespostaConsultaMultiple(1L);

        assertEquals("redirect:../1", controller.recuperarResposta(request, 1L));
    }

    @Test
    public void recuperarRespostaAmbConsultaNotFoundLlancaExcepcio() throws Exception {
        org.mockito.Mockito.doThrow(new ConsultaNotFoundException())
                .when(consultaService).recuperarRespostaConsultaMultiple(1L);

        assertThrows(ConsultaNotFoundException.class, () -> controller.recuperarResposta(request, 1L));
    }

    // ------------------------- getCommandInstance: filtre ja present a sessió -------------------------

    @Test
    public void getAmbFiltreASessioIEntitatIdOmpleProcedimentsIServeisDeLaEntitat() throws Exception {
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        command.setEntitatId(5L);
        command.setProcediment(7L);
        when(session.getAttribute("ConsultaAdminController.session.filtre")).thenReturn(command);
        when(entitatService.findAll()).thenReturn(List.of());
        when(procedimentService.findAmbEntitat(5L)).thenReturn(List.of());
        when(serveiService.findAmbEntitatIProcediment(5L, 7L)).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("adminConsultes", controller.get(request, model));
        assertTrue(model.containsAttribute("filtreCommand"));
        assertTrue(model.containsAttribute("serveis"));
    }

    private static class TestOutputStream extends javax.servlet.ServletOutputStream {
        @Override
        public void write(int b) {
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
