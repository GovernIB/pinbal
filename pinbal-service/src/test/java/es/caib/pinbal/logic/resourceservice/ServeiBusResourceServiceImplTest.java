package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.persist.resourceentity.ServeiBusResourceEntity;
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
 * Prova l'única lògica pròpia d'aquest recurs: validar que el servei SCSP existeixi abans de
 * crear una redirecció (com fa {@code ServeiServiceImpl.createServeiBus}). La resta de
 * create/update/delete/getOne/findPage són el mapeig genèric de {@code BaseMutableResourceService}.
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ServeiBusResourceServiceImplTest {

    @Mock private EntitatService entitatService;

    @InjectMocks
    private ServeiBusResourceServiceImpl service;

    private ServeiBusResourceEntity buildEntity(String serveiCodi) {
        ServeiBusResourceEntity entity = new ServeiBusResourceEntity();
        entity.setServeiCodi(serveiCodi);
        entity.setUrlDesti("https://exemple.caib.es/desti");
        return entity;
    }

    @Test
    public void beforeCreateEntity_serveiNoExisteix_llancaResourceNotCreated() {
        when(entitatService.scspServeiExisteix("SCDCPAJU")).thenReturn(false);
        ServeiBusResourceEntity entity = buildEntity("SCDCPAJU");

        assertThrows(ResourceNotCreatedException.class, () -> service.beforeCreateEntity(entity, null, null));
    }

    @Test
    public void beforeCreateEntity_serveiExisteix_noLlancaRes() {
        when(entitatService.scspServeiExisteix("SCDCPAJU")).thenReturn(true);
        ServeiBusResourceEntity entity = buildEntity("SCDCPAJU");

        assertDoesNotThrow(() -> service.beforeCreateEntity(entity, null, null));
    }
}
