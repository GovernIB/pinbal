package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseMutableResourceService;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.model.AvisResource;
import es.caib.pinbal.logic.intf.resourceservice.AvisResourceService;
import es.caib.pinbal.persist.resourceentity.AvisResourceEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de manteniment dels avisos.
 *
 * @author Límit Tecnologies
 */
@Service
public class AvisResourceServiceImpl
        extends BaseMutableResourceService<AvisResource, Long, AvisResourceEntity>
        implements AvisResourceService {

    @Override
    protected void beforeCreateSave(
            AvisResourceEntity entity,
            AvisResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers) {
        // Un avís es crea sempre actiu (JSP: avisForm.jsp no exposa aquest camp a la creació);
        // s'ha de fixar després del mapeig genèric (beforeCreateEntity s'executa abans).
        entity.setActiu(true);
    }

}
