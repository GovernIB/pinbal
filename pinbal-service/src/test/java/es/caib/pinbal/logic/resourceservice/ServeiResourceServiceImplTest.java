package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.intf.model.ServeiResource;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.persist.resourceentity.ServeiResourceEntity;
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
 * Prova els hooks que repliquen els efectes secundaris de negoci d'{@code ServeiService}
 * (sincronització de la descripció SCSP, invalidació de caches quan canvia l'actiu, protecció
 * del codi) i que create/delete queden bloquejats.
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ServeiResourceServiceImplTest {

    @Mock private ServeiService serveiService;

    @InjectMocks
    private ServeiResourceServiceImpl service;

    private ServeiResourceEntity buildEntity(String codi, boolean actiu) {
        ServeiResourceEntity entity = new ServeiResourceEntity();
        entity.setCodi(codi);
        entity.setActiu(actiu);
        return entity;
    }

    private ServeiResource buildResource(String codi, String descripcio, boolean actiu) {
        ServeiResource resource = new ServeiResource();
        resource.setCodi(codi);
        resource.setDescripcio(descripcio);
        resource.setActiu(actiu);
        return resource;
    }

    @Test
    public void beforeUpdateEntity_codiCanviat_llancaUnsupportedOperation() {
        ServeiResourceEntity entity = buildEntity("SCDCPAJU", true);
        ServeiResource resource = buildResource("ALTRE_CODI", "Padró", true);

        assertThrows(UnsupportedOperationException.class, () -> service.beforeUpdateEntity(entity, resource, null));
    }

    @Test
    public void beforeUpdateEntity_actiuCanviat_invalidaCaches() {
        ServeiResourceEntity entity = buildEntity("SCDCPAJU", true);
        ServeiResource resource = buildResource("SCDCPAJU", "Padró", false);

        service.beforeUpdateEntity(entity, resource, null);

        verify(serveiService).evictCachesPerServei("SCDCPAJU");
    }

    @Test
    public void beforeUpdateEntity_actiuNoCanviat_noInvalidaCaches() {
        ServeiResourceEntity entity = buildEntity("SCDCPAJU", true);
        ServeiResource resource = buildResource("SCDCPAJU", "Padró", true);

        service.beforeUpdateEntity(entity, resource, null);

        verify(serveiService, never()).evictCachesPerServei(any());
    }

    @Test
    public void afterUpdateSave_sincronitzaDescripcioScsp() {
        ServeiResourceEntity entity = buildEntity("SCDCPAJU", true);
        ServeiResource resource = buildResource("SCDCPAJU", "Padró municipal", true);

        service.afterUpdateSave(entity, resource, null, false);

        verify(serveiService).scspActualitzarDescripcio("SCDCPAJU", "Padró municipal");
    }

    @Test
    public void create_llancaUnsupportedOperation() {
        assertThrows(UnsupportedOperationException.class, () -> service.create(new ServeiResource(), null));
    }

    @Test
    public void delete_llancaUnsupportedOperation() {
        assertThrows(UnsupportedOperationException.class, () -> service.delete(1L, null));
    }
}
