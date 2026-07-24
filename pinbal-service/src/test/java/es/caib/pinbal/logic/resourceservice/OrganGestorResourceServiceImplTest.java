package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.intf.base.exception.ActionExecutionException;
import es.caib.pinbal.logic.intf.model.OrganGestorResource;
import es.caib.pinbal.logic.intf.model.OrganGestorSyncDir3Params;
import es.caib.pinbal.logic.intf.service.OrganGestorService;
import java.io.Serializable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Prova que l'acció "syncDir3" delega a {@code OrganGestorService.syncDir3OrgansGestors} i que
 * create/update/delete queden bloquejats (els òrgans gestors només es sincronitzen des de DIR3).
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class OrganGestorResourceServiceImplTest {

    @Mock private OrganGestorService organGestorService;

    @InjectMocks
    private OrganGestorResourceServiceImpl service;

    @BeforeEach
    public void init() {
        service.registerActions();
    }

    private OrganGestorSyncDir3Params buildParams(Long entitatId) {
        OrganGestorSyncDir3Params params = new OrganGestorSyncDir3Params();
        params.setEntitatId(entitatId);
        return params;
    }

    @Test
    public void syncDir3_delegaAOrganGestorService() throws Exception {
        when(organGestorService.syncDir3OrgansGestors(5L)).thenReturn(true);

        Serializable result = service.artifactActionExec(null, "syncDir3", buildParams(5L));

        assertEquals(Boolean.TRUE, result);
        verify(organGestorService).syncDir3OrgansGestors(5L);
    }

    @Test
    public void syncDir3_errorAlSincronitzar_llancaActionExecutionException() throws Exception {
        when(organGestorService.syncDir3OrgansGestors(5L)).thenThrow(new RuntimeException("error DIR3"));

        assertThrows(ActionExecutionException.class, () -> service.artifactActionExec(null, "syncDir3", buildParams(5L)));
    }

    @Test
    public void create_llancaUnsupportedOperation() {
        assertThrows(UnsupportedOperationException.class, () -> service.create(new OrganGestorResource(), null));
    }

    @Test
    public void update_llancaUnsupportedOperation() {
        assertThrows(UnsupportedOperationException.class, () -> service.update(1L, new OrganGestorResource(), null));
    }

    @Test
    public void delete_llancaUnsupportedOperation() {
        assertThrows(UnsupportedOperationException.class, () -> service.delete(1L, null));
    }
}
