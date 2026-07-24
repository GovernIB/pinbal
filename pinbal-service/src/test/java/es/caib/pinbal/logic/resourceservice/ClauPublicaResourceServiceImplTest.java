package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.pinbal.logic.intf.model.ClauPublicaResource;
import es.caib.pinbal.persist.resourceentity.ClauPublicaResourceEntity;
import es.caib.pinbal.persist.resourcerepository.ClauPublicaResourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Prova l'única lògica pròpia d'aquest recurs: la unicitat de {@code nom} i {@code alies}
 * (JSP: {@code @ClauPublicaNoRepetida}), que el PK autogenerat no garanteix per si sol.
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ClauPublicaResourceServiceImplTest {

    @Mock private ClauPublicaResourceRepository clauPublicaResourceRepository;

    @InjectMocks
    private ClauPublicaResourceServiceImpl service;

    private ClauPublicaResource buildResource(String nom, String alies) {
        ClauPublicaResource resource = new ClauPublicaResource();
        resource.setNom(nom);
        resource.setAlies(alies);
        return resource;
    }

    private ClauPublicaResourceEntity buildEntity(Long id) {
        ClauPublicaResourceEntity entity = new ClauPublicaResourceEntity();
        entity.setId(id);
        return entity;
    }

    @Test
    public void beforeCreateEntity_nomIAliesUnics_noLlancaRes() {
        assertDoesNotThrow(() -> service.beforeCreateEntity(buildEntity(null), buildResource("nom1", "alies1"), null));
    }

    @Test
    public void beforeCreateEntity_nomRepetit_llancaResourceNotCreated() {
        when(clauPublicaResourceRepository.findByNom("nom1")).thenReturn(buildEntity(5L));

        assertThrows(ResourceNotCreatedException.class,
                () -> service.beforeCreateEntity(buildEntity(null), buildResource("nom1", "alies1"), null));
    }

    @Test
    public void beforeCreateEntity_aliesRepetit_llancaResourceNotCreated() {
        when(clauPublicaResourceRepository.findByAlies("alies1")).thenReturn(buildEntity(5L));

        assertThrows(ResourceNotCreatedException.class,
                () -> service.beforeCreateEntity(buildEntity(null), buildResource("nom1", "alies1"), null));
    }

    @Test
    public void beforeUpdateEntity_repetitEnLaMateixaFila_noLlancaRes() {
        when(clauPublicaResourceRepository.findByNom("nom1")).thenReturn(buildEntity(10L));
        when(clauPublicaResourceRepository.findByAlies("alies1")).thenReturn(buildEntity(10L));

        assertDoesNotThrow(() -> service.beforeUpdateEntity(buildEntity(10L), buildResource("nom1", "alies1"), null));
    }

    @Test
    public void beforeUpdateEntity_repetitEnUnaAltraFila_llancaResourceNotUpdated() {
        when(clauPublicaResourceRepository.findByNom("nom1")).thenReturn(buildEntity(5L));

        assertThrows(ResourceNotUpdatedException.class,
                () -> service.beforeUpdateEntity(buildEntity(10L), buildResource("nom1", "alies1"), null));
    }

}
