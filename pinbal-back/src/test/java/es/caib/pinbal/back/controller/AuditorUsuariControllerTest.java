package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.EntitatUsuariCommand;
import es.caib.pinbal.back.command.UsuariFiltreCommand;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.EntitatUsuariDto;
import es.caib.pinbal.logic.intf.dto.RolEnumDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.EntitatService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuditorUsuariControllerTest {

    private AuditorUsuariController controller;
    private EntitatService entitatService;
    private UsuariService usuariService;
    private HttpServletRequest request;
    private HttpSession session;

    @BeforeEach
    public void configurar() {
        controller = new AuditorUsuariController();
        entitatService = mock(EntitatService.class);
        usuariService = mock(UsuariService.class);
        ControllerTestSupport.setField(controller, "entitatService", entitatService);
        ControllerTestSupport.setField(controller, "usuariService", usuariService);
        controller.setMessageSource(ControllerTestSupport.mockMessageSourceEcoDeLaClau());
        request = ControllerTestSupport.mockRequest();
        session = request.getSession();
    }

    private EntitatDto entitatAmbUsuaris(EntitatUsuariDto... usuaris) {
        EntitatDto entitat = new EntitatDto();
        entitat.setId(1L);
        List<EntitatUsuariDto> llista = new ArrayList<>(List.of(usuaris));
        entitat.setUsuaris(llista);
        return entitat;
    }

    private EntitatUsuariDto usuariAuditor(String codi, boolean auditor) {
        UsuariDto usuari = new UsuariDto();
        usuari.setCodi(codi);
        usuari.setNom("Nom " + codi);
        return new EntitatUsuariDto(usuari, "Dept", false, false, false, auditor, false, true);
    }

    // ------------------------- get -------------------------

    @Test
    public void getSenseRolAuditorRetornaNoAutoritzat() throws Exception {
        assertEquals("auditorNoAutoritzat", controller.get(request, new ExtendedModelMap()));
    }

    @Test
    public void getAmbRolAuditorIEntitatMostraLlistat() throws Exception {
        when(session.getAttribute("EntitatHelper.entitat.actual.auditor")).thenReturn(Boolean.TRUE);
        EntitatDto entitat = entitatAmbUsuaris();
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitat));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        when(entitatService.findById(1L)).thenReturn(entitat);

        Model model = new ExtendedModelMap();
        assertEquals("auditorUsuaris", controller.get(request, model));
        assertTrue(model.containsAttribute("entitat"));
    }

    // ------------------------- post -------------------------

    @Test
    public void postSenseEntitatRedirigeixAIndex() throws Exception {
        BindingResult bindingResult = new BeanPropertyBindingResult(new UsuariFiltreCommand(), "command");

        assertEquals("redirect:../index", controller.post(request, new UsuariFiltreCommand(), bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void postAmbErrorsDeBindingTornaAlLlistat() throws Exception {
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitatAmbUsuaris()));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        when(session.getAttribute("EntitatHelper.entitat.actual.auditor")).thenReturn(Boolean.TRUE);
        UsuariFiltreCommand command = new UsuariFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("entitatList", controller.post(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void postSenseErrorsGuardaElFiltreIRedirigeix() throws Exception {
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitatAmbUsuaris()));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        when(session.getAttribute("EntitatHelper.entitat.actual.auditor")).thenReturn(Boolean.TRUE);
        UsuariFiltreCommand command = new UsuariFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:usuari", controller.post(request, command, bindingResult, new ExtendedModelMap()));
        verify(session).setAttribute("AuditorUsuariController.session.filtre", command);
    }

    // ------------------------- datatable -------------------------

    @Test
    public void datatableSenseEntitatLlancaExcepcio() {
        assertThrowsEntitatNotFound(() -> controller.datatable(request, new ExtendedModelMap()));
    }

    @Test
    public void datatableFiltraPerRolAuditor() throws Exception {
        EntitatUsuariDto auditor = usuariAuditor("U1", true);
        EntitatUsuariDto noAuditor = usuariAuditor("U2", false);
        EntitatDto entitat = entitatAmbUsuaris(auditor, noAuditor);
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitat));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        when(entitatService.findById(1L)).thenReturn(entitatAmbUsuaris(auditor, noAuditor));
        ControllerTestSupport.mockDatatableParams(request, "actiu", "departament");

        UsuariFiltreCommand filtre = new UsuariFiltreCommand();
        filtre.setRol(RolEnumDto.AUDITOR);
        when(session.getAttribute("AuditorUsuariController.session.filtre")).thenReturn(filtre);

        var response = controller.datatable(request, new ExtendedModelMap());

        assertNotNull(response);
        assertEquals(1, response.getRecordsFiltered());
    }

    // ------------------------- usuariGet / usuariPost -------------------------

    @Test
    public void usuariGetSenseEntitatRedirigeix() {
        assertEquals("redirect:usuari", controller.usuariGet(request, null, new ExtendedModelMap()));
    }

    @Test
    public void usuariGetNouAmbEntitat() {
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitatAmbUsuaris()));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);

        assertEquals("auditorUsuariForm", controller.usuariGet(request, null, new ExtendedModelMap()));
    }

    @Test
    public void usuariGetExistentAmbCodiCarregaLesDades() {
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitatAmbUsuaris()));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        EntitatUsuariDto trobat = usuariAuditor("U1", true);
        when(usuariService.getEntitatUsuari(1L, "U1")).thenReturn(trobat);

        Model model = new ExtendedModelMap();
        assertEquals("auditorUsuariForm", controller.usuariGet(request, "U1", model));
    }

    @Test
    public void usuariPostAmbErrorsTornaAlFormulari() throws Exception {
        EntitatUsuariCommand command = new EntitatUsuariCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.rejectValue(null, "error", "missatge");

        assertEquals("auditorUsuariForm", controller.usuariPost(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void usuariPostSenseEntitatRetornaErrorModal() throws Exception {
        EntitatUsuariCommand command = new EntitatUsuariCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../usuari", controller.usuariPost(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void usuariPostSenseRolAuditorRetornaErrorModal() throws Exception {
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitatAmbUsuaris()));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        EntitatUsuariCommand command = new EntitatUsuariCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../usuari", controller.usuariPost(request, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void usuariPostAmbExitActualitzaIRetornaExit() throws Exception {
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitatAmbUsuaris()));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        when(session.getAttribute("EntitatHelper.entitat.actual.auditor")).thenReturn(Boolean.TRUE);
        EntitatUsuariCommand command = new EntitatUsuariCommand();
        command.setCodi("U1");
        command.setNom("Usuari 1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../usuari", controller.usuariPost(request, command, bindingResult, new ExtendedModelMap()));
        verify(usuariService).actualitzarDadesAuditor(1L, "U1", null, false, false);
    }

    @Test
    public void usuariPostAmbUsuariExternInexistentRetornaError() throws Exception {
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitatAmbUsuaris()));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        when(session.getAttribute("EntitatHelper.entitat.actual.auditor")).thenReturn(Boolean.TRUE);
        EntitatUsuariCommand command = new EntitatUsuariCommand();
        command.setCodi("U1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        org.mockito.Mockito.doThrow(new UsuariExternNotFoundException())
                .when(usuariService).actualitzarDadesAuditor(1L, "U1", null, false, false);

        assertEquals("redirect:../usuari", controller.usuariPost(request, command, bindingResult, new ExtendedModelMap()));
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private void assertThrowsEntitatNotFound(ThrowingRunnable runnable) {
        try {
            runnable.run();
            throw new AssertionError("S'esperava EntitatNotFoundException");
        } catch (EntitatNotFoundException expected) {
            // esperat
        } catch (Exception ex) {
            throw new AssertionError("Excepció inesperada", ex);
        }
    }
}
