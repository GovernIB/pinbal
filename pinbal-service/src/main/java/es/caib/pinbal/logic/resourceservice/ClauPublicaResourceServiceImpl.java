package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseMutableResourceService;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.pinbal.logic.intf.model.ClauPublicaResource;
import es.caib.pinbal.logic.intf.resourceservice.ClauPublicaResourceService;
import es.caib.pinbal.persist.resourceentity.ClauPublicaResourceEntity;
import es.caib.pinbal.persist.resourcerepository.ClauPublicaResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de manteniment de les claus públiques SCSP.
 * <p>
 * Replica la validació d'unicitat de {@code nom} i {@code alies} (JSP: {@code @ClauPublicaNoRepetida}),
 * que el PK autogenerat no garanteix per si sol.
 *
 * @author Límit Tecnologies
 */
@Service
@RequiredArgsConstructor
public class ClauPublicaResourceServiceImpl
        extends BaseMutableResourceService<ClauPublicaResource, Long, ClauPublicaResourceEntity>
        implements ClauPublicaResourceService {

    private final ClauPublicaResourceRepository clauPublicaResourceRepository;

    @Override
    protected void beforeCreateEntity(
            ClauPublicaResourceEntity entity,
            ClauPublicaResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers) {
        checkNoRepetida(resource, null);
    }

    @Override
    protected void beforeUpdateEntity(
            ClauPublicaResourceEntity entity,
            ClauPublicaResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers) {
        checkNoRepetida(resource, entity.getId());
    }

    private void checkNoRepetida(ClauPublicaResource resource, Long currentId) {
        ClauPublicaResourceEntity existingNom = clauPublicaResourceRepository.findByNom(resource.getNom());
        if (existingNom != null && !existingNom.getId().equals(currentId)) {
            throwNotSaved(currentId, "Ja existeix una clau pública amb aquest nom");
        }
        ClauPublicaResourceEntity existingAlies = clauPublicaResourceRepository.findByAlies(resource.getAlies());
        if (existingAlies != null && !existingAlies.getId().equals(currentId)) {
            throwNotSaved(currentId, "Ja existeix una clau pública amb aquest àlies");
        }
    }

    private void throwNotSaved(Long currentId, String reason) {
        if (currentId == null) {
            throw new ResourceNotCreatedException(ClauPublicaResource.class, reason);
        }
        throw new ResourceNotUpdatedException(ClauPublicaResource.class, String.valueOf(currentId), reason);
    }

}
