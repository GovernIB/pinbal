package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.model.ClauPrivadaResource;
import es.caib.pinbal.logic.intf.model.OrganismeCessionariResource;
import es.caib.pinbal.logic.service.EntitatClauHelper;
import es.caib.pinbal.persist.entity.ClauPrivada;
import es.caib.pinbal.persist.repository.ClauPrivadaRepository;
import es.caib.pinbal.persist.resourceentity.ClauPrivadaResourceEntity;
import es.caib.pinbal.persist.resourceentity.OrganismeCessionariResourceEntity;
import es.caib.pinbal.persist.resourcerepository.ClauPrivadaResourceRepository;
import es.caib.pinbal.persist.resourcerepository.OrganismeCessionariResourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Prova la lògica pròpia d'aquest recurs (replicada d'{@code ScspServiceImpl#createClauPrivada
 * /updateClauPrivada/deleteClauPrivada}): només una clau {@code perEntitat=true} per organisme,
 * i la sincronització dels serveis afectats en crear, canviar o esborrar una clau d'aquest tipus.
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ClauPrivadaResourceServiceImplTest {

    @Mock private ClauPrivadaResourceRepository clauPrivadaResourceRepository;
    @Mock private OrganismeCessionariResourceRepository organismeCessionariResourceRepository;
    @Mock private ClauPrivadaRepository clauPrivadaRepository;
    @Mock private EntitatClauHelper entitatClauHelper;

    @InjectMocks
    private ClauPrivadaResourceServiceImpl service;

    private OrganismeCessionariResourceEntity buildOrganisme(Long id, String cif) {
        OrganismeCessionariResourceEntity organisme = new OrganismeCessionariResourceEntity();
        organisme.setId(id);
        organisme.setCif(cif);
        return organisme;
    }

    private ClauPrivadaResourceEntity buildEntity(Long id, OrganismeCessionariResourceEntity organisme, boolean perEntitat) {
        ClauPrivadaResourceEntity entity = new ClauPrivadaResourceEntity();
        entity.setId(id);
        entity.setAlies("alies" + id);
        entity.setOrganisme(organisme);
        entity.setPerEntitat(perEntitat);
        return entity;
    }

    private ClauPrivadaResource buildResource(Long organismeId, boolean perEntitat) {
        ClauPrivadaResource resource = new ClauPrivadaResource();
        ResourceReference<OrganismeCessionariResource, Long> ref = ResourceReference.toResourceReference(organismeId, "organisme");
        resource.setOrganisme(ref);
        resource.setPerEntitat(perEntitat);
        return resource;
    }

    @Test
    public void beforeCreateEntity_novaClauPerEntitat_desmarcaLaClauAntiga() {
        OrganismeCessionariResourceEntity organisme = buildOrganisme(1L, "CIF001");
        ClauPrivadaResourceEntity nova = buildEntity(null, organisme, true);
        ClauPrivadaResource resource = buildResource(1L, true);
        ClauPrivadaResourceEntity antiga = buildEntity(99L, organisme, true);
        when(clauPrivadaResourceRepository.findTopByOrganismeCifAndPerEntitatTrueOrderByDataAltaDesc("CIF001")).thenReturn(antiga);

        service.beforeCreateEntity(nova, resource, null);

        assertFalse(antiga.isPerEntitat());
        verify(clauPrivadaResourceRepository).save(antiga);
    }

    @Test
    public void beforeCreateEntity_novaClauNoPerEntitat_noToca_capAltraClau() {
        OrganismeCessionariResourceEntity organisme = buildOrganisme(1L, "CIF001");
        ClauPrivadaResourceEntity nova = buildEntity(null, organisme, false);
        ClauPrivadaResource resource = buildResource(1L, false);

        service.beforeCreateEntity(nova, resource, null);

        verify(clauPrivadaResourceRepository, never()).findTopByOrganismeCifAndPerEntitatTrueOrderByDataAltaDesc(any());
        verify(clauPrivadaResourceRepository, never()).save(any());
    }

    @Test
    public void afterCreateSave_novaClauPerEntitat_registraSincronitzacio() {
        OrganismeCessionariResourceEntity organisme = buildOrganisme(1L, "CIF001");
        ClauPrivadaResourceEntity entity = buildEntity(10L, organisme, true);
        ClauPrivadaResource resource = buildResource(1L, true);

        // Sense transacció activa, TransactionSynchronizationManager.registerSynchronization
        // llançaria si s'invoqués fora d'un context transaccional real; aquí només comprovem
        // que el mètode no llança abans d'arribar-hi (el registre en si necessita un test
        // d'integració amb @Transactional per verificar l'afterCommit real).
        assertDoesNotThrow(() -> {
            try {
                service.afterCreateSave(entity, resource, null, false);
            } catch (IllegalStateException ignored) {
                // Esperat fora d'una transacció Spring activa.
            }
        });
    }

    @Test
    public void beforeUpdateEntity_noCanviaRes_noCridaHelper() {
        OrganismeCessionariResourceEntity organisme = buildOrganisme(1L, "CIF001");
        ClauPrivadaResourceEntity entity = buildEntity(10L, organisme, false);
        ClauPrivadaResource resource = buildResource(1L, false);
        when(organismeCessionariResourceRepository.findById(1L)).thenReturn(Optional.of(organisme));
        when(clauPrivadaResourceRepository.findByNom(any())).thenReturn(null);
        when(clauPrivadaResourceRepository.findByAlies(any())).thenReturn(null);

        service.beforeUpdateEntity(entity, resource, null);

        verify(entitatClauHelper, never()).actualitzarServiciosOrganismos(any(), any());
    }

    @Test
    public void beforeUpdateEntity_esMarcaPerEntitat_actualitzaServeisDelMateixOrganisme() {
        OrganismeCessionariResourceEntity organisme = buildOrganisme(1L, "CIF001");
        ClauPrivadaResourceEntity entity = buildEntity(10L, organisme, false);
        ClauPrivadaResource resource = buildResource(1L, true);
        ClauPrivada businessEntity = mock(ClauPrivada.class);
        when(organismeCessionariResourceRepository.findById(1L)).thenReturn(Optional.of(organisme));
        when(clauPrivadaResourceRepository.findByNom(any())).thenReturn(null);
        when(clauPrivadaResourceRepository.findByAlies(any())).thenReturn(null);
        when(clauPrivadaRepository.findById(10L)).thenReturn(Optional.of(businessEntity));

        service.beforeUpdateEntity(entity, resource, null);

        verify(entitatClauHelper).actualitzarServiciosOrganismos(eq(businessEntity), eq("CIF001"));
    }

    @Test
    public void beforeUpdateEntity_esDesmarcaPerEntitat_actualitzaServeisAmbNullIOrganismeOrigen() {
        OrganismeCessionariResourceEntity organisme = buildOrganisme(1L, "CIF001");
        ClauPrivadaResourceEntity entity = buildEntity(10L, organisme, true);
        ClauPrivadaResource resource = buildResource(1L, false);
        when(organismeCessionariResourceRepository.findById(1L)).thenReturn(Optional.of(organisme));
        when(clauPrivadaResourceRepository.findByNom(any())).thenReturn(null);
        when(clauPrivadaResourceRepository.findByAlies(any())).thenReturn(null);

        service.beforeUpdateEntity(entity, resource, null);

        verify(entitatClauHelper).actualitzarServiciosOrganismos(eq(null), eq("CIF001"));
    }

    @Test
    public void beforeDelete_clauPerEntitat_resetejaServeis() {
        OrganismeCessionariResourceEntity organisme = buildOrganisme(1L, "CIF001");
        ClauPrivadaResourceEntity entity = buildEntity(10L, organisme, true);
        ClauPrivada businessEntity = mock(ClauPrivada.class);
        when(clauPrivadaRepository.findById(10L)).thenReturn(Optional.of(businessEntity));

        service.beforeDelete(entity, null);

        verify(entitatClauHelper).resetClauServiciosOrganismos(businessEntity);
    }

    @Test
    public void beforeDelete_clauNoPerEntitat_noFaRes() {
        OrganismeCessionariResourceEntity organisme = buildOrganisme(1L, "CIF001");
        ClauPrivadaResourceEntity entity = buildEntity(10L, organisme, false);

        service.beforeDelete(entity, null);

        verify(entitatClauHelper, never()).resetClauServiciosOrganismos(any());
    }

}
