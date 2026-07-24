package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.pinbal.logic.intf.model.EntitatServeiResource;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.persist.resourceentity.EntitatResourceEntity;
import es.caib.pinbal.persist.resourceentity.EntitatServeiResourceEntity;
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
 * Prova els hooks que repliquen els efectes secundaris de negoci d'{@code EntitatService}
 * (validació SCSP, sincronització de serveis actius, invalidació de cache) i que el mètode
 * update queda bloquejat (com a la JSP, una assignació entitat-servei no es pot editar).
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class EntitatServeiResourceServiceImplTest {

    @Mock private EntitatService entitatService;
    @Mock private CacheHelper cacheHelper;

    @InjectMocks
    private EntitatServeiResourceServiceImpl service;

    private EntitatServeiResourceEntity buildEntity(String serveiCodi, Long entitatId, String entitatCodi) {
        EntitatResourceEntity entitat = new EntitatResourceEntity();
        entitat.setId(entitatId);
        entitat.setCodi(entitatCodi);
        EntitatServeiResourceEntity entity = new EntitatServeiResourceEntity();
        entity.setServeiCodi(serveiCodi);
        entity.setEntitat(entitat);
        return entity;
    }

    @Test
    public void beforeCreateEntity_serveiNoExisteix_llancaResourceNotCreated() {
        when(entitatService.scspServeiExisteix("SCDCPAJU")).thenReturn(false);
        EntitatServeiResourceEntity entity = buildEntity("SCDCPAJU", 5L, "ENT01");

        assertThrows(ResourceNotCreatedException.class, () -> service.beforeCreateEntity(entity, null, null));
    }

    @Test
    public void beforeCreateEntity_serveiExisteix_noLlancaRes() {
        when(entitatService.scspServeiExisteix("SCDCPAJU")).thenReturn(true);
        EntitatServeiResourceEntity entity = buildEntity("SCDCPAJU", 5L, "ENT01");

        assertDoesNotThrow(() -> service.beforeCreateEntity(entity, null, null));
    }

    @Test
    public void afterCreateSave_sincronitzaServeisActiusIInvalidaCache() {
        EntitatServeiResourceEntity entity = buildEntity("SCDCPAJU", 5L, "ENT01");

        service.afterCreateSave(entity, null, null, false);

        verify(entitatService).scspSincronitzarServeisActius(5L);
        verify(cacheHelper).evictServeisEntitat("ENT01");
    }

    @Test
    public void afterDelete_sincronitzaServeisActiusIInvalidaCache() {
        EntitatServeiResourceEntity entity = buildEntity("SCDCPAJU", 5L, "ENT01");

        service.afterDelete(entity, null);

        verify(entitatService).scspSincronitzarServeisActius(5L);
        verify(cacheHelper).evictServeisEntitat("ENT01");
    }

    @Test
    public void update_llancaUnsupportedOperation() {
        assertThrows(UnsupportedOperationException.class,
                () -> service.update(1L, new EntitatServeiResource(), null));
    }
}
