package es.caib.pinbal.back.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthControllerTest {

    private AuthController controller;
    private HttpServletRequest request;
    private HttpSession session;

    @BeforeEach
    public void configurar() {
        controller = new AuthController();
        // Sense això, logout() prendria la branca Spring Boot (jbossHomeDir == null), que
        // delega en WebSecurityConfig.LOGOUT_URL i mai exerceix la lògica pròpia de JBoss que
        // aquesta classe de test verifica (invalidació de sessió + redirect a l'end_session_
        // endpoint de Keycloak).
        ControllerTestSupport.setField(controller, "jbossHomeDir", "/opt/jboss");
        request = ControllerTestSupport.mockRequest();
        session = request.getSession();
    }

    @Test
    public void logoutSenseOrigenInvalidaLaSessioIRedirigeixALArrelJsp() {
        assertEquals("redirect:/", controller.logout(request, null));
        verify(session).invalidate();
    }

    @Test
    public void logoutAmbOrigenReactInvalidaLaSessioIRedirigeixALArrelReact() {
        assertEquals("redirect:/reactapp/", controller.logout(request, "react"));
        verify(session).invalidate();
    }

    @Test
    public void logoutAmbAuthUrlConfiguratRedirigeixAEndSessionEndpointAmbClientId() {
        // Port refusat -> el document de descobriment OIDC falla ràpid i sense dependre de xarxa real,
        // de manera que el controlador cau al path per defecte de Keycloak com a fallback.
        ControllerTestSupport.setField(controller, "authUrl", "http://127.0.0.1:1/auth");
        ControllerTestSupport.setField(controller, "authRealm", "pinbal");
        ControllerTestSupport.setField(controller, "authClientId", "pinbal-back");
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("pinbal.example.org");
        when(request.getServerPort()).thenReturn(443);
        when(request.getContextPath()).thenReturn("/pinbalback");

        String redirect = controller.logout(request, null);

        assertTrue(redirect.startsWith("redirect:http://127.0.0.1:1/auth/realms/pinbal/protocol/openid-connect/logout?"));
        assertTrue(redirect.contains("post_logout_redirect_uri=https%3A%2F%2Fpinbal.example.org%2Fpinbalback%2F"));
        assertTrue(redirect.contains("client_id=pinbal-back"));
        assertTrue(!redirect.contains("id_token_hint"));
        verify(session).invalidate();
    }

    @Test
    public void logoutAmbAuthUrlConfiguratIOrigenReactRedirigeixAEndSessionEndpointAmbArrelReact() {
        ControllerTestSupport.setField(controller, "authUrl", "http://127.0.0.1:1/auth");
        ControllerTestSupport.setField(controller, "authRealm", "pinbal");
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("pinbal.example.org");
        when(request.getServerPort()).thenReturn(443);
        when(request.getContextPath()).thenReturn("/pinbalback");

        String redirect = controller.logout(request, "react");

        assertTrue(redirect.contains("post_logout_redirect_uri=https%3A%2F%2Fpinbal.example.org%2Fpinbalback%2Freactapp%2F"));
        verify(session).invalidate();
    }

}
