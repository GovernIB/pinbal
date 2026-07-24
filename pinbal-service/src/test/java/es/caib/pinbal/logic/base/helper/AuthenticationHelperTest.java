package es.caib.pinbal.logic.base.helper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationHelperTest {

    private final AuthenticationHelper helper = new AuthenticationHelper();

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setAuth(Authentication auth) {
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void getCurrentUserName_retornaElNomDeLUsuariAutenticat() {
        setAuth(new TestingAuthenticationToken("usuari1", "cred"));

        assertEquals("usuari1", helper.getCurrentUserName());
    }

    @Test
    public void getCurrentUserRoles_retornaTotsElsRolsDeLUsuari() {
        setAuth(new UsernamePasswordAuthenticationToken("usuari1", "cred", List.of(
                new SimpleGrantedAuthority("PBL_ADMIN"),
                new SimpleGrantedAuthority("PBL_AUDIT"))));

        String[] roles = helper.getCurrentUserRoles();

        assertEquals(2, roles.length);
        assertTrue(List.of(roles).contains("PBL_ADMIN"));
        assertTrue(List.of(roles).contains("PBL_AUDIT"));
    }

    @Test
    public void isCurrentUserInRole_usuariTeElRol_retornaTrue() {
        setAuth(new UsernamePasswordAuthenticationToken("usuari1", "cred", List.of(
                new SimpleGrantedAuthority("PBL_ADMIN"))));

        assertTrue(helper.isCurrentUserInRole("PBL_ADMIN"));
    }

    @Test
    public void isCurrentUserInRole_usuariNoTeElRol_retornaFalse() {
        setAuth(new UsernamePasswordAuthenticationToken("usuari1", "cred", List.of(
                new SimpleGrantedAuthority("PBL_AUDIT"))));

        assertFalse(helper.isCurrentUserInRole("PBL_ADMIN"));
    }

    @Test
    public void isCurrentUserInRole_ambAuthenticationExplicit_usuariTeElRol_retornaTrue() {
        Authentication auth = new UsernamePasswordAuthenticationToken("usuari1", "cred", List.of(
                new SimpleGrantedAuthority("PBL_SUPERAUD")));

        assertTrue(helper.isCurrentUserInRole(auth, "PBL_SUPERAUD"));
    }

    @Test
    public void isCurrentUserInRole_ambAuthenticationExplicit_usuariNoTeElRol_retornaFalse() {
        Authentication auth = new UsernamePasswordAuthenticationToken("usuari1", "cred", List.of(
                new SimpleGrantedAuthority("PBL_SUPERAUD")));

        assertFalse(helper.isCurrentUserInRole(auth, "PBL_ADMIN"));
    }

    @Test
    public void isCurrentUserInRole_senseRols_retornaFalse() {
        setAuth(new UsernamePasswordAuthenticationToken("usuari1", "cred", List.of()));

        assertFalse(helper.isCurrentUserInRole("PBL_ADMIN"));
    }
}
