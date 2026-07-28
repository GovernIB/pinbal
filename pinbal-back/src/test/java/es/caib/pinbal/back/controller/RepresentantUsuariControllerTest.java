package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.EntitatUsuariCommand;
import es.caib.pinbal.back.command.UsuariFiltreCommand;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.EntitatUsuariDto;
import es.caib.pinbal.logic.intf.dto.RolEnumDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.ProcedimentService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.UsuariService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.UsuariExternNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RepresentantUsuariControllerTest {

    private RepresentantUsuariController controller;
    private EntitatService entitatService;
    private ProcedimentService procedimentService;
    private ServeiService serveiService;
    private UsuariService usuariService;
    private HttpServletRequest request;
    private HttpSession session;

    @BeforeEach
    public void configurar() {
        controller = new RepresentantUsuariController();
        entitatService = mock(EntitatService.class);
        procedimentService = mock(ProcedimentService.class);
        serveiService = mock(ServeiService.class);
        usuariService = mock(UsuariService.class);
        ControllerTestSupport.setField(controller, "entitatService", entitatService);
        ControllerTestSupport.setField(controller, "procedimentService", procedimentService);
        ControllerTestSupport.setField(controller, "serveiService", serveiService);
        ControllerTestSupport.setField(controller, "usuariService", usuariService);
        controller.setMessageSource(ControllerTestSupport.mockMessageSourceEcoDeLaClau());
        request = ControllerTestSupport.mockRequest();
        session = request.getSession();
    }

    private EntitatDto entitatAmbUsuaris(EntitatUsuariDto... usuaris) {
        EntitatDto entitat = new EntitatDto();
        entitat.setId(1L);
        entitat.setUsuaris(new ArrayList<>(List.of(usuaris)));
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitat));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        when(entitatService.findById(1L)).thenReturn(entitat);
        return entitat;
    }

    private void representant() {
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);
    }

    private EntitatUsuariDto usuariRepresentant(String codi, boolean actiu) {
        UsuariDto usuari = new UsuariDto();
        usuari.setCodi(codi);
        usuari.setNom("Nom " + codi);
        return new EntitatUsuariDto(usuari, "Dept", false, true, false, false, false, actiu);
    }

    // ------------------------- get / post -------------------------

    @Test
    public void getSenseRepresentantRetornaNoAutoritzat() throws Exception {
        entitatAmbUsuaris();

        assertEquals("representantNoAutoritzat", controller.get(request, new ExtendedModelMap()));
    }

    @Test
    public void getSenseEntitatRedirigeix() throws Exception {
        representant();

        assertEquals("redirect:../index", controller.get(request, new ExtendedModelMap()));
    }

    @Test
    public void getAmbEntitatMostraLlistat() throws Exception {
        representant();
        entitatAmbUsuaris();

        Model model = new ExtendedModelMap();
        assertEquals("representantUsuaris", controller.get(request, model));
        assertTrue(model.containsAttribute("entitat"));
    }

    @Test
    public void postSenseEntitatRedirigeix() throws Exception {
        UsuariFiltreCommand command = new UsuariFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../index", controller.post(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void postSenseRepresentantRetornaNoAutoritzat() throws Exception {
        entitatAmbUsuaris();
        UsuariFiltreCommand command = new UsuariFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("representantNoAutoritzat", controller.post(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void postAmbErrorsTornaAlLlistat() throws Exception {
        representant();
        entitatAmbUsuaris();
        UsuariFiltreCommand command = new UsuariFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("entitatList", controller.post(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void postSenseErrorsGuardaFiltreIRedirigeix() throws Exception {
        representant();
        entitatAmbUsuaris();
        UsuariFiltreCommand command = new UsuariFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:usuari", controller.post(request, command, bindingResult, new ExtendedModelMap()));
        verify(session).setAttribute("EntitatUsuariController.session.filtre", command);
    }

    // ------------------------- datatable -------------------------

    @Test
    public void datatableSenseEntitatLlancaExcepcio() {
        assertThrows(EntitatNotFoundException.class, () -> controller.datatable(request, new ExtendedModelMap()));
    }

    @Test
    public void datatableAmbEntitatFiltraPerActiuIRetornaResposta() throws Exception {
        EntitatUsuariDto actiu = usuariRepresentant("U1", true);
        EntitatUsuariDto inactiu = usuariRepresentant("U2", false);
        entitatAmbUsuaris(actiu, inactiu);
        ControllerTestSupport.mockDatatableParams(request, "actiu", "departament");

        var resposta = controller.datatable(request, new ExtendedModelMap());

        assertEquals(1, resposta.getRecordsFiltered());
    }

    @Test
    public void datatableOrdenaPerCodi() throws Exception {
        EntitatUsuariDto u1 = usuariRepresentant("B", true);
        EntitatUsuariDto u2 = usuariRepresentant("A", true);
        entitatAmbUsuaris(u1, u2);
        when(request.getParameter("draw")).thenReturn("1");
        when(request.getParameter("start")).thenReturn("0");
        when(request.getParameter("length")).thenReturn("10");
        when(request.getParameter("order[0][column]")).thenReturn("0");
        when(request.getParameter("order[0][dir]")).thenReturn("asc");
        when(request.getParameter("columns[0][data]")).thenReturn("usuari.codi");

        UsuariFiltreCommand filtre = new UsuariFiltreCommand();
        filtre.setActiu(null);
        when(session.getAttribute("EntitatUsuariController.session.filtre")).thenReturn(filtre);

        var resposta = controller.datatable(request, new ExtendedModelMap());

        assertEquals(2, resposta.getRecordsFiltered());
    }

    // ------------------------- usuariGet / usuariPost -------------------------

    @Test
    public void usuariGetNouSenseEntitatRedirigeix() {
        assertEquals("redirect:usuari", controller.usuariGet(request, new ExtendedModelMap()));
    }

    @Test
    public void usuariGetNouAmbEntitat() {
        entitatAmbUsuaris();

        Model model = new ExtendedModelMap();
        assertEquals("representantUsuariForm", controller.usuariGet(request, model));
        assertTrue(model.containsAttribute("entitatUsuariCommand"));
    }

    @Test
    public void usuariGetExistentAmbCodiCarregaLesDades() {
        entitatAmbUsuaris();
        EntitatUsuariDto trobat = usuariRepresentant("U1", true);
        when(usuariService.getEntitatUsuari(1L, "U1")).thenReturn(trobat);

        Model model = new ExtendedModelMap();
        assertEquals("representantUsuariForm", controller.usuariGet(request, "U1", model));
        assertTrue(model.containsAttribute("entitatUsuariCommand"));
    }

    @Test
    public void usuariPostAmbErrorsMostraAlertaITornaAlFormulari() throws Exception {
        EntitatUsuariCommand command = new EntitatUsuariCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.rejectValue(null, "error", "missatge d'error");

        assertEquals("representantUsuariForm", controller.usuariPost(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void usuariPostSenseEntitatRetornaErrorModal() throws Exception {
        EntitatUsuariCommand command = new EntitatUsuariCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../usuari", controller.usuariPost(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void usuariPostSenseRepresentantRetornaErrorModal() throws Exception {
        entitatAmbUsuaris();
        EntitatUsuariCommand command = new EntitatUsuariCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../usuari", controller.usuariPost(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void usuariPostAmbExitActualitza() throws Exception {
        representant();
        entitatAmbUsuaris();
        EntitatUsuariCommand command = new EntitatUsuariCommand();
        command.setId(1L);
        command.setCodi("U1");
        command.setNom("Usuari 1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../usuari", controller.usuariPost(request, command, bindingResult, new ExtendedModelMap()));
        verify(usuariService).actualitzarDadesRepresentant(1L, "U1", null, null, false, false, false, false, true);
    }

    @Test
    public void usuariPostAmbUsuariExternInexistentRetornaErrorModal() throws Exception {
        representant();
        entitatAmbUsuaris();
        EntitatUsuariCommand command = new EntitatUsuariCommand();
        command.setCodi("U1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        org.mockito.Mockito.doThrow(new UsuariExternNotFoundException())
                .when(usuariService).actualitzarDadesRepresentant(null, "U1", null, null, false, false, false, false, true);

        assertEquals("redirect:../usuari", controller.usuariPost(request, command, bindingResult, new ExtendedModelMap()));
    }

    // ------------------------- permisGet -------------------------

    @Test
    public void permisGetSenseEntitatRedirigeix() throws Exception {
        assertEquals("redirect:../index", controller.permisGet(request, "U1", new ExtendedModelMap()));
    }

    @Test
    public void permisGetSenseRepresentantRetornaNoAutoritzat() throws Exception {
        entitatAmbUsuaris();

        assertEquals("representantNoAutoritzat", controller.permisGet(request, "U1", new ExtendedModelMap()));
    }

    @Test
    public void permisGetAmbEntitatOmpleModel() throws Exception {
        representant();
        entitatAmbUsuaris();
        when(usuariService.getDades("U1")).thenReturn(new UsuariDto());
        when(procedimentService.findAmbEntitat(1L)).thenReturn(List.of());
        when(serveiService.findPermesosAmbEntitatIUsuari(1L, "U1")).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("representantUsuariPermis", controller.permisGet(request, "U1", model));
        assertTrue(model.containsAttribute("permisos"));
    }

    // ------------------------- permisAtorgar / permisDenegar -------------------------

    @Test
    public void permisAtorgarSenseEntitatRedirigeix() throws Exception {
        assertEquals("redirect:../index", controller.permisAtorgar(request, "U1", 1L, "SERV1", new ExtendedModelMap()));
    }

    @Test
    public void permisAtorgarSenseRepresentantRetornaNoAutoritzat() throws Exception {
        entitatAmbUsuaris();

        assertEquals("representantNoAutoritzat", controller.permisAtorgar(request, "U1", 1L, "SERV1", new ExtendedModelMap()));
    }

    @Test
    public void permisAtorgarAmbDadesCompletesAtorga() throws Exception {
        representant();
        entitatAmbUsuaris();
        when(usuariService.getDades("U1")).thenReturn(new UsuariDto());
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(ServeiDto.builder().descripcio("Servei 1").build());

        assertEquals("redirect:../permis", controller.permisAtorgar(request, "U1", 1L, "SERV1", new ExtendedModelMap()));
        verify(procedimentService).serveiPermisAllow(1L, "SERV1", "U1");
    }

    @Test
    public void permisDenegarAmbDadesCompletesDenega() throws Exception {
        representant();
        entitatAmbUsuaris();
        when(usuariService.getDades("U1")).thenReturn(new UsuariDto());
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(ServeiDto.builder().descripcio("Servei 1").build());

        assertEquals("redirect:../permis", controller.permisDenegar(request, "U1", 1L, "SERV1", new ExtendedModelMap()));
        verify(procedimentService).serveiPermisDeny(1L, "SERV1", "U1");
    }

    @Test
    public void permisDenegarSenseEntitatRedirigeix() throws Exception {
        assertEquals("redirect:../index", controller.permisDenegar(request, "U1", 1L, "SERV1", new ExtendedModelMap()));
    }

    // ------------------------- permisAfegirSeleccionats / permisDenegarSeleccionats -------------------------

    @Test
    public void permisAfegirSeleccionatsSenseEntitatRetornaErrorModal() throws Exception {
        assertEquals("redirect:/index",
                controller.permisAfegirSeleccionats(request, "U1", "[]", new ExtendedModelMap()));
    }

    @Test
    public void permisAfegirSeleccionatsAmbExit() throws Exception {
        representant();
        entitatAmbUsuaris();
        when(usuariService.getDades("U1")).thenReturn(new UsuariDto());

        assertEquals("redirect:../../permis",
                controller.permisAfegirSeleccionats(request, "U1", "[]", new ExtendedModelMap()));
        verify(procedimentService).serveiPermisAllowSelected(eq("U1"), any(), eq(1L));
    }

    @Test
    public void permisDenegarSeleccionatsSenseEntitatRetornaError() throws Exception {
        assertEquals("redirect:/index",
                controller.permisDenegarSeleccionats(request, "U1", "[]", new ExtendedModelMap()));
    }

    @Test
    public void permisDenegarSeleccionatsAmbExit() throws Exception {
        representant();
        entitatAmbUsuaris();
        when(usuariService.getDades("U1")).thenReturn(new UsuariDto());

        assertEquals("redirect:../../permis",
                controller.permisDenegarSeleccionats(request, "U1", "[]", new ExtendedModelMap()));
        verify(procedimentService).serveiPermisDenySelected(eq("U1"), any(), eq(1L));
    }

    // ------------------------- permisDenegarTots -------------------------

    @Test
    public void permisDenegarTotsSenseEntitatRedirigeix() throws Exception {
        assertEquals("redirect:/index", controller.permisDenegarTots(request, "U1", new ExtendedModelMap()));
    }

    @Test
    public void permisDenegarTotsSenseRepresentantRetornaNoAutoritzat() throws Exception {
        entitatAmbUsuaris();

        assertEquals("representantNoAutoritzat", controller.permisDenegarTots(request, "U1", new ExtendedModelMap()));
    }

    @Test
    public void permisDenegarTotsAmbExit() throws Exception {
        representant();
        entitatAmbUsuaris();
        when(usuariService.getDades("U1")).thenReturn(new UsuariDto());

        assertEquals("redirect:../../permis", controller.permisDenegarTots(request, "U1", new ExtendedModelMap()));
        verify(procedimentService).serveiPermisDenyAll("U1", 1L);
    }

    // ------------------------- permisAfegirGet -------------------------

    @Test
    public void permisAfegirGetSenseEntitatRedirigeix() throws Exception {
        assertEquals("redirect:../index", controller.permisAfegirGet(request, "U1", new ExtendedModelMap()));
    }

    @Test
    public void permisAfegirGetSenseRepresentantRetornaNoAutoritzat() throws Exception {
        entitatAmbUsuaris();

        assertEquals("representantNoAutoritzat", controller.permisAfegirGet(request, "U1", new ExtendedModelMap()));
    }

    @Test
    public void permisAfegirGetAmbEntitatOmpleModel() throws Exception {
        representant();
        entitatAmbUsuaris();
        when(usuariService.getDades("U1")).thenReturn(new UsuariDto());
        when(procedimentService.findAmbEntitat(1L)).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("representantUsuariPermisForm", controller.permisAfegirGet(request, "U1", model));
        assertTrue(model.containsAttribute("procediments"));
    }

    // ------------------------- serveisPerProcediment -------------------------

    @Test
    public void serveisPerProcedimentSenseEntitatRetornaExcepcio() throws Exception {
        assertThrows(NullPointerException.class,
                () -> controller.serveisPerProcediment(request, "U1", 1L, new ExtendedModelMap()));
    }

    @Test
    public void serveisPerProcedimentSenseRepresentantRetornaNull() throws Exception {
        entitatAmbUsuaris();

        assertNull(controller.serveisPerProcediment(request, "U1", 1L, new ExtendedModelMap()));
    }

    @Test
    public void serveisPerProcedimentAmbRepresentantRetornaLlista() throws Exception {
        representant();
        entitatAmbUsuaris();
        when(procedimentService.serveiDisponibles("U1", 1L, 1L)).thenReturn(List.of());

        assertEquals(List.of(), controller.serveisPerProcediment(request, "U1", 1L, new ExtendedModelMap()));
    }
}
