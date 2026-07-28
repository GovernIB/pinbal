package es.caib.pinbal.api.interna.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.j2ee.J2eePreAuthenticatedProcessingFilter;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    public void setUp() {
        securityConfig = new SecurityConfig();
        ReflectionTestUtils.setField(securityConfig, "mappableRoles", "PBL_ADMIN,PBL_WS,PBL_USER");
        ReflectionTestUtils.setField(securityConfig, "useResourceRoleMappings", true);
    }

    @Test
    public void testAuthenticationManager() throws Exception {
        AuthenticationManager manager = securityConfig.authenticationManager();
        assertNotNull(manager);
    }

    @Test
    public void testPreauthAuthProvider() {
        PreAuthenticatedAuthenticationProvider provider = securityConfig.preauthAuthProvider();
        assertNotNull(provider);
    }

    @Test
    public void testGrantedAuthorityDefaults() {
        GrantedAuthorityDefaults defaults = securityConfig.grantedAuthorityDefaults();
        assertNotNull(defaults);
        assertEquals("", defaults.getRolePrefix());
    }

    @Test
    public void testPreAuthenticatedProcessingFilter() throws Exception {
        J2eePreAuthenticatedProcessingFilter filter = securityConfig.preAuthenticatedProcessingFilter();
        assertNotNull(filter);
    }

    @Test
    public void testGetLogoutHandler_Success() throws Exception {
        LogoutHandler logoutHandler = securityConfig.getLogoutHandler();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        logoutHandler.logout(request, response, null);
        // No excepció esperada: request.logout() es crida sense llançar ServletException.
    }

    @Test
    public void testGetLogoutHandler_ServletException() throws Exception {
        LogoutHandler logoutHandler = securityConfig.getLogoutHandler();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        org.mockito.Mockito.doThrow(new javax.servlet.ServletException("logout error")).when(request).logout();

        logoutHandler.logout(request, response, null);
        // La ServletException s'ha de capturar internament i només registrar-se al log.
    }

    @Test
    public void testPreAuthenticatedGrantedAuthoritiesUserDetailsService_WithoutJwtDetails() {
        PreAuthenticatedGrantedAuthoritiesUserDetailsService service =
                securityConfig.preAuthenticatedGrantedAuthoritiesUserDetailsService();

        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(
                "testuser", "N/A", AuthorityUtils.createAuthorityList("ROLE_PBL_WS"));
        token.setDetails(new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(
                mock(HttpServletRequest.class), AuthorityUtils.createAuthorityList("ROLE_PBL_WS")));

        UserDetails userDetails = service.loadUserDetails(token);

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    public void testPreAuthenticatedGrantedAuthoritiesUserDetailsService_WithJwtDetails() {
        PreAuthenticatedGrantedAuthoritiesUserDetailsService service =
                securityConfig.preAuthenticatedGrantedAuthoritiesUserDetailsService();

        Map<String, Object> claims = new HashMap<>();
        claims.put("given_name", "Joan");
        claims.put("family_name", "Petit");
        claims.put("name", "Joan Petit");
        claims.put("email", "joan.petit@caib.es");

        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(
                "jpetit", "N/A", AuthorityUtils.createAuthorityList("ROLE_PBL_WS"));
        SecurityConfig.JwtAuthenticationDetails details = new SecurityConfig.JwtAuthenticationDetails(
                mock(HttpServletRequest.class), AuthorityUtils.createAuthorityList("ROLE_PBL_WS"), claims);
        token.setDetails(details);

        UserDetails userDetails = service.loadUserDetails(token);

        assertNotNull(userDetails);
        assertEquals("jpetit", userDetails.getUsername());
        assertTrue(userDetails instanceof SecurityConfig.JwtUserDetails);
        SecurityConfig.JwtUserDetails jwtUserDetails = (SecurityConfig.JwtUserDetails) userDetails;
        assertEquals("Joan", jwtUserDetails.getGivenName());
        assertEquals("Petit", jwtUserDetails.getFamilyName());
        assertEquals("Joan Petit", jwtUserDetails.getFullName());
        assertEquals("joan.petit@caib.es", jwtUserDetails.getEmail());
        assertEquals(claims, details.getClaims());
    }

    @Test
    public void testAuthenticationDetailsSource_WithoutBearerToken() {
        AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> source =
                securityConfig.authenticationDetailsSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setUserPrincipal(() -> "testuser");
        request.addUserRole("PBL_WS");

        PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails details = source.buildDetails(request);

        assertNotNull(details);
    }

    @Test
    public void testAuthenticationDetailsSource_WithBearerToken() throws Exception {
        AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> source =
                securityConfig.authenticationDetailsSource();

        Map<String, Object> claims = new HashMap<>();
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("PBL_ADMIN"));
        claims.put("realm_access", realmAccess);
        Map<String, Object> resourceAccess = new HashMap<>();
        Map<String, Object> clientAccess = new HashMap<>();
        clientAccess.put("roles", Arrays.asList("PBL_USER"));
        resourceAccess.put("pinbal-client", clientAccess);
        claims.put("resource_access", resourceAccess);
        claims.put("azp", "pinbal-client");

        ObjectMapper mapper = new ObjectMapper();
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(mapper.writeValueAsBytes(claims));
        String bearer = "Bearer header." + payload + ".signature";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setUserPrincipal(() -> "testuser");
        request.addUserRole("PBL_WS");
        request.addHeader("Authorization", bearer);

        PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails details = source.buildDetails(request);

        assertNotNull(details);
        assertTrue(details instanceof SecurityConfig.JwtAuthenticationDetails);
    }
}
