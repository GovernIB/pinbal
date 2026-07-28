package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.ServeiBusCommand;
import es.caib.pinbal.back.command.ServeiCampCommand;
import es.caib.pinbal.back.command.ServeiCampGrupCommand;
import es.caib.pinbal.back.command.ServeiCommand;
import es.caib.pinbal.back.command.ServeiFiltreCommand;
import es.caib.pinbal.back.command.ServeiJustificantCampCommand;
import es.caib.pinbal.back.command.ServeiReglaCommand;
import es.caib.pinbal.back.command.ServeiXsdCommand;
import es.caib.pinbal.logic.intf.dto.ArbreDto;
import es.caib.pinbal.logic.intf.dto.CodiValor;
import es.caib.pinbal.logic.intf.dto.FitxerDto;
import es.caib.pinbal.logic.intf.dto.ProcedimentDto;
import es.caib.pinbal.logic.intf.dto.ServeiBusDto;
import es.caib.pinbal.logic.intf.dto.ServeiCampDto;
import es.caib.pinbal.logic.intf.dto.ServeiCampGrupDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.logic.intf.dto.ServeiXsdDto;
import es.caib.pinbal.logic.intf.dto.XsdTipusEnumDto;
import es.caib.pinbal.logic.intf.dto.regles.AccioEnum;
import es.caib.pinbal.logic.intf.dto.regles.ModificatEnum;
import es.caib.pinbal.logic.intf.dto.regles.ServeiReglaDto;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.ProcedimentService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.exception.ServeiAmbConsultesException;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServeiControllerTest {

    private ServeiController controller;
    private ServeiService serveiService;
    private EntitatService entitatService;
    private ProcedimentService procedimentService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @BeforeEach
    public void configurar() {
        controller = new ServeiController();
        serveiService = mock(ServeiService.class);
        entitatService = mock(EntitatService.class);
        procedimentService = mock(ProcedimentService.class);
        ControllerTestSupport.setField(controller, "serveiService", serveiService);
        ControllerTestSupport.setField(controller, "entitatService", entitatService);
        ControllerTestSupport.setField(controller, "procedimentService", procedimentService);
        controller.setMessageSource(ControllerTestSupport.mockMessageSourceEcoDeLaClau());
        request = ControllerTestSupport.mockRequest();
        response = mock(HttpServletResponse.class);
        session = request.getSession();
    }

    // ------------------------- get / post (llistat) -------------------------

    @Test
    public void getMostraLlistat() throws Exception {
        when(serveiService.findEmisorAll()).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("serveiList", controller.get(request, response, model));
        assertTrue(model.containsAttribute("emisors"));
    }

    @Test
    public void postAmbErrorsTornaAlLlistat() throws Exception {
        when(serveiService.findEmisorAll()).thenReturn(List.of());
        ServeiFiltreCommand command = new ServeiFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("serveiList", controller.post(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void postSenseErrorsGuardaFiltreIRedirigeix() throws Exception {
        ServeiFiltreCommand command = new ServeiFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:servei", controller.post(request, command, bindingResult, new ExtendedModelMap()));
        verify(session).setAttribute("ServeiController.session.filtre", command);
    }

    // ------------------------- datatable -------------------------

    @Test
    public void datatableRetornaResposta() throws Exception {
        ControllerTestSupport.mockDatatableParams(request, "codi", "descripcio", "scspEmisor");
        when(serveiService.findAmbFiltrePaginat(any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new ServeiDto())));

        var resposta = controller.datatable(request, new ExtendedModelMap());

        assertEquals(1, resposta.getRecordsFiltered());
    }

    // ------------------------- get(new) / get(serveiCodi) -------------------------

    @Test
    public void getNouMostraFormulariBuit() throws Exception {
        when(serveiService.findEmisorAll()).thenReturn(List.of());
        when(serveiService.findClauPublicaAll()).thenReturn(List.of());
        when(serveiService.findClauPrivadaAll()).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("serveiForm", controller.get((String) null, model));
        assertTrue(model.containsAttribute("serveiCommand"));
    }

    @Test
    public void getAmbCodiExistentMostraFormulariPreomplert() throws Exception {
        ServeiDto servei = ServeiDto.builder().codi("SERV1").descripcio("Servei 1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);
        when(serveiService.xsdFindByServei("SERV1")).thenReturn(List.of());
        when(serveiService.findServeisBus("SERV1")).thenReturn(List.of());
        when(serveiService.findEmisorAll()).thenReturn(List.of());
        when(serveiService.findClauPublicaAll()).thenReturn(List.of());
        when(serveiService.findClauPrivadaAll()).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("serveiForm", controller.get("SERV1", model));
        assertTrue(model.containsAttribute("serveiCommand"));
        assertTrue(model.containsAttribute("serveisBus"));
    }

    // ------------------------- save -------------------------

    @Test
    public void saveAmbErrorsTornaAlFormulari() throws Exception {
        when(serveiService.findEmisorAll()).thenReturn(List.of());
        when(serveiService.findClauPublicaAll()).thenReturn(List.of());
        when(serveiService.findClauPrivadaAll()).thenReturn(List.of());
        ServeiCommand command = ServeiCommand.builder().build();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("serveiForm", controller.save(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void saveCreacioAmbExit() throws Exception {
        ServeiCommand command = ServeiCommand.builder().codi("SERV1").descripcio("Servei 1").creacio(true).build();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../servei", controller.save(request, command, bindingResult, new ExtendedModelMap()));
        verify(serveiService).save(any());
    }

    @Test
    public void saveModificacioAmbExit() throws Exception {
        ServeiCommand command = ServeiCommand.builder().codi("SERV1").descripcio("Servei 1").creacio(false).build();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../servei", controller.save(request, command, bindingResult, new ExtendedModelMap()));
        verify(serveiService).save(any());
    }

    // ------------------------- enable / disable -------------------------

    @Test
    public void enableAmbExit() throws Exception {
        assertEquals("redirect:../../servei", controller.enable(request, "SERV1"));
        verify(serveiService).saveActiu("SERV1", true);
        verify(session).removeAttribute("ServeiHelper.serveis");
    }

    @Test
    public void enableAmbExcepcioMostraError() throws Exception {
        org.mockito.Mockito.doThrow(new RuntimeException("error")).when(serveiService).saveActiu("SERV1", true);

        assertEquals("redirect:../../servei", controller.enable(request, "SERV1"));
    }

    @Test
    public void disableAmbExit() throws Exception {
        assertEquals("redirect:../../servei", controller.disable(request, "SERV1"));
        verify(serveiService).saveActiu("SERV1", false);
    }

    @Test
    public void disableAmbExcepcioMostraError() throws Exception {
        org.mockito.Mockito.doThrow(new RuntimeException("error")).when(serveiService).saveActiu("SERV1", false);

        assertEquals("redirect:../../servei", controller.disable(request, "SERV1"));
    }

    // ------------------------- downloadAjuda -------------------------

    @Test
    public void downloadAjudaAmbExit() throws Exception {
        ServeiDto servei = ServeiDto.builder().fitxerAjudaNom("ajuda.pdf").fitxerAjudaMimeType("application/pdf")
                .fitxerAjudaContingut(new byte[]{1, 2}).build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream());

        assertNull(controller.downloadAjuda(request, response, "SERV1"));
        verify(response).setContentType("application/pdf");
    }

    @Test
    public void downloadAjudaAmbErrorRedirigeix() throws Exception {
        assertEquals("redirect:servei", controller.downloadAjuda(request, response, "SERV1"));
    }

    // ------------------------- delete -------------------------

    @Test
    public void deleteAmbExit() throws Exception {
        assertEquals("redirect:../../servei", controller.delete(request, "SERV1"));
        verify(serveiService).delete("SERV1");
    }

    @Test
    public void deleteAmbConsultesMostraError() throws Exception {
        org.mockito.Mockito.doThrow(new ServeiAmbConsultesException()).when(serveiService).delete("SERV1");

        assertEquals("redirect:../../servei", controller.delete(request, "SERV1"));
    }

    // ------------------------- serveiCamp -------------------------

    @Test
    public void serveiCampOmpleModel() throws Exception {
        ServeiDto servei = ServeiDto.builder().codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);
        when(serveiService.generarArbreDadesEspecifiques("SERV1")).thenReturn(new ArbreDto<>());
        ServeiCampDto camp = campDto(1L, null, "path1", ServeiCampDto.ServeiCampDtoTipus.TEXT);
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of(camp));
        when(serveiService.getArrelRespostaPath("SERV1")).thenReturn("arrel");
        when(serveiService.findServeiCampGrups("SERV1")).thenReturn(List.of());
        when(serveiService.serveiReglesFindAll("SERV1")).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("serveiCamp", controller.serveiCamp(request, "SERV1", model));
        assertTrue(model.containsAttribute("camps"));
        assertTrue(model.containsAttribute("campsAgrupats"));
    }

    // ------------------------- serveiCampAdd / Update / Delete / Ordre / Agrupar / Desagrupar -------------------------

    @Test
    public void serveiCampAddAmbErrorsRedirigeix() throws Exception {
        ServeiCampCommand command = new ServeiCampCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("redirect:../camp", controller.serveiCampAdd(request, "SERV1", command, bindingResult));
    }

    @Test
    public void serveiCampAddAmbExit() throws Exception {
        ServeiCampCommand command = new ServeiCampCommand();
        command.setServei("SERV1");
        command.setPath("path1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../camp", controller.serveiCampAdd(request, "SERV1", command, bindingResult));
        verify(serveiService).createServeiCamp("SERV1", "path1");
    }

    @Test
    public void serveiCampUpdateAmbErrorsRedirigeix() throws Exception {
        ServeiCampCommand command = new ServeiCampCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("redirect:../camp", controller.serveiCampUpdate(request, "SERV1", command, bindingResult));
    }

    @Test
    public void serveiCampUpdateAmbExit() throws Exception {
        ServeiCampCommand command = new ServeiCampCommand();
        command.setId(1L);
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        when(request.getParameterValues("descripcio-1")).thenReturn(new String[]{});

        assertEquals("redirect:../camp", controller.serveiCampUpdate(request, "SERV1", command, bindingResult));
        verify(serveiService).updateServeiCamp(any());
    }

    @Test
    public void serveiCampDeleteAmbExit() throws Exception {
        assertEquals("redirect:../../camp", controller.serveiCampDelete(request, "SERV1", 1L));
        verify(serveiService).deleteServeiCamp(1L);
    }

    @Test
    public void serveiCampOrdreAmbExit() throws Exception {
        assertEquals("ok", controller.serveiCampOrdre(request, "SERV1", 1L, 2));
        verify(serveiService).moveServeiCamp("SERV1", 1L, 2);
    }

    @Test
    public void serveiCampOrdreAmbExcepcioLlancaExcepcio() throws Exception {
        org.mockito.Mockito.doThrow(new RuntimeException("error")).when(serveiService).moveServeiCamp("SERV1", 1L, 2);

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> controller.serveiCampOrdre(request, "SERV1", 1L, 2));
    }

    @Test
    public void serveiCampAgruparAmbExit() throws Exception {
        assertEquals("redirect:../../../camp", controller.serveiCampAgrupar(request, "SERV1", 1L, 2L));
        verify(serveiService).agrupaServeiCamp(1L, 2L);
    }

    @Test
    public void serveiCampMarcarArrelRespostaAmbExit() {
        assertEquals("ok", controller.serveiCampMarcarArrelResposta(request, "SERV1", "path1"));
        verify(serveiService).marcarArrelResposta("SERV1", "path1");
    }

    @Test
    public void serveiCampDesmarcarArrelRespostaAmbExit() {
        assertEquals("ok", controller.serveiCampDesmarcarArrelResposta(request, "SERV1"));
        verify(serveiService).desmarcarArrelResposta("SERV1");
    }

    @Test
    public void serveiCampDesagruparAmbExit() throws Exception {
        assertEquals("redirect:../../../camp", controller.serveiCampDesagrupar(request, "SERV1", 1L));
        verify(serveiService).agrupaServeiCamp(1L, null);
    }

    // ------------------------- serveiPreview -------------------------

    @Test
    public void serveiPreviewOmpleModel() throws Exception {
        ServeiDto servei = ServeiDto.builder().codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);
        when(serveiService.generarArbreDadesEspecifiques("SERV1")).thenReturn(new ArbreDto<>());
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of());
        when(serveiService.findServeiCampGrups("SERV1")).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("serveiPreview", controller.serveiPreview(request, "SERV1", model));
        assertTrue(model.containsAttribute("campsDadesEspecifiques"));
    }

    // ------------------------- serveiCampGrup Add / Update / Delete / Up / Down -------------------------

    @Test
    public void serveiCampGrupAddAmbErrorsMostraAlerta() throws Exception {
        ServeiCampGrupCommand command = new ServeiCampGrupCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.rejectValue(null, "error", "missatge d'error");

        assertEquals("redirect:../camp", controller.serveiCampGrupAdd(request, "SERV1", command, bindingResult));
    }

    @Test
    public void serveiCampGrupAddAmbExit() throws Exception {
        ServeiCampGrupCommand command = new ServeiCampGrupCommand();
        command.setServei("SERV1");
        command.setNom("Grup 1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../camp", controller.serveiCampGrupAdd(request, "SERV1", command, bindingResult));
        verify(serveiService).createServeiCampGrup(any());
    }

    @Test
    public void serveiCampGrupUpdateAmbErrorsMostraAlerta() throws Exception {
        ServeiCampGrupCommand command = new ServeiCampGrupCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("redirect:../camp", controller.serveiCampGrupUpdate(request, "SERV1", command, bindingResult));
    }

    @Test
    public void serveiCampGrupUpdateAmbExit() throws Exception {
        ServeiCampGrupCommand command = new ServeiCampGrupCommand();
        command.setId(1L);
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../camp", controller.serveiCampGrupUpdate(request, "SERV1", command, bindingResult));
        verify(serveiService).updateServeiCampGrup(any());
    }

    @Test
    public void serveiCampGrupDeleteAmbExit() throws Exception {
        assertEquals("redirect:../../camp", controller.serveiCampGrupDelete(request, "SERV1", 1L));
        verify(serveiService).deleteServeiCampGrup(1L);
    }

    @Test
    public void serveiCampGrupUpAmbExit() throws Exception {
        assertEquals("redirect:../../camp", controller.serveiCampGrupUp(request, "SERV1", 1L));
        verify(serveiService).moveServeiCampGrup(1L, true);
    }

    @Test
    public void serveiCampGrupDownAmbExit() throws Exception {
        assertEquals("redirect:../../camp", controller.serveiCampGrupDown(request, "SERV1", 1L));
        verify(serveiService).moveServeiCampGrup(1L, false);
    }

    // ------------------------- serveiJustificant -------------------------

    @Test
    public void serveiJustificantOmpleModel() throws Exception {
        ServeiDto servei = ServeiDto.builder().codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);
        when(serveiService.generarArbreDadesEspecifiques("SERV1")).thenReturn(new ArbreDto<>());
        when(serveiService.findServeiJustificantCamps("SERV1")).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("serveiJustificant", controller.serveiJustificant(request, "SERV1", model));
        assertTrue(model.containsAttribute("traduccions"));
    }

    @Test
    public void serveiJustificantPostAmbErrorsTornaAlFormulari() throws Exception {
        ServeiDto servei = ServeiDto.builder().codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);
        when(serveiService.generarArbreDadesEspecifiques("SERV1")).thenReturn(new ArbreDto<>());
        when(serveiService.findServeiJustificantCamps("SERV1")).thenReturn(List.of());
        ServeiJustificantCampCommand command = new ServeiJustificantCampCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("serveiJustificant", controller.serveiJustificantPost(request, "SERV1", command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void serveiJustificantPostAmbExit() throws Exception {
        ServeiJustificantCampCommand command = new ServeiJustificantCampCommand();
        command.setServei("SERV1");
        command.setXpath("xpath1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:justificant", controller.serveiJustificantPost(request, "SERV1", command, bindingResult, new ExtendedModelMap()));
        verify(serveiService).addServeiJustificantCamp(any());
    }

    // ------------------------- xsd -------------------------

    @Test
    public void newXsdDelegaAXsdGet() throws Exception {
        ServeiDto servei = ServeiDto.builder().codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);

        Model model = new ExtendedModelMap();
        assertEquals("serveiXsd", controller.newXsd(request, "SERV1", model));
        assertTrue(model.containsAttribute("serveiXsdCommand"));
    }

    @Test
    public void xsdGetAmbIdNoAfegeixCommand() throws Exception {
        ServeiDto servei = ServeiDto.builder().codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);

        Model model = new ExtendedModelMap();
        assertEquals("serveiXsd", controller.xsdGet(request, "SERV1", 1L, model));
        assertTrue(!model.containsAttribute("serveiXsdCommand"));
    }

    @Test
    public void xsdDeleteAmbExit() throws Exception {
        controller.xsdDelete(request, XsdTipusEnumDto.PETICIO, "SERV1");

        verify(serveiService).xsdDelete("SERV1", XsdTipusEnumDto.PETICIO);
        verify(serveiService).updateVersio("SERV1");
    }

    @Test
    public void xsdDownloadEscriuElFitxer() throws Exception {
        FitxerDto fitxer = FitxerDto.builder().nom("fitxer.xsd").contingut(new byte[]{1, 2}).build();
        when(serveiService.xsdDescarregar("SERV1", XsdTipusEnumDto.PETICIO)).thenReturn(fitxer);
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream());

        assertNull(controller.xsdDownload(request, response, "SERV1", XsdTipusEnumDto.PETICIO));
        verify(response).setHeader("Content-Disposition", "attachment; filename=\"fitxer.xsd\"");
    }

    @Test
    public void xsdPostAmbErrorsRetornaJsonAmbErrors() throws Exception {
        ServeiXsdCommand command = new ServeiXsdCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.rejectValue("nomArxiu", "error", "invalid");

        String resposta = controller.xsdPost(request, "SERV1", command, bindingResult, new ExtendedModelMap());

        assertTrue(resposta.contains("\"error\": true"));
    }

    @Test
    public void xsdPostSenseErrorsCampsCreaNouXsd() throws Exception {
        when(serveiService.xsdFindByServei("SERV1")).thenReturn(List.of());
        ServeiXsdCommand command = new ServeiXsdCommand();
        command.setCodi("SERV1");
        command.setTipus(XsdTipusEnumDto.PETICIO);
        command.setContingut(new org.springframework.mock.web.MockMultipartFile("contingut", "f.xsd", "text/xml", "<xsd/>".getBytes()));
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        String resposta = controller.xsdPost(request, "SERV1", command, bindingResult, new ExtendedModelMap());

        assertEquals("{\"error\": false}", resposta);
        verify(serveiService).xsdCreate(eq("SERV1"), any(), any());
    }

    // ------------------------- redir (bus) -------------------------

    @Test
    public void redirGetNouDelegaAmbIdNull() throws Exception {
        ServeiDto servei = ServeiDto.builder().id(1L).codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);
        when(entitatService.findDisponiblesPerRedireccionsBus("SERV1")).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("serveiBus", controller.redirGet(request, "SERV1", model));
        assertTrue(model.asMap().values().stream().anyMatch(v -> v instanceof ServeiBusCommand));
    }

    @Test
    public void redirGetAmbIdCarregaBus() throws Exception {
        ServeiDto servei = ServeiDto.builder().id(1L).codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);
        when(entitatService.findDisponiblesPerRedireccionsBus("SERV1")).thenReturn(List.of());
        ServeiBusDto bus = new ServeiBusDto();
        bus.setId(1L);
        bus.setServei("SERV1");
        when(serveiService.findServeiBusById(1L)).thenReturn(bus);

        Model model = new ExtendedModelMap();
        assertEquals("serveiBus", controller.redirGet(request, "SERV1", 1L, model));
        assertTrue(model.asMap().values().stream().anyMatch(v -> v instanceof ServeiBusCommand));
    }

    @Test
    public void redirPostAmbErrorsTornaAlFormulari() throws Exception {
        ServeiDto servei = ServeiDto.builder().id(1L).codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);
        when(entitatService.findDisponiblesPerRedireccionsBus("SERV1")).thenReturn(List.of());
        ServeiBusCommand command = new ServeiBusCommand("SERV1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("serveiBus", controller.redirPost(request, "SERV1", command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void redirPostCreacioAmbExit() throws Exception {
        ServeiBusCommand command = new ServeiBusCommand("SERV1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        Model model = new ExtendedModelMap();
        assertEquals("serveiBus", controller.redirPost(request, "SERV1", command, bindingResult, model));
        verify(serveiService).createServeiBus(any());
        assertTrue(model.containsAttribute("reloadPage"));
    }

    @Test
    public void redirPostActualitzacioAmbExit() throws Exception {
        ServeiBusCommand command = new ServeiBusCommand("SERV1");
        command.setId(1L);
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("serveiBus", controller.redirPost(request, "SERV1", command, bindingResult, new ExtendedModelMap()));
        verify(serveiService).updateServeiBus(any());
    }

    @Test
    public void deleteBusAmbExit() throws Exception {
        assertEquals("redirect:../../../SERV1", controller.delete(request, "SERV1", 1L));
        verify(serveiService).deleteServeiBus(1L);
    }

    // ------------------------- regla -------------------------

    @Test
    public void reglaNewGetOmpleModel() throws Exception {
        ServeiDto servei = ServeiDto.builder().id(1L).codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);

        Model model = new ExtendedModelMap();
        assertEquals("serveiReglaForm", controller.reglaNewGet(request, "SERV1", model));
        assertTrue(model.containsAttribute("valors"));
    }

    @Test
    public void reglaNewPostDelegaAUpdatePostAmbCreacio() throws Exception {
        ServeiDto servei = ServeiDto.builder().id(1L).codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);
        ServeiReglaCommand command = ServeiReglaCommand.builder().nom("Regla 1").build();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../..", controller.reglaNewPost(request, "SERV1", command, bindingResult, new ExtendedModelMap()));
        verify(serveiService).serveiReglaCreate(eq("SERV1"), any());
    }

    @Test
    public void reglaUpdateGetOmpleModelAmbCamps() throws Exception {
        ServeiDto servei = ServeiDto.builder().id(1L).codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);
        ServeiReglaDto regla = ServeiReglaDto.builder().id(1L).nom("Regla 1").modificat(ModificatEnum.CAMPS).accio(AccioEnum.MOSTRAR).build();
        when(serveiService.serveiReglaFindById(1L)).thenReturn(regla);
        ServeiCampDto camp = campDto(null, "Camp 1", "path1", null);
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of(camp));

        Model model = new ExtendedModelMap();
        assertEquals("serveiReglaForm", controller.reglaUpdateGet(request, "SERV1", 1L, model));
        assertTrue(model.containsAttribute("valors"));
    }

    @Test
    public void reglaUpdatePostAmbErrorsTornaAlFormulari() throws Exception {
        ServeiDto servei = ServeiDto.builder().id(1L).codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);
        ServeiReglaCommand command = ServeiReglaCommand.builder().modificat(ModificatEnum.GRUPS).build();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");
        when(serveiService.findServeiCampGrups("SERV1")).thenReturn(List.of());

        assertEquals("serveiReglaForm", controller.reglaUpdatePost(request, "SERV1", 1L, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void reglaUpdatePostAmbExitActualitza() throws Exception {
        ServeiDto servei = ServeiDto.builder().id(1L).codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);
        ServeiReglaCommand command = ServeiReglaCommand.builder().nom("Regla 1").build();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../..", controller.reglaUpdatePost(request, "SERV1", 1L, command, bindingResult, new ExtendedModelMap()));
        verify(serveiService).serveiReglaUpdate(eq("SERV1"), any());
    }

    @Test
    public void reglaDeleteAmbExit() throws Exception {
        ServeiDto servei = ServeiDto.builder().id(1L).codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);

        assertEquals("redirect:/servei/SERV1/camp", controller.reglaDelete(request, "SERV1", 1L, new ExtendedModelMap()));
        verify(serveiService).serveiReglaDelete("SERV1", 1L);
    }

    @Test
    public void moureReglaDelegaAlServei() throws Exception {
        ServeiDto servei = ServeiDto.builder().id(1L).codi("SERV1").build();
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(servei);
        when(serveiService.serveiReglaMoure(1L, 2)).thenReturn(true);

        assertTrue(controller.moureRegla(request, "SERV1", 1L, 2, new ExtendedModelMap()));
    }

    @Test
    public void reglaGetCampsRetornaCodiValor() throws Exception {
        ServeiCampDto camp = campDto(null, "Camp 1", "path1", null);
        when(serveiService.findServeiCamps("SERV1")).thenReturn(List.of(camp));

        List<CodiValor> resposta = controller.reglaGetCamps(request, "SERV1", new ExtendedModelMap());

        assertEquals(1, resposta.size());
        assertEquals("Camp 1", resposta.get(0).getCodi());
    }

    @Test
    public void reglaGetGrupsRetornaCodiValorAmbFills() throws Exception {
        ServeiCampGrupDto fill = ServeiCampGrupDto.builder().id(2L).nom("Fill 1").build();
        ServeiCampGrupDto grup = ServeiCampGrupDto.builder().id(1L).nom("Grup 1").fills(List.of(fill)).build();
        when(serveiService.findServeiCampGrups("SERV1")).thenReturn(List.of(grup));

        List<CodiValor> resposta = controller.reglaGetGrups(request, "SERV1", new ExtendedModelMap());

        assertEquals(2, resposta.size());
    }

    // ------------------------- procediments -------------------------

    @Test
    public void procediemntsAgrupaPerEntitat() throws Exception {
        ProcedimentDto p1 = ProcedimentDto.builder().id(1L).entitatNom("Entitat 1").build();
        ProcedimentDto p2 = ProcedimentDto.builder().id(2L).entitatNom("Entitat 1").build();
        when(procedimentService.findAmbServeiCodi("SERV1")).thenReturn(List.of(p1, p2));

        Model model = new ExtendedModelMap();
        assertEquals("serveiProcedimentList", controller.procediemnts("SERV1", model));
        var procedimentsEntitat = (java.util.Map<String, List<ProcedimentDto>>) model.getAttribute("procedimentsEntitat");
        assertEquals(2, procedimentsEntitat.get("Entitat 1").size());
    }

    private static ServeiCampDto campDto(Long id, String etiqueta, String path, ServeiCampDto.ServeiCampDtoTipus tipus) {
        ServeiCampDto camp = new ServeiCampDto();
        camp.setId(id);
        camp.setEtiqueta(etiqueta);
        camp.setPath(path);
        camp.setTipus(tipus);
        return camp;
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
