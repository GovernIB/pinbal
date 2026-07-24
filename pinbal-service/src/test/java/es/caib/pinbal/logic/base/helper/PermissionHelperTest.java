package es.caib.pinbal.logic.base.helper;

import es.caib.pinbal.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.pinbal.logic.intf.base.annotation.ResourceArtifact;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.model.ResourceArtifactType;
import es.caib.pinbal.logic.intf.base.permission.PermissionEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static es.caib.pinbal.logic.intf.base.annotation.ResourceAccessConstraint.ResourceAccessConstraintType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class PermissionHelperTest {

    @Mock private AuthenticationHelper authenticationHelper;

    private PermissionHelper helper;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
        helper = new PermissionHelper();
        ReflectionTestUtils.setField(helper, "authenticationHelper", authenticationHelper);
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Authentication authenticat(String... roles) {
        List<SimpleGrantedAuthority> authorities = java.util.Arrays.stream(roles)
                .map(SimpleGrantedAuthority::new)
                .collect(java.util.stream.Collectors.toList());
        return new UsernamePasswordAuthenticationToken("usuari1", "cred", authorities);
    }

    @ResourceConfig
    static class RecursSenseRestriccions {}

    static class RecursSenseAnotacio {}

    @ResourceConfig(accessConstraints = @ResourceAccessConstraint(
            type = PERMIT_ALL, grantedPermissions = {PermissionEnum.WRITE}))
    static class RecursPermitAll {}

    @ResourceConfig(accessConstraints = @ResourceAccessConstraint(
            type = AUTHENTICATED, grantedPermissions = {PermissionEnum.READ}))
    static class RecursAutenticatAmbReadGranted {}

    @ResourceConfig(accessConstraints = @ResourceAccessConstraint(
            type = ROLE, roles = {"PBL_ADMIN"}, grantedPermissions = {PermissionEnum.READ, PermissionEnum.WRITE}))
    static class RecursAmbRolAdmin {}

    @ResourceConfig(artifacts = @ResourceArtifact(
            type = ResourceArtifactType.REPORT, code = "informe1"))
    static class RecursAmbArtefacteSenseRestriccioPropia {}

    @ResourceConfig(artifacts = @ResourceArtifact(
            type = ResourceArtifactType.REPORT, code = "informe2",
            accessConstraints = @ResourceAccessConstraint(type = PERMIT_ALL)))
    static class RecursAmbArtefacteAmbPermitAll {}

    @Test
    public void checkResourcePermission_classeNoExisteix_retornaFalse() {
        boolean result = helper.checkResourcePermission(
                authenticat(), null, "es.caib.NoExisteix", null);

        assertFalse(result);
    }

    @Test
    public void checkResourcePermission_senseAnotacioResourceConfig_retornaTrue() {
        boolean result = helper.checkResourcePermission(
                authenticat(), null, RecursSenseAnotacio.class.getName(), null);

        assertTrue(result);
    }

    @Test
    public void checkResourcePermission_ambResourceConfigSenseRestriccions_retornaTrue() {
        boolean result = helper.checkResourcePermission(
                authenticat(), null, RecursSenseRestriccions.class.getName(), null);

        assertTrue(result);
    }

    @Test
    public void checkResourcePermission_permitAll_retornaTrue() {
        boolean result = helper.checkResourcePermission(
                authenticat(), null, RecursPermitAll.class.getName(),
                new BasePermission[]{(BasePermission) BasePermission.WRITE});

        assertTrue(result);
    }

    @Test
    public void checkResourcePermission_autenticatAmbPermisConcedit_retornaTrue() {
        boolean result = helper.checkResourcePermission(
                authenticat(), null, RecursAutenticatAmbReadGranted.class.getName(),
                new BasePermission[]{(BasePermission) BasePermission.READ});

        assertTrue(result);
    }

    @Test
    public void checkResourcePermission_autenticatAmbPermisNoConcedit_retornaFalse() {
        boolean result = helper.checkResourcePermission(
                authenticat(), null, RecursAutenticatAmbReadGranted.class.getName(),
                new BasePermission[]{(BasePermission) BasePermission.WRITE});

        assertFalse(result);
    }

    @Test
    public void checkResourcePermission_noAutenticat_retornaFalse() {
        Authentication noAutenticat = mock(Authentication.class);
        when(noAutenticat.isAuthenticated()).thenReturn(false);

        boolean result = helper.checkResourcePermission(
                noAutenticat, null, RecursAutenticatAmbReadGranted.class.getName(),
                new BasePermission[]{(BasePermission) BasePermission.READ});

        assertFalse(result);
    }

    @Test
    public void checkResourcePermission_rolCorrecte_retornaTrue() {
        Authentication auth = authenticat("PBL_ADMIN");
        when(authenticationHelper.isCurrentUserInRole(auth, "PBL_ADMIN")).thenReturn(true);

        boolean result = helper.checkResourcePermission(
                auth, null, RecursAmbRolAdmin.class.getName(),
                new BasePermission[]{(BasePermission) BasePermission.READ});

        assertTrue(result);
    }

    @Test
    public void checkResourcePermission_rolIncorrecte_retornaFalse() {
        Authentication auth = authenticat("PBL_AUDIT");
        when(authenticationHelper.isCurrentUserInRole(auth, "PBL_ADMIN")).thenReturn(false);

        boolean result = helper.checkResourcePermission(
                auth, null, RecursAmbRolAdmin.class.getName(),
                new BasePermission[]{(BasePermission) BasePermission.READ});

        assertFalse(result);
    }

    @Test
    public void checkResourceArtifactPermission_recursSenseAnotacio_retornaFalse() {
        boolean result = helper.checkResourceArtifactPermission(
                RecursSenseAnotacio.class, ResourceArtifactType.REPORT, "informe1");

        assertFalse(result);
    }

    @Test
    public void checkResourceArtifactPermission_artefacteNoDefinit_retornaFalse() {
        boolean result = helper.checkResourceArtifactPermission(
                RecursAmbArtefacteSenseRestriccioPropia.class, ResourceArtifactType.REPORT, "codiInexistent");

        assertFalse(result);
    }

    @Test
    public void checkResourceArtifactPermission_ambRestriccioPropiaPermitAll_retornaTrue() {
        boolean result = helper.checkResourceArtifactPermission(
                RecursAmbArtefacteAmbPermitAll.class, ResourceArtifactType.REPORT, "informe2");

        assertTrue(result);
    }
}
