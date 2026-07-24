package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseMutableResourceService;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.pinbal.logic.intf.model.EmissorCertResource;
import es.caib.pinbal.logic.intf.resourceservice.EmissorCertResourceService;
import es.caib.pinbal.persist.resourceentity.EmissorCertResourceEntity;
import es.caib.pinbal.persist.resourcerepository.EmissorCertResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de manteniment dels emissors de certificats SCSP.
 * <p>
 * Replica la validació d'unicitat del CIF (JSP: {@code @CifEmisorNoRepetit}), que el PK
 * autogenerat no garanteix per si sol.
 *
 * @author Límit Tecnologies
 */
@Service
@RequiredArgsConstructor
public class EmissorCertResourceServiceImpl
        extends BaseMutableResourceService<EmissorCertResource, Long, EmissorCertResourceEntity>
        implements EmissorCertResourceService {

    private final EmissorCertResourceRepository emissorCertResourceRepository;

    @Override
    protected void beforeCreateEntity(
            EmissorCertResourceEntity entity,
            EmissorCertResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers) {
        checkCifNoRepetit(resource, null);
    }

    @Override
    protected void beforeUpdateEntity(
            EmissorCertResourceEntity entity,
            EmissorCertResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers) {
        checkCifNoRepetit(resource, entity.getId());
    }

    private void checkCifNoRepetit(EmissorCertResource resource, Long currentId) {
        EmissorCertResourceEntity existing = emissorCertResourceRepository.findByCif(resource.getCif());
        if (existing != null && !existing.getId().equals(currentId)) {
            if (currentId == null) {
                throw new ResourceNotCreatedException(EmissorCertResource.class, "Ja existeix un emissor de certificat amb aquest CIF");
            }
            throw new ResourceNotUpdatedException(EmissorCertResource.class, String.valueOf(currentId), "Ja existeix un emissor de certificat amb aquest CIF");
        }
    }

}
