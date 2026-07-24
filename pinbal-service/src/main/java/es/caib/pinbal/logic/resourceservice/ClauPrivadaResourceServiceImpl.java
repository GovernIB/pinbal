package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseMutableResourceService;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.pinbal.logic.intf.model.ClauPrivadaResource;
import es.caib.pinbal.logic.intf.resourceservice.ClauPrivadaResourceService;
import es.caib.pinbal.logic.service.EntitatClauHelper;
import es.caib.pinbal.persist.entity.ClauPrivada;
import es.caib.pinbal.persist.repository.ClauPrivadaRepository;
import es.caib.pinbal.persist.resourceentity.ClauPrivadaResourceEntity;
import es.caib.pinbal.persist.resourceentity.OrganismeCessionariResourceEntity;
import es.caib.pinbal.persist.resourcerepository.ClauPrivadaResourceRepository;
import es.caib.pinbal.persist.resourcerepository.OrganismeCessionariResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;

/**
 * Implementació del servei de manteniment de les claus privades SCSP.
 * <p>
 * Replica la lògica de negoci d'{@code ScspServiceImpl#createClauPrivada/updateClauPrivada/deleteClauPrivada}:
 * només pot existir una clau {@code perEntitat=true} per organisme (es desmarca l'antiga),
 * i cal sincronitzar els serveis que usen certificat d'entitat quan la clau és, deixa de ser,
 * o canvia de titular sent una clau "per entitat". La sincronització de la creació es fa
 * després del commit (com l'original), ja que depèn que la fila ja estigui persistida.
 *
 * @author Límit Tecnologies
 */
@Service
@RequiredArgsConstructor
public class ClauPrivadaResourceServiceImpl
        extends BaseMutableResourceService<ClauPrivadaResource, Long, ClauPrivadaResourceEntity>
        implements ClauPrivadaResourceService {

    private final ClauPrivadaResourceRepository clauPrivadaResourceRepository;
    private final OrganismeCessionariResourceRepository organismeCessionariResourceRepository;
    private final ClauPrivadaRepository clauPrivadaRepository;
    private final EntitatClauHelper entitatClauHelper;

    @Override
    protected ClauPrivadaResource entityToResource(ClauPrivadaResourceEntity entity) {
        ClauPrivadaResource resource = super.entityToResource(entity);
        resource.setCaducada(entity.getDataBaixa() != null && new java.util.Date().after(entity.getDataBaixa()));
        return resource;
    }

    @Override
    protected void beforeCreateEntity(
            ClauPrivadaResourceEntity entity,
            ClauPrivadaResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers) {
        checkNoRepetida(resource, null);
        desmarcaClauAntigaPerEntitat(null, resource.isPerEntitat(), entity.getOrganisme().getCif());
    }

    @Override
    protected void afterCreateSave(
            ClauPrivadaResourceEntity entity,
            ClauPrivadaResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers,
            boolean anyOrderChanged) {
        if (resource.isPerEntitat()) {
            String alies = entity.getAlies();
            String organismeCif = entity.getOrganisme().getCif();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    try {
                        entitatClauHelper.sincronitzarAmbServeis(alies, organismeCif);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    }

    @Override
    protected void beforeUpdateEntity(
            ClauPrivadaResourceEntity entity,
            ClauPrivadaResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers) {
        checkNoRepetida(resource, entity.getId());
        boolean canviaPerEntitat = entity.isPerEntitat() != resource.isPerEntitat();
        String organismeCifOrigen = entity.getOrganisme().getCif();
        Long novaOrganismeId = resource.getOrganisme().getId();
        boolean canviaOrganisme = !entity.getOrganisme().getId().equals(novaOrganismeId);
        OrganismeCessionariResourceEntity organisme = organismeCessionariResourceRepository.findById(novaOrganismeId).orElseThrow();

        desmarcaClauAntigaPerEntitat(entity.getId(), resource.isPerEntitat(), organisme.getCif());

        if ((canviaOrganisme && canviaPerEntitat) || resource.isPerEntitat()) {
            entitatClauHelper.actualitzarServiciosOrganismos(
                    resource.isPerEntitat() ? fetchBusinessClauPrivada(entity.getId()) : null,
                    organisme.getCif());
        } else if (canviaPerEntitat) {
            entitatClauHelper.actualitzarServiciosOrganismos(null, organismeCifOrigen);
        }
    }

    @Override
    protected void beforeDelete(
            ClauPrivadaResourceEntity entity,
            Map<String, AnswerRequiredException.AnswerValue> answers) {
        if (entity.isPerEntitat()) {
            entitatClauHelper.resetClauServiciosOrganismos(fetchBusinessClauPrivada(entity.getId()));
        }
    }

    private void desmarcaClauAntigaPerEntitat(Long currentId, boolean novaClauPerEntitat, String organismeCif) {
        if (!novaClauPerEntitat) {
            return;
        }
        ClauPrivadaResourceEntity antiga = clauPrivadaResourceRepository.findTopByOrganismeCifAndPerEntitatTrueOrderByDataAltaDesc(organismeCif);
        if (antiga != null && !antiga.getId().equals(currentId)) {
            antiga.setPerEntitat(false);
            clauPrivadaResourceRepository.save(antiga);
        }
    }

    private ClauPrivada fetchBusinessClauPrivada(Long id) {
        return clauPrivadaRepository.findById(id).orElseThrow();
    }

    private void checkNoRepetida(ClauPrivadaResource resource, Long currentId) {
        ClauPrivadaResourceEntity existingNom = clauPrivadaResourceRepository.findByNom(resource.getNom());
        if (existingNom != null && !existingNom.getId().equals(currentId)) {
            throwNotSaved(currentId, "Ja existeix una clau privada amb aquest nom");
        }
        ClauPrivadaResourceEntity existingAlies = clauPrivadaResourceRepository.findByAlies(resource.getAlies());
        if (existingAlies != null && !existingAlies.getId().equals(currentId)) {
            throwNotSaved(currentId, "Ja existeix una clau privada amb aquest àlies");
        }
    }

    private void throwNotSaved(Long currentId, String reason) {
        if (currentId == null) {
            throw new ResourceNotCreatedException(ClauPrivadaResource.class, reason);
        }
        throw new ResourceNotUpdatedException(ClauPrivadaResource.class, String.valueOf(currentId), reason);
    }

}
