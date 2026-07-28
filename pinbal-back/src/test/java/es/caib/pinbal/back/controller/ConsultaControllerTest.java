package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.ConsultaCommand;
import es.caib.pinbal.back.command.ConsultaFiltreCommand;
import es.caib.pinbal.logic.intf.dto.*;
import es.caib.pinbal.logic.intf.service.*;
import es.caib.pinbal.logic.intf.service.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ConsultaControllerTest {

    private ConsultaController controller;
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
        controller = new ConsultaController();
        entitatService = mock(EntitatService.class);
        procedimentService = mock(ProcedimentService.class);
        serveiService = mock(ServeiService.class);
        consultaService = mock(ConsultaService.class);
        historicConsultaService = mock(HistoricConsultaService.class);
        usuariService = mock(UsuariService.class);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        ControllerTestSupport.setField(controller, "entitatService", entitatService);
        ControllerTestSupport.setField(controller, "procedimentService", procedimentService);
        ControllerTestSupport.setField(controller, "serveiService", serveiService);
        ControllerTestSupport.setField(controller, "consultaService", consultaService);
        ControllerTestSupport.setField(controller, "historicConsultaService", historicConsultaService);
        ControllerTestSupport.setField(controller, "usuariService", usuariService);
        ControllerTestSupport.setField(controller, "validator", validator);
        controller.setMessageSource(ControllerTestSupport.mockMessageSourceEcoDeLaClau());
        request = ControllerTestSupport.mockRequest();
        when(request.getParameterNames()).thenReturn(java.util.Collections.emptyEnumeration());
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
    public void getAmbDelegatIEntitatMostraLlistat() throws Exception {
        entitatDelegat();
        when(usuariService.getDades()).thenReturn(new UsuariDto());
        when(procedimentService.findAmbEntitatPerDelegat(1L)).thenReturn(List.of());
        when(serveiService.findPermesosAmbProcedimentPerDelegat(eq(1L), any())).thenReturn(List.of());

        assertEquals("consulta", controller.get(request, new ExtendedModelMap()));
    }

    @Test
    public void postSenseDelegatRetornaNoAutoritzat() throws Exception {
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("delegatNoAutoritzat", controller.post(request, command, bindingResult, null, new ExtendedModelMap()));
    }

    @Test
    public void postAmbAccioNetejarEsborraFiltreIRedirigeix() throws Exception {
        entitatDelegat();
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:.", controller.post(request, command, bindingResult, "netejar", new ExtendedModelMap()));
        verify(session).removeAttribute("ConsultaController.session.filtre");
    }

    @Test
    public void postSenseErrorsGuardaFiltreIRedirigeix() throws Exception {
        entitatDelegat();
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:.", controller.post(request, command, bindingResult, null, new ExtendedModelMap()));
        verify(session).setAttribute("ConsultaController.session.filtre", command);
    }

    @Test
    public void postAmbErrorsTornaAlLlistat() throws Exception {
        entitatDelegat();
        when(usuariService.getDades()).thenReturn(new UsuariDto());
        when(procedimentService.findAmbEntitatPerDelegat(1L)).thenReturn(List.of());
        when(serveiService.findPermesosAmbProcedimentPerDelegat(eq(1L), any())).thenReturn(List.of());
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("consulta", controller.post(request, command, bindingResult, null, new ExtendedModelMap()));
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
        when(consultaService.findSimplesByFiltrePaginatPerDelegat(any(), any(), any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(new ConsultaDto())));

        var resposta = controller.datatable(request, new ExtendedModelMap());

        assertEquals(1, resposta.getRecordsFiltered());
    }

    // ------------------------- excel -------------------------

    @Test
    public void excelSenseAutoritzacioRetornaNoAutoritzat() throws Exception {
        assertEquals("representantNoAutoritzat", controller.excel(request, new ExtendedModelMap()));
    }

    @Test
    public void excelAmbRepresentantRetornaVista() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);
        entitatDelegat();
        when(consultaService.findSimplesByFiltrePaginatPerDelegat(eq(1L), any(), any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(new ConsultaDto())));

        Model model = new ExtendedModelMap();
        assertEquals("consultaExcelView", controller.excel(request, model));
        assertTrue(model.containsAttribute("consultaList"));
    }

    // ------------------------- newGet -------------------------

    private void autenticarSecurityContext() {
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("usuari1", "pwd"));
    }

    @org.junit.jupiter.api.AfterEach
    public void netejarSecurityContext() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    public void newGetAmbUsuariInactiuRetornaNoAutoritzat() throws Exception {
        entitatDelegat();
        autenticarSecurityContext();
        EntitatUsuariDto entitatUsuari = new EntitatUsuariDto(new UsuariDto(), "Dept", false, false, false, false, false, false);
        when(usuariService.getEntitatUsuari(1L, "usuari1")).thenReturn(entitatUsuari);

        assertEquals("delegatNoAutoritzat", controller.newGet(request, "SERV1", new ExtendedModelMap()));
    }

    @Test
    public void newGetAmbUsuariActiuMostraFormulari() throws Exception {
        entitatDelegat();
        autenticarSecurityContext();
        EntitatUsuariDto entitatUsuari = new EntitatUsuariDto(new UsuariDto(), "Dept", false, false, false, false, false, true);
        when(usuariService.getEntitatUsuari(1L, "usuari1")).thenReturn(entitatUsuari);
        when(procedimentService.findActiusAmbEntitatIServeiCodi(1L, "SERV1")).thenReturn(List.of());
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(ServeiDto.builder().build());
        when(serveiService.generarArbreDadesEspecifiques("SERV1")).thenReturn(new ArbreDto<>());
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of());
        when(serveiService.findServeiCampGrups("SERV1")).thenReturn(List.of());
        when(usuariService.getDades()).thenReturn(new UsuariDto());

        Model model = new ExtendedModelMap();
        assertEquals("consultaForm", controller.newGet(request, "SERV1", model));
        assertTrue(model.containsAttribute("consultaCommand"));
    }

    // ------------------------- newPost -------------------------

    @Test
    public void newPostSenseDelegatRetornaNoAutoritzat() throws Exception {
        ConsultaCommand command = new ConsultaCommand("SERV1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("delegatNoAutoritzat", controller.newPost(request, "SERV1", command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void newPostSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.delegat")).thenReturn(Boolean.TRUE);
        ConsultaCommand command = new ConsultaCommand("SERV1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../../index", controller.newPost(request, "SERV1", command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void newPostAmbErrorsDeBindingTornaAlFormulari() throws Exception {
        entitatDelegat();
        when(procedimentService.findActiusAmbEntitatIServeiCodi(1L, "SERV1")).thenReturn(List.of());
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(ServeiDto.builder().build());
        when(serveiService.generarArbreDadesEspecifiques("SERV1")).thenReturn(new ArbreDto<>());
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of());
        when(serveiService.findServeiCampGrups("SERV1")).thenReturn(List.of());
        when(usuariService.getDades()).thenReturn(new UsuariDto());
        ConsultaCommand command = new ConsultaCommand("SERV1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("consultaForm", controller.newPost(request, "SERV1", command, bindingResult, new ExtendedModelMap()));
    }

    private ConsultaCommand comandaValida() {
        ConsultaCommand command = new ConsultaCommand();
        command.setProcedimentId(1L);
        command.setServeiCodi("SERV1");
        command.setFuncionariNom("Nom Funcionari");
        command.setDepartamentNom("Departament");
        command.setConsentiment(ConsultaDto.Consentiment.Si);
        command.setFinalitat("Finalitat");
        command.setTitularDocumentTipus(ConsultaDto.DocumentTipus.NIF);
        command.setMultiple(false);
        return command;
    }

    private void stubDependenciesFormulariBuit() throws Exception {
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of());
        when(serveiService.findServeiCampGrupsAndSubgrups("SERV1")).thenReturn(List.of());
        when(serveiService.getCampsByserveiRegla(eq("SERV1"), any())).thenReturn(List.of());
        when(serveiService.getGrupsByserveiRegla(eq("SERV1"), any())).thenReturn(List.of());
        when(serveiService.generarArbreDadesEspecifiques("SERV1")).thenReturn(new ArbreDto<>());
        when(serveiService.findServeiCampGrups("SERV1")).thenReturn(List.of());
        when(procedimentService.findActiusAmbEntitatIServeiCodi(1L, "SERV1")).thenReturn(List.of());
        when(usuariService.getDades()).thenReturn(new UsuariDto());
    }

    @Test
    public void newPostSimpleAmbExitRedirigeix() throws Exception {
        entitatDelegat();
        ServeiDto servei = ServeiDto.builder().build();
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(servei);
        stubDependenciesFormulariBuit();
        ConsultaCommand command = comandaValida();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        ConsultaDto resultat = new ConsultaDto();
        resultat.setEstat(ConsultaDto.EstatTipus.Tramitada.name());
        when(consultaService.peticioSincrona(any())).thenReturn(resultat);

        assertEquals("redirect:../../consulta", controller.newPost(request, "SERV1", command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void newPostSimpleAmbErrorEnResultatTornaAlFormulari() throws Exception {
        entitatDelegat();
        ServeiDto servei = ServeiDto.builder().build();
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(servei);
        stubDependenciesFormulariBuit();
        ConsultaCommand command = comandaValida();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        ConsultaDto resultat = new ConsultaDto();
        resultat.setEstat(ConsultaDto.EstatTipus.Error.name());
        when(consultaService.peticioSincrona(any())).thenReturn(resultat);

        Model model = new ExtendedModelMap();
        assertEquals("consultaForm", controller.newPost(request, "SERV1", command, bindingResult, model));
        assertTrue(model.containsAttribute("reintentar"));
    }

    @Test
    public void newPostSimpleAmbConsultaScspExceptionTornaAlFormulari() throws Exception {
        entitatDelegat();
        ServeiDto servei = ServeiDto.builder().build();
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(servei);
        stubDependenciesFormulariBuit();
        ConsultaCommand command = comandaValida();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        when(consultaService.peticioSincrona(any())).thenThrow(new ConsultaScspComunicacioException("1", "error scsp"));

        Model model = new ExtendedModelMap();
        assertEquals("consultaForm", controller.newPost(request, "SERV1", command, bindingResult, model));
        assertTrue(model.containsAttribute("reintentar"));
    }

    @Test
    public void newPostMultipleAmbFitxerTipusInvalidTornaAlFormulari() throws Exception {
        entitatDelegat();
        ServeiDto servei = ServeiDto.builder().scspMaxSolicitudesPeticion(0).build();
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(servei);
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of());
        stubDependenciesFormulariBuit();
        ConsultaCommand command = comandaValida();
        command.setMultiple(true);
        command.setMultipleFitxer(new MockMultipartFile("multipleFitxer", "fitxer.txt", "text/plain", "contingut".getBytes()));
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("consultaForm", controller.newPost(request, "SERV1", command, bindingResult, new ExtendedModelMap()));
        assertTrue(bindingResult.hasFieldErrors("multipleFitxer"));
    }

    // ------------------------- errorsDownload -------------------------

    @Test
    public void errorsDownloadAmbUuidInexistentRetornaError() throws Exception {
        JsonResponse resposta = controller.errorsDownload(request, mock(HttpServletResponse.class), "no-existeix");
        assertTrue(resposta.isError());
    }

    // ------------------------- info -------------------------

    @Test
    public void infoSenseDelegatRetornaNoAutoritzat() throws Exception {
        assertEquals("delegatNoAutoritzat", controller.info(request, 1L, null, new ExtendedModelMap()));
    }

    @Test
    public void infoSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.delegat")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../../index", controller.info(request, 1L, null, new ExtendedModelMap()));
    }

    @Test
    public void infoAmbConsultaSenseErrorMostraDadesResposta() throws Exception {
        entitatDelegat();
        ConsultaDto consulta = new ConsultaDto();
        consulta.setServeiCodi("SERV1");
        consulta.setEstat(ConsultaDto.EstatTipus.Tramitada.name());
        when(consultaService.findOneDelegat(1L)).thenReturn(consulta);
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(ServeiDto.builder().build());
        when(serveiService.generarArbreDadesEspecifiques("SERV1")).thenReturn(new ArbreDto<>());
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of());
        when(serveiService.findServeiCampGrups("SERV1")).thenReturn(List.of());
        when(consultaService.generarArbreResposta(1L)).thenReturn(new ArbreRespostaDto());

        Model model = new ExtendedModelMap();
        assertEquals("consultaInfo", controller.info(request, 1L, true, model));
        assertTrue(model.containsAttribute("dadesResposta"));
        assertTrue(model.containsAttribute("multiple"));
    }

    @Test
    public void infoAmbConsultaAmbErrorNoMostraDadesResposta() throws Exception {
        entitatDelegat();
        ConsultaDto consulta = new ConsultaDto();
        consulta.setServeiCodi("SERV1");
        consulta.setEstat(ConsultaDto.EstatTipus.Error.name());
        when(consultaService.findOneDelegat(1L)).thenReturn(consulta);
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(ServeiDto.builder().build());
        when(serveiService.generarArbreDadesEspecifiques("SERV1")).thenReturn(new ArbreDto<>());
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of());
        when(serveiService.findServeiCampGrups("SERV1")).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("consultaInfo", controller.info(request, 1L, null, model));
        assertTrue(!model.containsAttribute("dadesResposta"));
    }

    // ------------------------- justificantArxiuDetall -------------------------

    @Test
    public void justificantArxiuDetallOmpleModel() {
        when(consultaService.obtenirArxiuInfo(1L)).thenReturn(null);

        Model model = new ExtendedModelMap();
        assertEquals("contingutArxiu", controller.justificantArxiuDetall(request, mock(HttpServletResponse.class), 1L, model));
        assertTrue(model.containsAttribute("mostrarArxiuInfo"));
    }

    // ------------------------- justificant -------------------------

    @Test
    public void justificantSenseDelegatRetornaNoAutoritzat() throws Exception {
        assertEquals("delegatNoAutoritzat", controller.justificant(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void justificantSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.delegat")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../index", controller.justificant(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void justificantAmbExitEscriuElFitxer() throws Exception {
        entitatDelegat();
        JustificantDto justificant = JustificantDto.builder().nom("just.pdf").contingut(new byte[]{1, 2, 3}).error(false).build();
        when(consultaService.obtenirJustificant(1L, false)).thenReturn(justificant);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream());

        assertNull(controller.justificant(request, response, 1L, new ExtendedModelMap()));
        verify(response).setHeader("Content-Disposition", "attachment; filename=\"just.pdf\"");
    }

    @Test
    public void justificantAmbErrorRedirigeix() throws Exception {
        entitatDelegat();
        JustificantDto justificant = JustificantDto.builder().error(true).build();
        when(consultaService.obtenirJustificant(1L, false)).thenReturn(justificant);

        assertEquals("redirect:../../consulta", controller.justificant(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void justificantAmbConsultaNotFoundLlancaExcepcio() throws Exception {
        entitatDelegat();
        when(consultaService.obtenirJustificant(1L, false)).thenThrow(new ConsultaNotFoundException());
        when(historicConsultaService.obtenirJustificant(1L, false)).thenThrow(new ConsultaNotFoundException());

        assertThrows(ConsultaNotFoundException.class,
                () -> controller.justificant(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void justificantAmbExcepcioGenericaRedirigeix() throws Exception {
        entitatDelegat();
        when(consultaService.obtenirJustificant(1L, false)).thenThrow(new RuntimeException("error"));
        when(historicConsultaService.obtenirJustificant(1L, false)).thenThrow(new RuntimeException("error"));

        assertEquals("redirect:../../consulta", controller.justificant(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
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
    public void justificantInlineAmbExitEscriuElFitxer() throws Exception {
        entitatDelegat();
        JustificantDto justificant = JustificantDto.builder().nom("just.pdf").contingut(new byte[]{1, 2, 3}).error(false).build();
        when(consultaService.obtenirJustificant(1L, false)).thenReturn(justificant);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream());

        controller.justificantInline(request, response, 1L, new ExtendedModelMap());

        verify(response).setContentType("application/pdf");
    }

    @Test
    public void justificantInlineAmbErrorLlancaExcepcio() throws Exception {
        entitatDelegat();
        JustificantDto justificant = JustificantDto.builder().error(true).build();
        when(consultaService.obtenirJustificant(1L, false)).thenReturn(justificant);

        assertThrows(JustificantGeneracioException.class,
                () -> controller.justificantInline(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
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
    public void justificantPrevisualitzacioAmbExit() throws Exception {
        entitatDelegat();
        JustificantDto justificant = JustificantDto.builder().nom("just.pdf").error(false).build();
        when(consultaService.obtenirJustificant(1L, false)).thenReturn(justificant);

        var resposta = controller.justificantPrevisualitzacio(request, 1L);
        assertTrue(!resposta.isError());
    }

    @Test
    public void justificantPrevisualitzacioAmbErrorIntern() throws Exception {
        entitatDelegat();
        JustificantDto justificant = JustificantDto.builder().error(true).build();
        when(consultaService.obtenirJustificant(1L, false)).thenReturn(justificant);

        var resposta = controller.justificantPrevisualitzacio(request, 1L);
        assertTrue(resposta.isError());
    }

    @Test
    public void justificantPrevisualitzacioAmbConsultaNotFound() throws Exception {
        entitatDelegat();
        when(consultaService.obtenirJustificant(1L, false)).thenThrow(new ConsultaNotFoundException());
        when(historicConsultaService.obtenirJustificant(1L, false)).thenThrow(new ConsultaNotFoundException());

        var resposta = controller.justificantPrevisualitzacio(request, 1L);
        assertTrue(resposta.isError());
    }

    // ------------------------- justificantReintentar -------------------------

    @Test
    public void justificantReintentarSenseDelegatRetornaNoAutoritzat() throws Exception {
        assertEquals("delegatNoAutoritzat", controller.justificantReintentar(request, mock(HttpServletResponse.class), 1L, null));
    }

    @Test
    public void justificantReintentarSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.delegat")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../index", controller.justificantReintentar(request, mock(HttpServletResponse.class), 1L, null));
    }

    @Test
    public void justificantReintentarAmbExitIInfoRedirigeixAInfo() throws Exception {
        entitatDelegat();
        JustificantDto justificant = JustificantDto.builder().error(false).build();
        when(consultaService.reintentarGeneracioJustificant(1L, false, false)).thenReturn(justificant);

        assertEquals("redirect:../../consulta/1",
                controller.justificantReintentar(request, mock(HttpServletResponse.class), 1L, true));
    }

    @Test
    public void justificantReintentarAmbErrorRedirigeixAllistat() throws Exception {
        entitatDelegat();
        JustificantDto justificant = JustificantDto.builder().error(true).build();
        when(consultaService.reintentarGeneracioJustificant(1L, false, false)).thenReturn(justificant);

        assertEquals("redirect:../../consulta",
                controller.justificantReintentar(request, mock(HttpServletResponse.class), 1L, null));
    }

    @Test
    public void justificantReintentarAmbConsultaNotFoundLlancaExcepcio() throws Exception {
        entitatDelegat();
        when(consultaService.reintentarGeneracioJustificant(1L, false, false)).thenThrow(new ConsultaNotFoundException());

        assertThrows(ConsultaNotFoundException.class,
                () -> controller.justificantReintentar(request, mock(HttpServletResponse.class), 1L, null));
    }

    @Test
    public void justificantReintentarAmbExcepcioGenericaRedirigeix() throws Exception {
        entitatDelegat();
        when(consultaService.reintentarGeneracioJustificant(1L, false, false)).thenThrow(new RuntimeException("error"));

        assertEquals("redirect:../../consulta",
                controller.justificantReintentar(request, mock(HttpServletResponse.class), 1L, null));
    }

    // ------------------------- xmlPeticio / xmlResposta -------------------------

    @Test
    public void xmlPeticioSenseDelegatRetornaNoAutoritzat() throws Exception {
        assertEquals("delegatNoAutoritzat", controller.xmlPeticio(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void xmlPeticioSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.delegat")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../index", controller.xmlPeticio(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void xmlPeticioAmbEntitatMostraXml() throws Exception {
        entitatDelegat();
        when(consultaService.findOneDelegat(1L)).thenReturn(new ConsultaDto());

        Model model = new ExtendedModelMap();
        assertEquals("consultaXml", controller.xmlPeticio(request, mock(HttpServletResponse.class), 1L, model));
        assertEquals(Boolean.TRUE, model.getAttribute("mostrarPeticio"));
    }

    @Test
    public void xmlRespostaAmbEntitatMostraXml() throws Exception {
        entitatDelegat();
        when(consultaService.findOneDelegat(1L)).thenReturn(new ConsultaDto());

        Model model = new ExtendedModelMap();
        assertEquals("consultaXml", controller.xmlResposta(request, mock(HttpServletResponse.class), 1L, model));
        assertEquals(Boolean.TRUE, model.getAttribute("mostrarResposta"));
    }

    // ------------------------- serveisPermesosPerProcediment -------------------------

    @Test
    public void serveisPermesosPerProcedimentAmbIdSenseDelegatNoOmpleModel() throws Exception {
        Model model = new ExtendedModelMap();
        assertEquals("serveiSelectJson", controller.serveisPermesosPerProcedimentAmbId(request, mock(HttpServletResponse.class), 1L, model));
        assertTrue(model.asMap().isEmpty());
    }

    @Test
    public void serveisPermesosPerProcedimentAmbIdAmbDelegatOmpleModel() throws Exception {
        entitatDelegat();
        when(serveiService.findPermesosAmbProcedimentPerDelegat(1L, 2L)).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("serveiSelectJson", controller.serveisPermesosPerProcedimentAmbId(request, mock(HttpServletResponse.class), 2L, model));
        assertTrue(model.containsAttribute("serveis"));
    }

    @Test
    public void serveisPermesosPerProcedimentSenseIdDelegaAAmbId() throws Exception {
        Model model = new ExtendedModelMap();
        assertEquals("serveiSelectJson", controller.serveisPermesosPerProcedimentSenseId(request, mock(HttpServletResponse.class), model));
    }

    // ------------------------- plantillaCsvGet -------------------------

    @Test
    public void plantillaCsvGetSenseDelegatRetornaNoAutoritzat() throws Exception {
        assertEquals("delegatNoAutoritzat", controller.plantillaCsvGet(request, "SERV1", "CSV", new ExtendedModelMap()));
    }

    @Test
    public void plantillaCsvGetSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.delegat")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../../index", controller.plantillaCsvGet(request, "SERV1", "CSV", new ExtendedModelMap()));
    }

    @Test
    public void plantillaCsvGetAmbTipusCsvRetornaVistaCsv() throws Exception {
        entitatDelegat();
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(ServeiDto.builder().build());
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of());

        assertEquals("peticioMultiplePlantillaCsvView", controller.plantillaCsvGet(request, "SERV1", "CSV", new ExtendedModelMap()));
    }

    @Test
    public void plantillaCsvGetAmbTipusOdsRetornaVistaOds() throws Exception {
        entitatDelegat();
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(ServeiDto.builder().build());
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of());

        assertEquals("peticioMultiplePlantillaOdsView", controller.plantillaCsvGet(request, "SERV1", "ODS", new ExtendedModelMap()));
    }

    @Test
    public void plantillaCsvGetAmbAltreTipusRetornaVistaExcel() throws Exception {
        entitatDelegat();
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(ServeiDto.builder().build());
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of());

        assertEquals("peticioMultiplePlantillaExcelView", controller.plantillaCsvGet(request, "SERV1", "EXCEL", new ExtendedModelMap()));
    }

    // ------------------------- downloadAjuda -------------------------

    @Test
    public void downloadAjudaAmbErrorRedirigeix() throws Exception {
        assertEquals("redirect:servei", controller.downloadAjuda(request, mock(HttpServletResponse.class), "SERV1"));
    }

    @Test
    public void downloadAjudaAmbExitEscriuElFitxer() throws Exception {
        entitatDelegat();
        ServeiDto servei = ServeiDto.builder().fitxerAjudaNom("ajuda.pdf").fitxerAjudaMimeType("application/pdf")
                .fitxerAjudaContingut(new byte[]{1, 2}).build();
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(servei);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream());

        assertNull(controller.downloadAjuda(request, response, "SERV1"));
        verify(response).setContentType("application/pdf");
    }

    // ------------------------- campsRegles / grupsRegles -------------------------

    // ------------------------- get / post: delegat sense entitat -------------------------

    @Test
    public void getAmbDelegatSenseEntitatNoOmpleModelPeroMostraLlistat() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.delegat")).thenReturn(Boolean.TRUE);

        Model model = new ExtendedModelMap();
        assertEquals("consulta", controller.get(request, model));
        assertTrue(!model.containsAttribute("filtreCommand"));
    }

    @Test
    public void postAmbDelegatSenseEntitatRetornaConsulta() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.delegat")).thenReturn(Boolean.TRUE);
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("consulta", controller.post(request, command, bindingResult, null, new ExtendedModelMap()));
    }

    // ------------------------- datatable / excel: historic -------------------------

    @Test
    public void datatableHistoricRetornaResposta() throws Exception {
        entitatDelegat();
        when(session.getAttribute("consulta_delegat")).thenReturn(Boolean.TRUE);
        ControllerTestSupport.mockDatatableParams(request, "scspPeticionId", "creacioData", "procedimentCodiNom", "serveiCodiNom");
        when(historicConsultaService.findSimplesByFiltrePaginatPerDelegat(any(), any(), any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(new ConsultaDto())));

        var resposta = controller.datatable(request, new ExtendedModelMap());

        assertEquals(1, resposta.getRecordsFiltered());
    }

    @Test
    public void excelHistoricRetornaVista() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);
        entitatDelegat();
        when(session.getAttribute("consulta_delegat")).thenReturn(Boolean.TRUE);
        when(historicConsultaService.findSimplesByFiltrePaginatPerDelegat(eq(1L), any(), any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(new ConsultaDto())));

        Model model = new ExtendedModelMap();
        assertEquals("consultaExcelView", controller.excel(request, model));
        assertTrue(model.containsAttribute("consultaList"));
    }

    // ------------------------- newPost: grups de document -------------------------

    @Test
    public void newPostAmbDocumentObligatoriSenseNumeroTornaAlFormulari() throws Exception {
        entitatDelegat();
        ServeiDto servei = ServeiDto.builder().pinbalDocumentObligatori(true).build();
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(servei);
        stubDependenciesFormulariBuit();
        ConsultaCommand command = comandaValida();
        command.setTitularDocumentNum(null);
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("consultaForm", controller.newPost(request, "SERV1", command, bindingResult, new ExtendedModelMap()));
        assertTrue(bindingResult.hasErrors());
    }

    @Test
    public void newPostAmbComprovarDocumentNifOkContinuaElProces() throws Exception {
        entitatDelegat();
        ServeiDto servei = ServeiDto.builder().pinbalComprovarDocument(true).build();
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(servei);
        stubDependenciesFormulariBuit();
        ConsultaCommand command = comandaValida();
        command.setTitularDocumentTipus(ConsultaDto.DocumentTipus.NIF);
        command.setTitularDocumentNum(null);
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        ConsultaDto resultat = new ConsultaDto();
        resultat.setEstat(ConsultaDto.EstatTipus.Tramitada.name());
        when(consultaService.peticioSincrona(any())).thenReturn(resultat);

        assertEquals("redirect:../../consulta", controller.newPost(request, "SERV1", command, bindingResult, new ExtendedModelMap()));
    }

    // ------------------------- newPost: flux múltiple -------------------------

    private static final String CSV_MULTIPLE = "h1\nh2\ncamp1,camp2\nval1,val2\nval3,val4\n";

    @Test
    public void newPostMultipleAmbMassaPeticionsTornaAlFormulari() throws Exception {
        entitatDelegat();
        ServeiDto servei = ServeiDto.builder().scspMaxSolicitudesPeticion(1).build();
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(servei);
        stubDependenciesFormulariBuit();
        ConsultaCommand command = comandaValida();
        command.setMultiple(true);
        command.setMultipleFitxer(new MockMultipartFile("multipleFitxer", "fitxer.csv", "text/csv", CSV_MULTIPLE.getBytes()));
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("consultaForm", controller.newPost(request, "SERV1", command, bindingResult, new ExtendedModelMap()));
        assertTrue(bindingResult.hasFieldErrors("multipleFitxer"));
    }

    @Test
    public void newPostMultipleAmbExitRedirigeixAMultiple() throws Exception {
        entitatDelegat();
        ServeiDto servei = ServeiDto.builder().scspMaxSolicitudesPeticion(0).build();
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(servei);
        stubDependenciesFormulariBuit();
        ConsultaCommand command = comandaValida();
        command.setMultiple(true);
        command.setMultipleFitxer(new MockMultipartFile("multipleFitxer", "fitxer.csv", "text/csv", CSV_MULTIPLE.getBytes()));
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        ConsultaDto resultat = new ConsultaDto();
        resultat.setEstat(ConsultaDto.EstatTipus.Tramitada.name());
        when(consultaService.peticioAsincrona(any())).thenReturn(resultat);

        assertEquals("redirect:../../consulta/multiple", controller.newPost(request, "SERV1", command, bindingResult, new ExtendedModelMap()));
    }

    // ------------------------- errorsDownload: fitxer amb errors present -------------------------

    @Test
    public void errorsDownloadAmbUuidExistentRetornaFitxer() throws Exception {
        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("errors", ".csv");
        java.nio.file.Files.write(tempFile, "contingut".getBytes());
        String uuid = afegirFitxerAmbErrors(tempFile, "errors", "csv", "text/csv");

        JsonResponse resposta = controller.errorsDownload(request, mock(HttpServletResponse.class), uuid);

        assertTrue(!resposta.isError());
    }

    @Test
    public void errorsDownloadAmbErrorLlegintElFitxerRetornaError() throws Exception {
        java.nio.file.Path pathInexistent = java.nio.file.Path.of("/no/existeix/fitxer.csv");
        String uuid = afegirFitxerAmbErrors(pathInexistent, "errors", "csv", "text/csv");

        JsonResponse resposta = controller.errorsDownload(request, mock(HttpServletResponse.class), uuid);

        assertTrue(resposta.isError());
    }

    @SuppressWarnings("unchecked")
    private String afegirFitxerAmbErrors(java.nio.file.Path path, String nom, String extensio, String contentType) throws Exception {
        String uuid = java.util.UUID.randomUUID().toString();
        java.lang.reflect.Field field = ConsultaController.class.getDeclaredField("fitxersAmbErrors");
        field.setAccessible(true);
        Map<String, Object> mapa = (Map<String, Object>) field.get(null);
        Object fitxerErrors = ConsultaController.FitxerErrors.builder()
                .path(path)
                .nom(nom)
                .extensio(extensio)
                .contentType(contentType)
                .build();
        mapa.put(uuid, fitxerErrors);
        return uuid;
    }

    // ------------------------- info / xmlPeticio / justificant: historic -------------------------

    @Test
    public void infoHistoricMostraInfo() throws Exception {
        entitatDelegat();
        when(session.getAttribute("consulta_delegat")).thenReturn(Boolean.TRUE);
        ConsultaDto consulta = new ConsultaDto();
        consulta.setServeiCodi("SERV1");
        consulta.setEstat(ConsultaDto.EstatTipus.Error.name());
        when(historicConsultaService.findOneDelegat(1L)).thenReturn(consulta);
        when(serveiService.findAmbCodiPerDelegat(1L, "SERV1")).thenReturn(ServeiDto.builder().build());
        when(serveiService.generarArbreDadesEspecifiques("SERV1")).thenReturn(new ArbreDto<>());
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of());
        when(serveiService.findServeiCampGrups("SERV1")).thenReturn(List.of());

        assertEquals("consultaInfo", controller.info(request, 1L, null, new ExtendedModelMap()));
    }

    @Test
    public void xmlPeticioHistoricMostraXml() throws Exception {
        entitatDelegat();
        when(session.getAttribute("consulta_delegat")).thenReturn(Boolean.TRUE);
        when(historicConsultaService.findOneDelegat(1L)).thenReturn(new ConsultaDto());

        assertEquals("consultaXml", controller.xmlPeticio(request, mock(HttpServletResponse.class), 1L, new ExtendedModelMap()));
    }

    @Test
    public void justificantHistoricAmbExitEscriuElFitxer() throws Exception {
        entitatDelegat();
        when(session.getAttribute("consulta_delegat")).thenReturn(Boolean.TRUE);
        JustificantDto justificant = JustificantDto.builder().nom("just.pdf").contingut(new byte[]{1}).error(false).build();
        when(historicConsultaService.obtenirJustificant(1L, false)).thenReturn(justificant);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream());

        assertNull(controller.justificant(request, response, 1L, new ExtendedModelMap()));
    }

    @Test
    public void campsReglesDelegaAlServei() throws Exception {
        when(serveiService.getCampsByserveiRegla(eq("SERV1"), any())).thenReturn(List.of());

        assertEquals(List.of(), controller.campsRegles(request, "SERV1", new String[]{"camp1"}));
    }

    @Test
    public void grupsReglesDelegaAlServei() throws Exception {
        when(serveiService.getGrupsByserveiRegla(eq("SERV1"), any())).thenReturn(List.of());

        assertEquals(List.of(), controller.grupsRegles(request, "SERV1", new String[]{"grup1"}));
    }

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
