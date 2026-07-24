package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.pinbal.logic.intf.model.EmissorCertResource;
import es.caib.pinbal.persist.resourceentity.EmissorCertResourceEntity;
import es.caib.pinbal.persist.resourcerepository.EmissorCertResourceRepository;
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
 * Prova l'única lògica pròpia d'aquest recurs: la unicitat del CIF (JSP: {@code @CifEmisorNoRepetit}),
 * que el PK autogenerat no garanteix per si sol.
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class EmissorCertResourceServiceImplTest {

    @Mock private EmissorCertResourceRepository emissorCertResourceRepository;

    @InjectMocks
    private EmissorCertResourceServiceImpl service;

    private EmissorCertResource buildResource(String cif) {
        EmissorCertResource resource = new EmissorCertResource();
        resource.setCif(cif);
        resource.setNom("Emissor");
        return resource;
    }

    private EmissorCertResourceEntity buildEntity(Long id) {
        EmissorCertResourceEntity entity = new EmissorCertResourceEntity();
        entity.setId(id);
        return entity;
    }

    @Test
    public void beforeCreateEntity_cifNoRepetit_noLlancaRes() {
        when(emissorCertResourceRepository.findByCif("12345678A")).thenReturn(null);

        assertDoesNotThrow(() -> service.beforeCreateEntity(buildEntity(null), buildResource("12345678A"), null));
    }

    @Test
    public void beforeCreateEntity_cifRepetit_llancaResourceNotCreated() {
        when(emissorCertResourceRepository.findByCif("12345678A")).thenReturn(buildEntity(5L));

        assertThrows(ResourceNotCreatedException.class,
                () -> service.beforeCreateEntity(buildEntity(null), buildResource("12345678A"), null));
    }

    @Test
    public void beforeUpdateEntity_cifRepetitEnUnaAltraFila_llancaResourceNotUpdated() {
        when(emissorCertResourceRepository.findByCif("12345678A")).thenReturn(buildEntity(5L));

        assertThrows(ResourceNotUpdatedException.class,
                () -> service.beforeUpdateEntity(buildEntity(10L), buildResource("12345678A"), null));
    }

    @Test
    public void beforeUpdateEntity_cifRepetitEnLaMateixaFila_noLlancaRes() {
        when(emissorCertResourceRepository.findByCif("12345678A")).thenReturn(buildEntity(10L));

        assertDoesNotThrow(() -> service.beforeUpdateEntity(buildEntity(10L), buildResource("12345678A"), null));
    }

}
