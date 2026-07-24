package es.caib.pinbal.back.resourcecontroller;

import es.caib.pinbal.back.base.controller.BaseReadonlyResourceController;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.model.OrganismeCessionariResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de només lectura dels organismes cessionaris SCSP.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/organismescessionaris")
public class OrganismeCessionariResourceController extends BaseReadonlyResourceController<OrganismeCessionariResource, Long> {

}
