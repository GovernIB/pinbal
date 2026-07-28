package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.UsuariCodiCommand;
import es.caib.pinbal.back.command.UsuariCommand;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.ProcedimentService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.UsuariService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UsuariControllerTest {

    private UsuariController controller;
    private UsuariService usuariService;
    private EntitatService entitatService;
    private ProcedimentService procedimentService;
    private ServeiService serveiService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @BeforeEach
    public void configurar() {
        controller = new UsuariController();
        usuariService = mock(UsuariService.class);
        entitatService = mock(EntitatService.class);
        procedimentService = mock(ProcedimentService.class);
        serveiService = mock(ServeiService.class);
        ControllerTestSupport.setField(controller, "usuariService", usuariService);
        ControllerTestSupport.setField(controller, "entitatService", entitatService);
        ControllerTestSupport.setField(controller, "procedimentService", procedimentService);
        ControllerTestSupport.setField(controller, "serveiService", serveiService);
        controller.setMessageSource(ControllerTestSupport.mockMessageSourceEcoDeLaClau());
        request = ControllerTestSupport.mockRequest();
        response = mock(HttpServletResponse.class);
        session = request.getSession();
    }

    @AfterEach
    public void netejarSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void rolActual(String rol) {
        when(session.getAttribute("RolHelper.rol.actual")).thenReturn(rol);
    }

    private void entitatActual() {
        EntitatDto entitat = new EntitatDto();
        entitat.setId(1L);
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitat));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        when(entitatService.findById(1L)).thenReturn(entitat);
    }

    private void stubEmplenaModelUsuariNormal() throws Exception {
        entitatActual();
        when(procedimentService.findAmbEntitat(1L)).thenReturn(List.of());
        when(serveiService.findAmbEntitat(1L)).thenReturn(List.of());
    }

    // ------------------------- logout -------------------------

    @Test
    public void logoutSobreescriuLesCookiesIRedirigeix() {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "abc")});
        when(request.getContextPath()).thenReturn("/pinbal");

        assertEquals("redirect:/", controller.logout(request, response));
        verify(response).addCookie(any());
    }

    // ------------------------- getConfiguracio / save -------------------------

    @Test
    public void getConfiguracioOmpleModel() throws Exception {
        UsuariDto usuari = new UsuariDto();
        usuari.setEntitatId(1L);
        when(usuariService.getUsuariActual()).thenReturn(usuari);
        stubEmplenaModelUsuariNormal();

        Model model = new ExtendedModelMap();
        assertEquals("usuariForm", controller.getConfiguracio(request, model));
        assertTrue(model.containsAttribute("usuariCommand"));
        assertTrue(model.containsAttribute("procediments"));
    }

    @Test
    public void getConfiguracioAmbRolAdministradorOmpleEntitatsActives() throws Exception {
        UsuariDto usuari = new UsuariDto();
        when(usuariService.getUsuariActual()).thenReturn(usuari);
        rolActual("PBL_ADMIN");
        when(entitatService.findActives()).thenReturn(List.of());
        when(procedimentService.findAll()).thenReturn(List.of());
        when(serveiService.findActius()).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        assertEquals("usuariForm", controller.getConfiguracio(request, model));
        assertTrue(model.containsAttribute("entitats"));
    }

    @Test
    public void saveAmbErrorsTornaAlFormulari() throws Exception {
        stubEmplenaModelUsuariNormal();
        UsuariCommand command = new UsuariCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("usuariForm", controller.save(request, response, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void saveAmbRolAdministradorActualitzaEntitat() throws Exception {
        rolActual("PBL_ADMIN");
        UsuariDto usuari = new UsuariDto();
        when(usuariService.getUsuariActual()).thenReturn(usuari);
        UsuariCommand command = new UsuariCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:/", controller.save(request, response, command, bindingResult, new ExtendedModelMap()));
        verify(usuariService).updateUsuariActual(any(), org.mockito.ArgumentMatchers.eq(true));
        verify(session).removeAttribute("UsuariHelper.dades.usuari.actual");
    }

    @Test
    public void saveSenseCanvisNoActualitzaEntitat() throws Exception {
        UsuariDto usuari = new UsuariDto();
        usuari.setProcedimentId(5L);
        usuari.setServeiCodi("SERV1");
        when(usuariService.getUsuariActual()).thenReturn(usuari);
        UsuariCommand command = new UsuariCommand();
        command.setProcedimentId(5L);
        command.setServeiCodi("SERV1");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:/", controller.save(request, response, command, bindingResult, new ExtendedModelMap()));
        verify(usuariService).updateUsuariActual(any(), org.mockito.ArgumentMatchers.eq(false));
    }

    @Test
    public void saveAmbCanviDeProcedimentActualitzaEntitat() throws Exception {
        entitatActual();
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(null);
        UsuariDto usuari = new UsuariDto();
        usuari.setProcedimentId(5L);
        when(usuariService.getUsuariActual()).thenReturn(usuari);
        UsuariCommand command = new UsuariCommand();
        command.setProcedimentId(6L);
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("redirect:/", controller.save(request, response, command, bindingResult, new ExtendedModelMap()));
        verify(usuariService).updateUsuariActual(any(), org.mockito.ArgumentMatchers.eq(true));
        assertEquals(Long.valueOf(1L), command.getEntitatId());
    }

    // ------------------------- getNumElementsPaginaDefecte -------------------------

    @Test
    public void getNumElementsPaginaDefecteDelegaAlServei() {
        when(usuariService.getNumElementsPaginaDefecte()).thenReturn(25);

        assertEquals(Integer.valueOf(25), controller.getNumElementsPaginaDefecte(request, new ExtendedModelMap()));
    }

    // ------------------------- username (canvi codi) -------------------------

    @Test
    public void getCanviCodiOmpleModel() {
        Model model = new ExtendedModelMap();
        assertEquals("usuariCodiForm", controller.getCanviCodi(request, model));
        assertTrue(model.containsAttribute("usuariCodiCommand"));
        assertTrue(model.containsAttribute("idiomaEnumOptions"));
    }

    @Test
    public void setCanviCodiAmbErrorsTornaAlFormulari() {
        UsuariCodiCommand command = new UsuariCodiCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("usuariCodiForm", controller.setCanviCodi(request, response, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void setCanviCodiAmbExit() {
        UsuariCodiCommand command = new UsuariCodiCommand();
        command.setCodiAntic("U1");
        command.setCodiNou("U2");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        assertEquals("usuariCodiForm", controller.setCanviCodi(request, response, command, bindingResult, new ExtendedModelMap()));
        verify(usuariService).updateUsuariCodi("U1", "U2", null, null, null, null);
    }

    @Test
    public void setCanviCodiAmbExcepcioMostraError() {
        UsuariCodiCommand command = new UsuariCodiCommand();
        command.setCodiAntic("U1");
        command.setCodiNou("U2");
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        org.mockito.Mockito.doThrow(new RuntimeException("error")).when(usuariService)
                .updateUsuariCodi("U1", "U2", null, null, null, null);

        assertEquals("usuariCodiForm", controller.setCanviCodi(request, response, command, bindingResult, new ExtendedModelMap()));
    }

    @Test
    public void getCanviCodisMostraVista() {
        assertEquals("usuarisCanviCodi", controller.getCanviCodis(request, new ExtendedModelMap()));
    }

    // ------------------------- validaCanviCodis / setCanviCodis -------------------------

    @Test
    public void validaCanviCodisSenseAutenticacio() {
        UsuariDto antic = new UsuariDto();
        when(usuariService.getDades("U1")).thenReturn(antic);
        when(usuariService.getDades("U2")).thenReturn(null);

        var resposta = controller.validaCanviCodis(request, "U1", "U2");

        assertTrue(!resposta.isUsuariActual());
        assertTrue(resposta.isUsuariAnticExists());
        assertTrue(!resposta.isUsuariNouExists());
    }

    @Test
    public void validaCanviCodisAmbUsuariActualAutenticat() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("U1", "pwd"));
        when(usuariService.getDades("U1")).thenReturn(new UsuariDto());
        when(usuariService.getDades("U2")).thenReturn(new UsuariDto());

        var resposta = controller.validaCanviCodis(request, "U1", "U2");

        assertTrue(resposta.isUsuariActual());
        assertTrue(resposta.isUsuariAnticExists());
        assertTrue(resposta.isUsuariNouExists());
    }

    @Test
    public void setCanviCodisAmbExit() {
        when(usuariService.updateUsuariCodi("U1", "U2")).thenReturn(3L);

        var resposta = controller.setCanviCodis(request, "U1", "U2");

        assertEquals(UsuariController.ResultatEstatEnum.OK, resposta.getEstat());
        assertEquals(Long.valueOf(3L), resposta.getRegistresModificats());
    }

    @Test
    public void setCanviCodisAmbExcepcioRetornaError() {
        when(usuariService.updateUsuariCodi("U1", "U2")).thenThrow(new RuntimeException("error canvi"));

        var resposta = controller.setCanviCodis(request, "U1", "U2");

        assertEquals(UsuariController.ResultatEstatEnum.ERROR, resposta.getEstat());
        assertTrue(resposta.getErrorMessage().contains("error canvi"));
    }
}
