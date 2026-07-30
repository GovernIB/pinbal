package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.EntitatUsuariCommand;
import es.caib.pinbal.back.command.UsuariFiltreCommand;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.EntitatUsuariDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.UsuariService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.NotFoundException;
import es.caib.pinbal.logic.intf.service.exception.UsuariExternNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EntitatUsuariControllerTest {

    private EntitatUsuariController controller;
    private EntitatService entitatService;
    private UsuariService usuariService;
    private HttpServletRequest request;
    private HttpSession session;

    @BeforeEach
    public void configurar() {
        controller = new EntitatUsuariController();
        entitatService = mock(EntitatService.class);
        usuariService = mock(UsuariService.class);
        ControllerTestSupport.setField(controller, "entitatService", entitatService);
        ControllerTestSupport.setField(controller, "usuariService", usuariService);
        controller.setMessageSource(ControllerTestSupport.mockMessageSourceEcoDeLaClau());
        request = ControllerTestSupport.mockRequest();
        session = request.getSession();
    }

    private EntitatDto entitatExistent() {
        EntitatDto entitat = new EntitatDto();
        entitat.setId(1L);
        when(entitatService.findById(1L)).thenReturn(entitat);
        return entitat;
    }

    // ------------------------- usuariGet (llistat) / usuariPost (filtre) -------------------------

    @Test
    public void usuariGetLlistatAmbEntitatInexistentRedirigeix() throws Exception {
        assertEquals("redirect:../../entitat", controller.usuariGet(request, 1L, new ExtendedModelMap()));
    }

    @Test
    public void usuariGetLlistatAmbEntitatMostraLlistat() throws Exception {
        entitatExistent();

        Model model = new ExtendedModelMap();
        assertEquals("entitatUsuaris", controller.usuariGet(request, 1L, model));
        assertTrue(model.containsAttribute("entitat"));
    }

    @Test
    public void usuariPostAmbEntitatInexistentRedirigeix() throws Exception {
        UsuariFiltreCommand command = new UsuariFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../../entitat", controller.usuariPost(request, 1L, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void usuariPostAmbErrorsTornaAlLlistat() throws Exception {
        entitatExistent();
        UsuariFiltreCommand command = new UsuariFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("entitatList", controller.usuariPost(request, 1L, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void usuariPostSenseErrorsGuardaFiltreIRedirigeix() throws Exception {
        entitatExistent();
        UsuariFiltreCommand command = new UsuariFiltreCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:usuari", controller.usuariPost(request, 1L, command, bindingResult, new ExtendedModelMap()));
        verify(session).setAttribute("EntitatUsuariController.session.filtre", command);
    }

    // ------------------------- datatable -------------------------

    @Test
    public void datatableAmbEntitatInexistentLlancaExcepcio() {
        assertThrows(EntitatNotFoundException.class, () -> controller.datatable(request, 1L, new ExtendedModelMap()));
    }

    @Test
    public void datatableAmbEntitatRetornaResposta() throws Exception {
        entitatExistent();
        ControllerTestSupport.mockDatatableParams(request, "actiu", "departament");
        when(usuariService.findAmbFiltrePaginat(
                org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()))
                .thenReturn(new PageImpl<>(List.of(new EntitatUsuariDto(new UsuariDto(), "Dept", false, false, false, false, false, true))));

        var resposta = controller.datatable(request, 1L, new ExtendedModelMap());

        assertEquals(1, resposta.getRecordsFiltered());
    }

    // ------------------------- usuariNew / usuariGet (formulari) -------------------------

    @Test
    public void usuariNewDelegaAUsuariGetAmbCodiNull() {
        entitatExistent();

        Model model = new ExtendedModelMap();
        assertEquals("entitatUsuariForm", controller.usuariNew(request, 1L, model));
        assertTrue(model.containsAttribute("entitatUsuariCommand"));
    }

    @Test
    public void usuariGetFormulariAmbEntitatInexistentRedirigeix() {
        assertEquals("redirect:usuari", controller.usuariGet(request, 1L, "U1", new ExtendedModelMap()));
    }

    @Test
    public void usuariGetFormulariNouAmbCodiBlancCreaCommandNou() {
        entitatExistent();

        Model model = new ExtendedModelMap();
        assertEquals("entitatUsuariForm", controller.usuariGet(request, 1L, "", model));
        assertTrue(model.containsAttribute("entitatUsuariCommand"));
    }

    @Test
    public void usuariGetFormulariExistentAmbCodiCarregaLesDades() {
        entitatExistent();
        UsuariDto usuari = new UsuariDto();
        usuari.setCodi("U1");
        EntitatUsuariDto trobat = new EntitatUsuariDto(usuari, "Dept", false, false, false, false, false, true);
        when(usuariService.getEntitatUsuari(1L, "U1")).thenReturn(trobat);

        Model model = new ExtendedModelMap();
        assertEquals("entitatUsuariForm", controller.usuariGet(request, 1L, "U1", model));
        assertTrue(model.containsAttribute("entitatUsuariCommand"));
    }

    // ------------------------- usuariPost (save) -------------------------

    @Test
    public void usuariPostSaveAmbErrorsMostraAlertaITornaAlFormulari() throws Exception {
        EntitatUsuariCommand command = new EntitatUsuariCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.rejectValue(null, "error", "missatge d'error");

        assertEquals("entitatUsuariForm", controller.usuariPost(request, 1L, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void usuariPostSaveSenseEntitatRetornaErrorModal() throws Exception {
        EntitatUsuariCommand command = new EntitatUsuariCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../usuari", controller.usuariPost(request, 1L, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void usuariPostSaveAmbExitActualitza() throws Exception {
        entitatExistent();
        EntitatUsuariCommand command = new EntitatUsuariCommand();
        command.setId(1L);
        command.setCodi("U1");
        command.setNom("Usuari 1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:../usuari", controller.usuariPost(request, 1L, command, bindingResult, new ExtendedModelMap()));
        verify(usuariService).actualitzarDadesAdmin(1L, "U1", null, null, false, false, false, false, false, true);
    }

    @Test
    public void usuariPostSaveAmbUsuariExternInexistentRetornaErrorModal() throws Exception {
        entitatExistent();
        EntitatUsuariCommand command = new EntitatUsuariCommand();
        command.setCodi("U1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        org.mockito.Mockito.doThrow(new UsuariExternNotFoundException())
                .when(usuariService).actualitzarDadesAdmin(null, "U1", null, null, false, false, false, false, false, true);

        assertEquals("redirect:../usuari", controller.usuariPost(request, 1L, command, bindingResult, new ExtendedModelMap()));
    }

    // ------------------------- usuariPrincipal -------------------------

    @Test
    public void usuariPrincipalAmbEntitatInexistentLlancaExcepcio() {
        assertThrows(NotFoundException.class, () -> controller.usuariPrincipal(request, 1L, "U1", new ExtendedModelMap()));
    }

    @Test
    public void usuariPrincipalMarcaProtegit() throws Exception {
        entitatExistent();
        when(usuariService.establirPrincipal(1L, "U1")).thenReturn(true);

        assertEquals("OK", controller.usuariPrincipal(request, 1L, "U1", new ExtendedModelMap()));
    }

    @Test
    public void usuariPrincipalDesmarca() throws Exception {
        entitatExistent();
        when(usuariService.establirPrincipal(1L, "U1")).thenReturn(false);

        assertEquals("OK", controller.usuariPrincipal(request, 1L, "U1", new ExtendedModelMap()));
    }

    // ------------------------- usuariActivar -------------------------

    @Test
    public void usuariActivarAmbEntitatInexistentLlancaExcepcio() {
        assertThrows(NotFoundException.class, () -> controller.usuariActivar(request, 1L, "U1", new ExtendedModelMap()));
    }

    @Test
    public void usuariActivarActiva() throws Exception {
        entitatExistent();
        when(usuariService.canviActiu(1L, "U1")).thenReturn(true);

        assertEquals("OK", controller.usuariActivar(request, 1L, "U1", new ExtendedModelMap()));
    }

    @Test
    public void usuariActivarDesactiva() throws Exception {
        entitatExistent();
        when(usuariService.canviActiu(1L, "U1")).thenReturn(false);

        assertEquals("OK", controller.usuariActivar(request, 1L, "U1", new ExtendedModelMap()));
    }
}
