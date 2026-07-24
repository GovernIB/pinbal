package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseReadonlyResourceService;
import es.caib.pinbal.logic.intf.model.OrganismeCessionariResource;
import es.caib.pinbal.logic.intf.resourceservice.OrganismeCessionariResourceService;
import es.caib.pinbal.persist.resourceentity.OrganismeCessionariResourceEntity;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de només lectura dels organismes cessionaris SCSP.
 *
 * @author Límit Tecnologies
 */
@Service
public class OrganismeCessionariResourceServiceImpl
        extends BaseReadonlyResourceService<OrganismeCessionariResource, Long, OrganismeCessionariResourceEntity>
        implements OrganismeCessionariResourceService {

}
