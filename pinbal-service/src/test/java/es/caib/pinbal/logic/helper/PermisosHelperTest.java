package es.caib.pinbal.logic.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermisosHelperTest {

    @Mock private MutableAclService aclService;
    @Mock private MutableAcl acl;
    @Mock private Authentication auth;

    @Test
    public void isGrantedAny_permisGranted_retornaTrue() {
        when(auth.getName()).thenReturn("usuari1");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);

        boolean result = PermisosHelper.isGrantedAny(
                1L, String.class,
                new Permission[]{BasePermission.READ},
                aclService, auth);

        assertTrue(result);
    }

    @Test
    public void isGrantedAny_permisNoGranted_retornaFalse() {
        when(auth.getName()).thenReturn("usuari1");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenThrow(new NotFoundException("not found"));

        boolean result = PermisosHelper.isGrantedAny(
                1L, String.class,
                new Permission[]{BasePermission.READ},
                aclService, auth);

        assertFalse(result);
    }

    @Test
    public void isGrantedAny_aclNoExisteix_retornaFalse() {
        when(auth.getName()).thenReturn("usuari1");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        when(aclService.readAclById(any(ObjectIdentity.class))).thenThrow(new NotFoundException("no acl"));

        boolean result = PermisosHelper.isGrantedAny(
                1L, String.class,
                new Permission[]{BasePermission.READ},
                aclService, auth);

        assertFalse(result);
    }

    @Test
    public void isGrantedAll_totsGranted_retornaTrue() {
        when(auth.getName()).thenReturn("usuari1");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);

        boolean result = PermisosHelper.isGrantedAll(
                1L, String.class,
                new Permission[]{BasePermission.READ, BasePermission.WRITE},
                aclService, auth);

        assertTrue(result);
    }

    @Test
    public void isGrantedAll_unNoGranted_retornaFalse() {
        when(auth.getName()).thenReturn("usuari1");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        when(acl.isGranted(anyList(), anyList(), eq(false)))
                .thenReturn(true)
                .thenThrow(new NotFoundException("not found"));

        boolean result = PermisosHelper.isGrantedAll(
                1L, String.class,
                new Permission[]{BasePermission.READ, BasePermission.WRITE},
                aclService, auth);

        assertFalse(result);
    }

    @Test
    public void filterGrantedAny_eliminaObjectesNoPermessos() {
        when(auth.getName()).thenReturn("usuari1");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        when(aclService.readAclById(any(ObjectIdentity.class))).thenThrow(new NotFoundException("no acl"));

        List<Long> ids = new ArrayList<>(Arrays.asList(1L, 2L));
        PermisosHelper.filterGrantedAny(
                ids,
                (obj) -> (Long) obj,
                Long.class,
                new Permission[]{BasePermission.READ},
                aclService,
                auth);

        assertTrue(ids.isEmpty());
    }

    @Test
    public void filterGrantedAll_eliminaObjectesNoPermessos() {
        when(auth.getName()).thenReturn("usuari1");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        when(aclService.readAclById(any(ObjectIdentity.class))).thenThrow(new NotFoundException("no acl"));

        List<Long> ids = new ArrayList<>(Arrays.asList(1L, 2L));
        PermisosHelper.filterGrantedAll(
                ids,
                (obj) -> (Long) obj,
                Long.class,
                new Permission[]{BasePermission.READ},
                aclService,
                auth);

        assertTrue(ids.isEmpty());
    }

    @Test
    public void revocarPermisosServei_aclNoExisteix_noCridaUpdate() {
        when(aclService.readAclById(any(ObjectIdentity.class))).thenThrow(new NotFoundException("no acl"));

        assertDoesNotThrow(() -> PermisosHelper.revocarPermisosServei(String.class, 1L, aclService));
        verify(aclService, never()).updateAcl(any());
    }

    @Test
    public void revocarPermisosServei_aclExisteix_eliminaAces() {
        List<AccessControlEntry> aces = new ArrayList<>();
        AccessControlEntry ace1 = mock(AccessControlEntry.class);
        aces.add(ace1);
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        when(acl.getEntries()).thenReturn(aces);

        PermisosHelper.revocarPermisosServei(String.class, 1L, aclService);

        verify(acl).deleteAce(0);
        verify(aclService).updateAcl(acl);
    }

    @Test
    public void getAclSids_aclNoExisteix_retornaNull() {
        when(aclService.readAclById(any(ObjectIdentity.class))).thenThrow(new NotFoundException("no acl"));

        List<AccessControlEntry> result = PermisosHelper.getAclSids(String.class, 1L, aclService);

        assertNull(result);
    }

    @Test
    public void getAclSids_aclExisteix_retornaEntrades() {
        List<AccessControlEntry> aces = Collections.singletonList(mock(AccessControlEntry.class));
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        when(acl.getEntries()).thenReturn(aces);

        List<AccessControlEntry> result = PermisosHelper.getAclSids(String.class, 1L, aclService);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
