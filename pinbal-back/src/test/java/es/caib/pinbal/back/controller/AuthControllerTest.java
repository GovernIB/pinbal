package es.caib.pinbal.back.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.IDToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
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
    public void logoutAmbAuthUrlConfiguratRedirigeixAEndSessionEndpointAmbIdTokenHintIClientIdDeLAzp() {
        // Port refusat -> el document de descobriment OIDC falla ràpid i sense dependre de xarxa real,
        // de manera que el controlador cau al path per defecte de Keycloak com a fallback.
        ControllerTestSupport.setField(controller, "authUrl", "http://127.0.0.1:1/auth");
        ControllerTestSupport.setField(controller, "authRealm", "pinbal");
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("pinbal.example.org");
        when(request.getServerPort()).thenReturn(443);
        when(request.getContextPath()).thenReturn("/pinbalback");
        // El "client_id" ha de sortir de l'"azp" del propi id_token (no d'una propietat de
        // configuració a part), perquè mai pugui quedar desincronitzat amb el client real amb
        // què s'ha autenticat l'usuari.
        KeycloakSecurityContext keycloakSecurityContext = mock(KeycloakSecurityContext.class);
        IDToken idToken = mock(IDToken.class);
        when(keycloakSecurityContext.getIdTokenString()).thenReturn("id-token-jwt");
        when(keycloakSecurityContext.getIdToken()).thenReturn(idToken);
        when(idToken.getIssuedFor()).thenReturn("pinbal-back");
        when(request.getAttribute(KeycloakSecurityContext.class.getName())).thenReturn(keycloakSecurityContext);

        String redirect = controller.logout(request, null);

        assertTrue(redirect.startsWith("redirect:http://127.0.0.1:1/auth/realms/pinbal/protocol/openid-connect/logout?"));
        assertTrue(redirect.contains("post_logout_redirect_uri=https%3A%2F%2Fpinbal.example.org%2Fpinbalback%2F"));
        assertTrue(redirect.contains("id_token_hint=id-token-jwt"));
        assertTrue(redirect.contains("client_id=pinbal-back"));
        verify(session).invalidate();
    }

    @Test
    public void logoutAmbIssuerDelTokenDiferentDeLAuthUrlConfiguratUsaLIssuerDelToken() {
        // Reprodueix el bug de preproducció: "es.caib.pinbal.auth.url"/"es.caib.pinbal.auth.realm" apunten a
        // un realm ("realm-configurat" a "http://127.0.0.1:1/auth-configurat") diferent del que ha emès
        // realment l'id_token ("http://127.0.0.1:2/auth-token/realms/realm-token", vist al seu claim "iss").
        // El redirect ha d'anar SEMPRE a l'"issuer" del token, no al configurat, perquè és l'únic realm on
        // Keycloak/Soffid té la sessió SSO activa -- altrament respon "Session not active" (id_token_hint
        // d'un realm, end_session_endpoint d'un altre).
        ControllerTestSupport.setField(controller, "authUrl", "http://127.0.0.1:1/auth-configurat");
        ControllerTestSupport.setField(controller, "authRealm", "realm-configurat");
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("pinbal.example.org");
        when(request.getServerPort()).thenReturn(443);
        when(request.getContextPath()).thenReturn("/pinbalback");
        KeycloakSecurityContext keycloakSecurityContext = mock(KeycloakSecurityContext.class);
        IDToken idToken = mock(IDToken.class);
        when(keycloakSecurityContext.getIdTokenString()).thenReturn("id-token-jwt");
        when(keycloakSecurityContext.getIdToken()).thenReturn(idToken);
        when(idToken.getIssuedFor()).thenReturn("pinbal-back");
        when(idToken.getIssuer()).thenReturn("http://127.0.0.1:2/auth-token/realms/realm-token");
        when(request.getAttribute(KeycloakSecurityContext.class.getName())).thenReturn(keycloakSecurityContext);

        String redirect = controller.logout(request, null);

        assertTrue(redirect.startsWith("redirect:http://127.0.0.1:2/auth-token/realms/realm-token/protocol/openid-connect/logout?"));
        assertTrue(redirect.contains("id_token_hint=id-token-jwt"));
        assertTrue(redirect.contains("client_id=pinbal-back"));
        verify(session).invalidate();
    }

    @Test
    public void logoutSenseKeycloakSecurityContextRedirigeixSenseIdTokenHintNiClientId() {
        ControllerTestSupport.setField(controller, "authUrl", "http://127.0.0.1:1/auth");
        ControllerTestSupport.setField(controller, "authRealm", "pinbal");
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("pinbal.example.org");
        when(request.getServerPort()).thenReturn(443);
        when(request.getContextPath()).thenReturn("/pinbalback");

        String redirect = controller.logout(request, null);

        assertTrue(redirect.startsWith("redirect:http://127.0.0.1:1/auth/realms/pinbal/protocol/openid-connect/logout?"));
        assertTrue(!redirect.contains("id_token_hint"));
        assertTrue(!redirect.contains("client_id"));
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
