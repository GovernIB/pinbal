package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.ProcedimentCommand;
import es.caib.pinbal.back.command.ProcedimentFiltreCommand;
import es.caib.pinbal.back.command.ProcedimentServeiCommand;
import es.caib.pinbal.back.command.ProcedimentServeiMigrarCommand;
import es.caib.pinbal.back.command.ProcedimentServeiPermisFiltreCommand;
import es.caib.pinbal.back.command.ServeiFiltreCommand;
import es.caib.pinbal.back.command.UsuariPermisiCommand;
import es.caib.pinbal.logic.intf.dto.CodiValor;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.EntitatUsuariDto;
import es.caib.pinbal.logic.intf.dto.OrganGestorDto;
import es.caib.pinbal.logic.intf.dto.ProcedimentDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.OrganGestorService;
import es.caib.pinbal.logic.intf.service.ProcedimentService;
import es.caib.pinbal.logic.intf.service.PropertyService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.UsuariService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProcedimentControllerTest {

    private ProcedimentController controller;
    private ProcedimentService procedimentService;
    private EntitatService entitatService;
    private ServeiService serveiService;
    private PropertyService propertyService;
    private OrganGestorService organGestorService;
    private UsuariService usuariService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @BeforeEach
    public void configurar() {
        controller = new ProcedimentController();
        procedimentService = mock(ProcedimentService.class);
        entitatService = mock(EntitatService.class);
        serveiService = mock(ServeiService.class);
        propertyService = mock(PropertyService.class);
        organGestorService = mock(OrganGestorService.class);
        usuariService = mock(UsuariService.class);
        ControllerTestSupport.setField(controller, "procedimentService", procedimentService);
        ControllerTestSupport.setField(controller, "entitatService", entitatService);
        ControllerTestSupport.setField(controller, "serveiService", serveiService);
        ControllerTestSupport.setField(controller, "propertyService", propertyService);
        ControllerTestSupport.setField(controller, "organGestorService", organGestorService);
        ControllerTestSupport.setField(controller, "usuariService", usuariService);
        controller.setMessageSource(ControllerTestSupport.mockMessageSourceEcoDeLaClau());
        request = ControllerTestSupport.mockRequest();
        response = mock(HttpServletResponse.class);
        session = request.getSession();
    }

    private EntitatDto entitatRepresentant() {
        EntitatDto entitat = new EntitatDto();
        entitat.setId(1L);
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitat));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        when(entitatService.findById(1L)).thenReturn(entitat);
        return entitat;
    }

    // ------------------------- get / post (llistat) -------------------------

    @Test
    public void getSenseRepresentantRetornaNoAutoritzat() throws Exception {
        assertEquals("representantNoAutoritzat", controller.get(request, response, new ExtendedModelMap()));
    }

    @Test
    public void getSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../index", controller.get(request, response, new ExtendedModelMap()));
    }

    @Test
    public void getAmbEntitatMostraLlistat() throws Exception {
        entitatRepresentant();
        when(entitatService.getOrgansGestors(1L)).thenReturn(List.of());
        when(propertyService.get(any(), any())).thenReturn("false");

        Model model = new ExtendedModelMap();
        assertEquals("procedimentList", controller.get(request, response, model));
        assertTrue(model.containsAttribute("organsGestors"));
        assertTrue(model.containsAttribute("filtreActiu"));
    }

    @Test
    public void postSenseRepresentantRetornaNoAutoritzat() throws Exception {
        ProcedimentFiltreCommand command = new ProcedimentFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("representantNoAutoritzat", controller.post(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void postSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);
        ProcedimentFiltreCommand command = new ProcedimentFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../index", controller.post(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void postAmbErrorsTornaAlLlistat() throws Exception {
        entitatRepresentant();
        when(propertyService.get(any(), any())).thenReturn("false");
        ProcedimentFiltreCommand command = new ProcedimentFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("procedimentList", controller.post(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void postSenseErrorsGuardaFiltreIRedirigeix() throws Exception {
        entitatRepresentant();
        ProcedimentFiltreCommand command = new ProcedimentFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:procediment", controller.post(request, command, bindingResult, new ExtendedModelMap()));
        verify(session).setAttribute("ProcedimentController.session.filtre", command);
    }

    // ------------------------- datatable -------------------------

    @Test
    public void datatableSenseEntitatLlancaExcepcio() {
        assertThrows(EntitatNotFoundException.class, () -> controller.datatable(request, new ExtendedModelMap()));
    }

    @Test
    public void datatableAmbEntitatRetornaResposta() throws Exception {
        entitatRepresentant();
        ControllerTestSupport.mockDatatableParams(request, "codi", "nom", "departament", "codiSia");
        when(procedimentService.findAmbFiltrePaginat(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new ProcedimentDto())));

        var resposta = controller.datatable(request, new ExtendedModelMap());

        assertEquals(1, resposta.getRecordsFiltered());
    }

    // ------------------------- get(new) / get(procedimentId) -------------------------

    @Test
    public void getNouDelegaAGetAmbIdNull() throws Exception {
        assertEquals("representantNoAutoritzat", controller.get(request, new ExtendedModelMap()));
    }

    @Test
    public void getAmbIdSenseRepresentantRetornaNoAutoritzat() throws Exception {
        assertEquals("representantNoAutoritzat", controller.get(request, 1L, new ExtendedModelMap()));
    }

    @Test
    public void getAmbIdSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../index", controller.get(request, 1L, new ExtendedModelMap()));
    }

    @Test
    public void getAmbIdNullMostraFormulariNou() throws Exception {
        entitatRepresentant();
        when(procedimentService.findAmbEntitatPerOrigen(1L)).thenReturn(List.of());
        when(organGestorService.findActivesByEntitat(1L)).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("procedimentForm", controller.get(request, (Long) null, model));
        assertTrue(model.containsAttribute("procedimentCommand"));
    }

    @Test
    public void getAmbProcedimentExistentSenseCodiSiaMostraFormulari() throws Exception {
        entitatRepresentant();
        ProcedimentDto procediment = ProcedimentDto.builder().id(1L).codi("P1").nom("Procediment 1").build();
        when(procedimentService.findAmbEntitatPerOrigen(1L)).thenReturn(List.of());
        when(procedimentService.findById(1L)).thenReturn(procediment);
        when(organGestorService.findActivesByEntitat(1L)).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("procedimentForm", controller.get(request, 1L, model));
        assertTrue(model.containsAttribute("procedimentCommand"));
    }

    @Test
    public void getAmbProcedimentExistentAmbCodiSiaMostraFillsIOrigens() throws Exception {
        entitatRepresentant();
        OrganGestorDto organ = OrganGestorDto.builder().id(5L).build();
        ProcedimentDto procediment = ProcedimentDto.builder().id(1L).codi("P1").nom("Procediment 1")
                .codiSia("SIA1").organGestor(organ).build();
        List<CodiValor> procedimentsOrigen = new java.util.ArrayList<>(
                List.of(CodiValor.builder().codi("SIA1").valor("v").build(), CodiValor.builder().codi("SIA2").valor("v2").build()));
        List<CodiValor> procedimentsFills = new java.util.ArrayList<>(
                List.of(CodiValor.builder().codi("SIA1").valor("v").build()));
        when(procedimentService.findAmbEntitatPerOrigen(1L)).thenReturn(procedimentsOrigen);
        when(procedimentService.findById(1L)).thenReturn(procediment);
        when(procedimentService.findAmbEntitatPerFills(1L, "SIA1")).thenReturn(procedimentsFills);
        when(procedimentService.findCodiSiaFills(1L, "SIA1")).thenReturn(List.of("SIAF1"));
        when(organGestorService.findItem(5L)).thenReturn(organ);
        when(organGestorService.findActivesByEntitat(1L)).thenReturn(new java.util.ArrayList<>());

        Model model = new ExtendedModelMap();
        assertEquals("procedimentForm", controller.get(request, 1L, model));
        assertTrue(model.containsAttribute("procedimentsFills"));
    }

    // ------------------------- cloneGet -------------------------

    @Test
    public void cloneGetSenseRepresentantRetornaNoAutoritzat() throws Exception {
        assertEquals("representantNoAutoritzat", controller.cloneGet(request, 1L, new ExtendedModelMap()));
    }

    @Test
    public void cloneGetSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../index", controller.cloneGet(request, 1L, new ExtendedModelMap()));
    }

    @Test
    public void cloneGetAmbEntitatMostraFormulari() throws Exception {
        entitatRepresentant();
        ProcedimentDto procediment = ProcedimentDto.builder().id(1L).codi("P1").nom("Procediment 1").codiSia("SIA1").build();
        when(procedimentService.findById(1L)).thenReturn(procediment);
        when(organGestorService.findActivesByEntitat(1L)).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("procedimentForm", controller.cloneGet(request, 1L, model));
        assertTrue(model.containsAttribute("procedimentCommand"));
    }

    // ------------------------- save -------------------------

    @Test
    public void saveSenseRepresentantRetornaNoAutoritzat() throws Exception {
        ProcedimentCommand command = ProcedimentCommand.builder().build();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("representantNoAutoritzat", controller.save(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void saveSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);
        ProcedimentCommand command = ProcedimentCommand.builder().build();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../index", controller.save(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void saveAmbErrorsTornaAlFormulari() throws Exception {
        entitatRepresentant();
        when(organGestorService.findActivesByEntitat(1L)).thenReturn(List.of());
        ProcedimentCommand command = ProcedimentCommand.builder().build();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("procedimentForm", controller.save(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void saveAmbIdActualitza() throws Exception {
        entitatRepresentant();
        ProcedimentCommand command = ProcedimentCommand.builder().id(1L).build();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../procediment", controller.save(request, command, bindingResult, new ExtendedModelMap()));
        verify(procedimentService).update(any());
    }

    @Test
    public void saveSenseIdCrea() throws Exception {
        entitatRepresentant();
        ProcedimentCommand command = ProcedimentCommand.builder().build();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../procediment", controller.save(request, command, bindingResult, new ExtendedModelMap()));
        verify(procedimentService).create(any());
    }

    // ------------------------- delete / enable / disable -------------------------

    @Test
    public void deleteSenseRepresentantRetornaNoAutoritzat() throws Exception {
        assertEquals("representantNoAutoritzat", controller.delete(request, 1L));
    }

    @Test
    public void deleteSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../../index", controller.delete(request, 1L));
    }

    @Test
    public void deleteAmbEntitatEsborra() throws Exception {
        entitatRepresentant();

        assertEquals("redirect:../../procediment", controller.delete(request, 1L));
        verify(procedimentService).delete(1L);
    }

    @Test
    public void enableAmbEntitatActiva() throws Exception {
        entitatRepresentant();

        assertEquals("redirect:../../procediment", controller.enable(request, 1L));
        verify(procedimentService).updateActiu(1L, true);
    }

    @Test
    public void enableSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../../index", controller.enable(request, 1L));
    }

    @Test
    public void disableAmbEntitatDesactiva() throws Exception {
        entitatRepresentant();

        assertEquals("redirect:../../procediment", controller.disable(request, 1L));
        verify(procedimentService).updateActiu(1L, false);
    }

    @Test
    public void disableSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../../index", controller.disable(request, 1L));
    }

    // ------------------------- servei datatable / servei / serveiPost -------------------------

    @Test
    public void datatableServeiSenseRepresentantLlancaExcepcio() {
        Exception ex = assertThrows(Exception.class, () -> controller.datatable(request, 1L, new ExtendedModelMap()));
        assertEquals("Representant no autoritzat", ex.getMessage());
    }

    @Test
    public void datatableServeiSenseEntitatLlancaExcepcio() {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        Exception ex = assertThrows(Exception.class, () -> controller.datatable(request, 1L, new ExtendedModelMap()));
        assertEquals("Entitat actual incorrecte", ex.getMessage());
    }

    @Test
    public void datatableServeiSenseProcedimentLlancaExcepcio() {
        entitatRepresentant();

        Exception ex = assertThrows(Exception.class, () -> controller.datatable(request, 1L, new ExtendedModelMap()));
        assertEquals("Incorrect procediment id", ex.getMessage());
    }

    @Test
    public void datatableServeiAmbProcedimentRetornaResposta() throws Exception {
        entitatRepresentant();
        ControllerTestSupport.mockDatatableParams(request, "codi", "descripcio", "procedimentCodi");
        ProcedimentDto procediment = ProcedimentDto.builder().id(1L).build();
        when(procedimentService.findById(1L)).thenReturn(procediment);
        when(serveiService.findAmbFiltrePaginat(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new ServeiDto())));

        var resposta = controller.datatable(request, 1L, new ExtendedModelMap());

        assertEquals(1, resposta.getRecordsFiltered());
    }

    @Test
    public void serveiSenseRepresentantRetornaNoAutoritzat() throws Exception {
        assertEquals("representantNoAutoritzat", controller.servei(request, 1L, new ExtendedModelMap()));
    }

    @Test
    public void serveiSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../../index", controller.servei(request, 1L, new ExtendedModelMap()));
    }

    @Test
    public void serveiAmbProcedimentInexistentRedirigeix() throws Exception {
        entitatRepresentant();

        assertEquals("redirect:../../procediment", controller.servei(request, 1L, new ExtendedModelMap()));
    }

    @Test
    public void serveiAmbProcedimentMostraLlistat() throws Exception {
        entitatRepresentant();
        when(procedimentService.findById(1L)).thenReturn(ProcedimentDto.builder().id(1L).build());

        Model model = new ExtendedModelMap();
        assertEquals("procedimentServeis", controller.servei(request, 1L, model));
        assertTrue(model.containsAttribute("procediment"));
    }

    @Test
    public void serveiPostSenseRepresentantRetornaNoAutoritzat() throws Exception {
        ServeiFiltreCommand command = new ServeiFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("representantNoAutoritzat", controller.serveiPost(request, 1L, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void serveiPostSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);
        ServeiFiltreCommand command = new ServeiFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../../../index", controller.serveiPost(request, 1L, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void serveiPostAmbProcedimentInexistentRedirigeix() throws Exception {
        entitatRepresentant();
        ServeiFiltreCommand command = new ServeiFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../../procediment", controller.serveiPost(request, 1L, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void serveiPostAmbErrorsTornaAlLlistat() throws Exception {
        entitatRepresentant();
        when(procedimentService.findById(1L)).thenReturn(ProcedimentDto.builder().id(1L).build());
        ServeiFiltreCommand command = new ServeiFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("procedimentServeis", controller.serveiPost(request, 1L, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void serveiPostSenseErrorsGuardaFiltre() throws Exception {
        entitatRepresentant();
        when(procedimentService.findById(1L)).thenReturn(ProcedimentDto.builder().id(1L).build());
        ServeiFiltreCommand command = new ServeiFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("procedimentServeis", controller.serveiPost(request, 1L, command, bindingResult, new ExtendedModelMap()));
        verify(session).setAttribute("ServeiController.session.filtre.procediment", command);
    }

    // ------------------------- procedimentServeiPost (new) / save -------------------------

    @Test
    public void procedimentServeiPostSenseRepresentantRetornaNoAutoritzat() throws Exception {
        assertEquals("representantNoAutoritzat", controller.procedimentServeiPost(request, 1L, new ExtendedModelMap()));
    }

    @Test
    public void procedimentServeiPostSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:index", controller.procedimentServeiPost(request, 1L, new ExtendedModelMap()));
    }

    @Test
    public void procedimentServeiPostAmbEntitatMostraFormulari() throws Exception {
        entitatRepresentant();
        when(procedimentService.serveisDisponiblesPerProcediment(1L)).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("procedimentServeiForm", controller.procedimentServeiPost(request, 1L, model));
        assertTrue(model.containsAttribute("procedimentServeiCommand"));
    }

    @Test
    public void procedimentServeiSaveSenseRepresentantRetornaNoAutoritzat() throws Exception {
        ProcedimentServeiCommand command = new ProcedimentServeiCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("representantNoAutoritzat", controller.procedimentServeiSave(request, 1L, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void procedimentServeiSaveAmbErrorsTornaAlFormulari() throws Exception {
        entitatRepresentant();
        when(procedimentService.serveisDisponiblesPerProcediment(1L)).thenReturn(List.of());
        ProcedimentServeiCommand command = new ProcedimentServeiCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("procedimentServeiForm", controller.procedimentServeiSave(request, 1L, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void procedimentServeiSaveSenseEntitatRetornaErrorModal() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);
        ProcedimentServeiCommand command = new ProcedimentServeiCommand();
        command.setServeiCodi("SERV1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../../../../../index", controller.procedimentServeiSave(request, 1L, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void procedimentServeiSaveAmbProcedimentInexistentRetornaErrorModal() throws Exception {
        entitatRepresentant();
        ProcedimentServeiCommand command = new ProcedimentServeiCommand();
        command.setServeiCodi("SERV1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../../servei", controller.procedimentServeiSave(request, 1L, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void procedimentServeiSaveAmbExitActiva() throws Exception {
        entitatRepresentant();
        when(procedimentService.findById(1L)).thenReturn(ProcedimentDto.builder().id(1L).build());
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(ServeiDto.builder().descripcio("Servei 1").build());
        ProcedimentServeiCommand command = new ProcedimentServeiCommand();
        command.setServeiCodi("SERV1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../../servei", controller.procedimentServeiSave(request, 1L, command, bindingResult, new ExtendedModelMap()));
        verify(procedimentService).serveiEnable(1L, "SERV1");
    }

    // ------------------------- getServei / getServeis -------------------------

    @Test
    public void getServeiDelegaAlServei() throws Exception {
        when(serveiService.getServeiDtoByCodi("SERV1")).thenReturn(ServeiDto.builder().codi("SERV1").build());

        assertEquals("SERV1", controller.getServei(request, "SERV1").getCodi());
    }

    @Test
    public void getServeisDecodificaTextIDelegaAlServei() throws Exception {
        when(request.getRequestURI()).thenReturn("/pinbal/procediment/serveis/text");
        when(serveiService.getServeis("text")).thenReturn(List.of());

        assertEquals(List.of(), controller.getServeis(request, "text"));
    }

    // ------------------------- serveiEnable / serveiDisable -------------------------

    @Test
    public void serveiEnableSenseRepresentantRetornaNoAutoritzat() throws Exception {
        assertEquals("representantNoAutoritzat", controller.serveiEnable(request, 1L, "SERV1", new ExtendedModelMap()));
    }

    @Test
    public void serveiEnableSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../../../../index", controller.serveiEnable(request, 1L, "SERV1", new ExtendedModelMap()));
    }

    @Test
    public void serveiEnableAmbProcedimentInexistentRedirigeix() throws Exception {
        entitatRepresentant();

        assertEquals("redirect:../../../../procediment", controller.serveiEnable(request, 1L, "SERV1", new ExtendedModelMap()));
    }

    @Test
    public void serveiEnableAmbExit() throws Exception {
        entitatRepresentant();
        when(procedimentService.findById(1L)).thenReturn(ProcedimentDto.builder().id(1L).build());
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(ServeiDto.builder().descripcio("Servei 1").build());

        assertEquals("redirect:../../servei", controller.serveiEnable(request, 1L, "SERV1", new ExtendedModelMap()));
        verify(procedimentService).serveiEnable(1L, "SERV1");
    }

    @Test
    public void serveiDisableAmbExit() throws Exception {
        entitatRepresentant();
        when(procedimentService.findById(1L)).thenReturn(ProcedimentDto.builder().id(1L).build());
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(ServeiDto.builder().descripcio("Servei 1").build());

        assertEquals("redirect:../../servei", controller.serveiDisable(request, 1L, "SERV1", new ExtendedModelMap()));
        verify(procedimentService).serveiDisable(1L, "SERV1");
    }

    @Test
    public void serveiDisableAmbProcedimentInexistentRedirigeix() throws Exception {
        entitatRepresentant();

        assertEquals("redirect:../../../../entitat", controller.serveiDisable(request, 1L, "SERV1", new ExtendedModelMap()));
    }

    @Test
    public void serveiDisableSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../../../../index", controller.serveiDisable(request, 1L, "SERV1", new ExtendedModelMap()));
    }

    // ------------------------- serveiPermisGet / serveiPermisPost -------------------------

    @Test
    public void serveiPermisGetSenseRepresentantRetornaNoAutoritzat() throws Exception {
        assertEquals("representantNoAutoritzat", controller.serveiPermisGet(request, 1L, "SERV1", new ExtendedModelMap()));
    }

    @Test
    public void serveiPermisGetSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../../../../index", controller.serveiPermisGet(request, 1L, "SERV1", new ExtendedModelMap()));
    }

    @Test
    public void serveiPermisGetAmbProcedimentInexistentRedirigeix() throws Exception {
        entitatRepresentant();

        assertEquals("redirect:../../../../procediment", controller.serveiPermisGet(request, 1L, "SERV1", new ExtendedModelMap()));
    }

    @Test
    public void serveiPermisGetAmbEntitatMostraLlistat() throws Exception {
        entitatRepresentant();
        when(procedimentService.findById(1L)).thenReturn(ProcedimentDto.builder().id(1L).build());
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(ServeiDto.builder().build());

        assertEquals("procedimentServeiPermisos", controller.serveiPermisGet(request, 1L, "SERV1", new ExtendedModelMap()));
    }

    @Test
    public void serveiPermisPostAmbErrorsMostraLlistat() throws Exception {
        entitatRepresentant();
        when(procedimentService.findById(1L)).thenReturn(ProcedimentDto.builder().id(1L).build());
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(ServeiDto.builder().build());
        ProcedimentServeiPermisFiltreCommand command = new ProcedimentServeiPermisFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("procedimentServeiPermisos", controller.serveiPermisPost(request, 1L, "SERV1", command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void serveiPermisPostSenseErrorsGuardaFiltre() throws Exception {
        entitatRepresentant();
        when(procedimentService.findById(1L)).thenReturn(ProcedimentDto.builder().id(1L).build());
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(ServeiDto.builder().build());
        ProcedimentServeiPermisFiltreCommand command = new ProcedimentServeiPermisFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("procedimentServeiPermisos", controller.serveiPermisPost(request, 1L, "SERV1", command, bindingResult, new ExtendedModelMap()));
        verify(session).setAttribute("ProcedimentController.session.filtre.permis", command);
    }

    // ------------------------- datatableServeiPermis -------------------------

    @Test
    public void datatableServeiPermisSenseRepresentantLlancaExcepcio() {
        Exception ex = assertThrows(Exception.class, () -> controller.datatableServeiPermis(request, 1L, "SERV1", new ExtendedModelMap()));
        assertEquals("Representant no autoritzat", ex.getMessage());
    }

    @Test
    public void datatableServeiPermisSenseEntitatLlancaExcepcio() {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertThrows(EntitatNotFoundException.class, () -> controller.datatableServeiPermis(request, 1L, "SERV1", new ExtendedModelMap()));
    }

    @Test
    public void datatableServeiPermisSenseProcedimentLlancaExcepcio() {
        entitatRepresentant();

        assertThrows(es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException.class,
                () -> controller.datatableServeiPermis(request, 1L, "SERV1", new ExtendedModelMap()));
    }

    @Test
    public void datatableServeiPermisAmbProcedimentRetornaResposta() throws Exception {
        entitatRepresentant();
        ControllerTestSupport.mockDatatableParams(request, "actiu", "departament", "auditor", "representant");
        when(procedimentService.findById(1L)).thenReturn(ProcedimentDto.builder().id(1L).build());
        when(procedimentService.findUsuarisAmbPermisPerServei(any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new EntitatUsuariDto(new UsuariDto(), "Dept", false, true, false, false, false, true))));

        var resposta = controller.datatableServeiPermis(request, 1L, "SERV1", new ExtendedModelMap());

        assertEquals(1, resposta.getRecordsFiltered());
    }

    // ------------------------- serveiPermisAllow / serveiPermisDeny -------------------------

    private EntitatDto entitatAmbUsuari() {
        EntitatDto entitat = entitatRepresentant();
        UsuariDto usuari = new UsuariDto();
        usuari.setCodi("U1");
        usuari.setNom("Usuari U1");
        EntitatUsuariDto entitatUsuari = new EntitatUsuariDto(usuari, "Dept", false, true, false, false, false, true);
        entitat.setUsuaris(new java.util.ArrayList<>(List.of(entitatUsuari)));
        return entitat;
    }

    @Test
    public void serveiPermisAllowSenseRepresentantRetornaNoAutoritzat() throws Exception {
        assertEquals("representantNoAutoritzat", controller.serveiPermisAllow(request, 1L, "SERV1", "U1", new ExtendedModelMap()));
    }

    @Test
    public void serveiPermisAllowSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../../../../index", controller.serveiPermisAllow(request, 1L, "SERV1", "U1", new ExtendedModelMap()));
    }

    @Test
    public void serveiPermisAllowAmbProcedimentInexistentRedirigeix() throws Exception {
        entitatRepresentant();

        assertEquals("redirect:../../../../procediment", controller.serveiPermisAllow(request, 1L, "SERV1", "U1", new ExtendedModelMap()));
    }

    @Test
    public void serveiPermisAllowAmbExit() throws Exception {
        entitatAmbUsuari();
        when(procedimentService.findById(1L)).thenReturn(ProcedimentDto.builder().id(1L).build());
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(ServeiDto.builder().descripcio("Servei 1").build());

        assertEquals("redirect:../../permis", controller.serveiPermisAllow(request, 1L, "SERV1", "U1", new ExtendedModelMap()));
        verify(procedimentService).serveiPermisAllow(1L, "SERV1", "U1");
    }

    @Test
    public void serveiPermisDenyAmbExit() throws Exception {
        entitatAmbUsuari();
        when(procedimentService.findById(1L)).thenReturn(ProcedimentDto.builder().id(1L).build());
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(ServeiDto.builder().descripcio("Servei 1").build());

        assertEquals("redirect:../../permis", controller.serveiPermisDeny(request, 1L, "SERV1", "U1", new ExtendedModelMap()));
        verify(procedimentService).serveiPermisDeny(1L, "SERV1", "U1");
    }

    @Test
    public void serveiPermisDenySenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:../../../../../index", controller.serveiPermisDeny(request, 1L, "SERV1", "U1", new ExtendedModelMap()));
    }

    @Test
    public void serveiPermisDenyAmbProcedimentInexistentRedirigeix() throws Exception {
        entitatRepresentant();

        assertEquals("redirect:../../../../procediment", controller.serveiPermisDeny(request, 1L, "SERV1", "U1", new ExtendedModelMap()));
    }

    // ------------------------- serveiPermisPost (new) / getProcedimentServeiPermis (save) -------------------------

    @Test
    public void serveiPermisPostNewSenseRepresentantRetornaNoAutoritzat() throws Exception {
        assertEquals("representantNoAutoritzat", controller.serveiPermisPost(request, 1L, "SERV1", new ExtendedModelMap()));
    }

    @Test
    public void serveiPermisPostNewSenseEntitatRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertEquals("redirect:index", controller.serveiPermisPost(request, 1L, "SERV1", new ExtendedModelMap()));
    }

    @Test
    public void serveiPermisPostNewAmbEntitatMostraFormulari() throws Exception {
        entitatRepresentant();

        Model model = new ExtendedModelMap();
        assertEquals("procedimentServeiPermis", controller.serveiPermisPost(request, 1L, "SERV1", model));
        assertTrue(model.containsAttribute("usuariPermisiCommand"));
    }

    @Test
    public void getProcedimentServeiPermisSenseRepresentantRetornaNoAutoritzat() throws Exception {
        UsuariPermisiCommand command = new UsuariPermisiCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("representantNoAutoritzat", controller.getProcedimentServeiPermis(request, 1L, "SERV1", command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void getProcedimentServeiPermisAmbErrorsTornaAlFormulari() throws Exception {
        entitatRepresentant();
        UsuariPermisiCommand command = new UsuariPermisiCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("procedimentServeiPermis", controller.getProcedimentServeiPermis(request, 1L, "SERV1", command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void getProcedimentServeiPermisAmbExitAtorga() throws Exception {
        entitatRepresentant();
        when(procedimentService.findById(1L)).thenReturn(ProcedimentDto.builder().id(1L).build());
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(ServeiDto.builder().descripcio("Servei 1").build());
        UsuariPermisiCommand command = new UsuariPermisiCommand();
        command.setUsuariCodi("U1");
        command.setUsuariNom("Usuari 1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../../permis", controller.getProcedimentServeiPermis(request, 1L, "SERV1", command, bindingResult, new ExtendedModelMap()));
        verify(procedimentService).serveiPermisAllow(1L, "SERV1", "U1");
    }

    @Test
    public void getProcedimentServeiPermisAmbProcedimentInexistentRetornaErrorModal() throws Exception {
        entitatRepresentant();
        UsuariPermisiCommand command = new UsuariPermisiCommand();
        command.setUsuariCodi("U1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../../permis", controller.getProcedimentServeiPermis(request, 1L, "SERV1", command, bindingResult, new ExtendedModelMap()));
    }

    // ------------------------- putProcedimentCodi -------------------------

    @Test
    public void putProcedimentCodiSenseRepresentantRetornaNoAutoritzat() throws Exception {
        assertEquals("representantNoAutoritzat", controller.putProcedimentCodi(request, response, 1L, "SERV1", "PC1", new ExtendedModelMap()));
    }

    @Test
    public void putProcedimentCodiSenseEntitatRetornaFalse() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertEquals("false", controller.putProcedimentCodi(request, response, 1L, "SERV1", "PC1", new ExtendedModelMap()));
    }

    @Test
    public void putProcedimentCodiAmbProcedimentInexistentRetornaFalse() throws Exception {
        entitatRepresentant();

        assertEquals("false", controller.putProcedimentCodi(request, response, 1L, "SERV1", "PC1", new ExtendedModelMap()));
    }

    @Test
    public void putProcedimentCodiAmbExitRetornaTrue() throws Exception {
        entitatRepresentant();
        when(procedimentService.findById(1L)).thenReturn(ProcedimentDto.builder().id(1L).build());

        assertEquals("true", controller.putProcedimentCodi(request, response, 1L, "SERV1", "PC1", new ExtendedModelMap()));
        verify(procedimentService).putProcedimentCodi(1L, "SERV1", "PC1");
    }

    // ------------------------- serveiMigrarGet / serveiMigrarPost -------------------------

    @Test
    public void serveiMigrarGetOmpleModel() throws Exception {
        entitatRepresentant();
        ProcedimentDto procediment = ProcedimentDto.builder().id(1L).build();
        when(procedimentService.findById(1L)).thenReturn(procediment);
        when(serveiService.findAmbEntitat(1L)).thenReturn(new java.util.ArrayList<>(
                List.of(ServeiDto.builder().codi("SERV1").build(), ServeiDto.builder().codi("SERV2").build())));

        Model model = new ExtendedModelMap();
        assertEquals("procedimentServeiMigrar", controller.serveiMigrarGet(request, 1L, "SERV1", model));
        assertTrue(model.containsAttribute("serveis"));
        List<ServeiDto> serveis = (List<ServeiDto>) model.getAttribute("serveis");
        assertEquals(1, serveis.size());
    }

    @Test
    public void serveiMigrarPostAmbErrorsTornaAlFormulari() throws Exception {
        entitatRepresentant();
        when(procedimentService.findById(1L)).thenReturn(ProcedimentDto.builder().id(1L).build());
        when(serveiService.findAmbEntitat(1L)).thenReturn(new java.util.ArrayList<>());
        ProcedimentServeiMigrarCommand command = new ProcedimentServeiMigrarCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("procedimentServeiMigrar", controller.serveiMigrarPost(request, 1L, "SERV1", command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void serveiMigrarPostAmbExit() throws Exception {
        ProcedimentServeiMigrarCommand command = new ProcedimentServeiMigrarCommand();
        command.setServeiCodiDesti("SERV2");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../../permis", controller.serveiMigrarPost(request, 1L, "SERV1", command, bindingResult, new ExtendedModelMap()));
        verify(procedimentService).migrarProcedimentServei(1L, "SERV1", "SERV2");
    }

    @Test
    public void serveiMigrarPostAmbExcepcioRetornaErrorModal() throws Exception {
        ProcedimentServeiMigrarCommand command = new ProcedimentServeiMigrarCommand();
        command.setServeiCodiDesti("SERV2");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        org.mockito.Mockito.doThrow(new RuntimeException("error migracio"))
                .when(procedimentService).migrarProcedimentServei(1L, "SERV1", "SERV2");

        assertEquals("redirect:index", controller.serveiMigrarPost(request, 1L, "SERV1", command, bindingResult, new ExtendedModelMap()));
    }
}
