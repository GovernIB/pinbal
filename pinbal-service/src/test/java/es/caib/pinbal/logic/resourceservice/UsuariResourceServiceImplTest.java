package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.helper.AuthenticationHelper;
import es.caib.pinbal.logic.intf.model.auth.PinbalAuthenticationDetails;
import es.caib.pinbal.persist.resourceentity.UsuariResourceEntity;
import es.caib.pinbal.persist.resourcerepository.UsuariResourceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class UsuariResourceServiceImplTest {

    @Mock private AuthenticationHelper authenticationHelper;
    @Mock private UsuariResourceRepository usuariResourceRepository;

    @InjectMocks
    private UsuariResourceServiceImpl service;

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Simula una JwtAuthenticationToken sense afegir la dependència de
     * spring-security-oauth2-resource-server, que no forma part d'aquest mòdul.
     */
    private AbstractAuthenticationToken jwtAuthentication(Jwt jwt) {
        AbstractAuthenticationToken token = new AbstractAuthenticationToken(Collections.emptyList()) {
            @Override
            public Object getCredentials() {
                return jwt;
            }

            @Override
            public Object getPrincipal() {
                return jwt;
            }

            @Override
            public String getName() {
                return jwt.getSubject();
            }
        };
        token.setAuthenticated(true);
        return token;
    }

    private Jwt buildJwt(String subject, String name, String nif, String email) {
        return Jwt.withTokenValue("token-value")
                .header("alg", "none")
                .subject(subject)
                .claim("name", name)
                .claim("nif", nif)
                .claim("email", email)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    @Test
    public void refresh_authenticacioJwt_usuariNouEsCrea() {
        Jwt jwt = buildJwt("usuari1", "Joan Bibiloni", "12345678A", "joan@test.cat");
        AbstractAuthenticationToken auth = jwtAuthentication(jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(authenticationHelper.getCurrentUserName()).thenReturn("usuari1");
        when(usuariResourceRepository.findById("usuari1")).thenReturn(Optional.empty());

        service.refresh();

        verify(usuariResourceRepository).save(argThat(entity ->
                "usuari1".equals(entity.getId())
                        && "Joan Bibiloni".equals(entity.getNom())
                        && "12345678A".equals(entity.getNif())
                        && "joan@test.cat".equals(entity.getEmail())));
    }

    @Test
    public void refresh_authenticacioJwt_usuariExistentActualitzaCampsCanviats() {
        Jwt jwt = buildJwt("usuari1", "Nom Nou", "12345678A", "nou@test.cat");
        AbstractAuthenticationToken auth = jwtAuthentication(jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        UsuariResourceEntity existent = new UsuariResourceEntity();
        existent.setId("usuari1");
        existent.setNom("Nom Vell");
        existent.setNif("12345678A");
        existent.setEmail("nou@test.cat");

        when(authenticationHelper.getCurrentUserName()).thenReturn("usuari1");
        when(usuariResourceRepository.findById("usuari1")).thenReturn(Optional.of(existent));

        service.refresh();

        verify(usuariResourceRepository).save(existent);
        assertEquals("Nom Nou", existent.getNom());
    }

    @Test
    public void refresh_authenticacioJwt_usuariExistentSenseCanvis_noEsGuarda() {
        Jwt jwt = buildJwt("usuari1", "Nom Igual", "12345678A", "igual@test.cat");
        AbstractAuthenticationToken auth = jwtAuthentication(jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        UsuariResourceEntity existent = new UsuariResourceEntity();
        existent.setId("usuari1");
        existent.setNom("Nom Igual");
        existent.setNif("12345678A");
        existent.setEmail("igual@test.cat");

        when(authenticationHelper.getCurrentUserName()).thenReturn("usuari1");
        when(usuariResourceRepository.findById("usuari1")).thenReturn(Optional.of(existent));

        service.refresh();

        verify(usuariResourceRepository, never()).save(any());
    }

    @Test
    public void refresh_authenticacioUserAmbPinbalDetails_usuariNouEsCrea() {
        User user = new User("usuari2", "N/A", Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEST")));
        PinbalAuthenticationDetails details = mock(PinbalAuthenticationDetails.class);
        when(details.getName()).thenReturn("Maria Ramis");
        when(details.getNif()).thenReturn("87654321B");
        when(details.getEmail()).thenReturn("maria@test.cat");

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        auth.setDetails(details);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(authenticationHelper.getCurrentUserName()).thenReturn("usuari2");
        when(usuariResourceRepository.findById("usuari2")).thenReturn(Optional.empty());

        service.refresh();

        verify(usuariResourceRepository).save(argThat(entity ->
                "usuari2".equals(entity.getId())
                        && "Maria Ramis".equals(entity.getNom())
                        && "87654321B".equals(entity.getNif())
                        && "maria@test.cat".equals(entity.getEmail())));
    }

    @Test
    public void refresh_authenticacioUserSensePinbalDetails_esCreaUsuariMinim() {
        User user = new User("usuari3", "N/A", Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEST")));
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(authenticationHelper.getCurrentUserName()).thenReturn("usuari3");
        when(usuariResourceRepository.findById("usuari3")).thenReturn(Optional.empty());

        service.refresh();

        verify(usuariResourceRepository).save(argThat(entity ->
                "usuari3".equals(entity.getId())
                        && entity.getNom() == null
                        && entity.getEmail() == null));
    }

    @Test
    public void refresh_sensePrincipalReconegut_noFaResNi(){
        TestingAuthenticationToken auth = new TestingAuthenticationToken("principal-desconegut", "cred");
        SecurityContextHolder.getContext().setAuthentication(auth);

        service.refresh();

        verify(usuariResourceRepository, never()).save(any());
        verify(usuariResourceRepository, never()).findById(any());
    }

    @Test
    public void additionalSpringFilter_construeixFiltrePerUsuariActual() {
        when(authenticationHelper.getCurrentUserName()).thenReturn("usuari1");

        String filter = org.springframework.test.util.ReflectionTestUtils.invokeMethod(
                service, "additionalSpringFilter", "", new String[0]);

        assertEquals("id:'usuari1'", filter);
    }
}
