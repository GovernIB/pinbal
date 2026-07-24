package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.intf.model.ProcedimentResource;
import es.caib.pinbal.persist.resourceentity.EntitatResourceEntity;
import es.caib.pinbal.persist.resourceentity.ProcedimentResourceEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.*;

/**
 * Prova els hooks que repliquen la invalidació de cache de {@code ProcedimentService}
 * (create/delete/canvi d'actiu), sense duplicar el fetch/save del procediment (vegeu el
 * comentari a {@link ProcedimentResourceServiceImpl}).
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ProcedimentResourceServiceImplTest {

    @Mock private CacheHelper cacheHelper;

    @InjectMocks
    private ProcedimentResourceServiceImpl service;

    private ProcedimentResourceEntity buildEntity(Long id, String entitatCodi, boolean actiu) {
        EntitatResourceEntity entitat = new EntitatResourceEntity();
        entitat.setCodi(entitatCodi);
        ProcedimentResourceEntity entity = new ProcedimentResourceEntity();
        entity.setId(id);
        entity.setEntitat(entitat);
        entity.setActiu(actiu);
        return entity;
    }

    private ProcedimentResource buildResource(boolean actiu) {
        ProcedimentResource resource = new ProcedimentResource();
        resource.setActiu(actiu);
        return resource;
    }

    @Test
    public void afterCreateSave_invalidaCachePerEntitat() {
        ProcedimentResourceEntity entity = buildEntity(1L, "ENT01", true);

        service.afterCreateSave(entity, null, null, false);

        verify(cacheHelper).evictProcedimentsPerEntitat("ENT01");
    }

    @Test
    public void afterDelete_invalidaCachePerEntitat() {
        ProcedimentResourceEntity entity = buildEntity(1L, "ENT01", true);

        service.afterDelete(entity, null);

        verify(cacheHelper).evictProcedimentsPerEntitat("ENT01");
    }

    @Test
    public void beforeUpdateEntity_actiuCanviat_invalidaCache() {
        ProcedimentResourceEntity entity = buildEntity(1L, "ENT01", true);

        service.beforeUpdateEntity(entity, buildResource(false), null);

        verify(cacheHelper).evictProcedimentsPerEntitat("ENT01");
    }

    @Test
    public void beforeUpdateEntity_actiuNoCanviat_noInvalidaCache() {
        ProcedimentResourceEntity entity = buildEntity(1L, "ENT01", true);

        service.beforeUpdateEntity(entity, buildResource(true), null);

        verify(cacheHelper, never()).evictProcedimentsPerEntitat(any());
    }
}
