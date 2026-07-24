package es.caib.pinbal.back.resourcecontroller;

import es.caib.pinbal.back.base.controller.BaseMutableResourceController;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.model.OrganGestorResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de consulta d'òrgans gestors i de sincronització amb DIR3 (acció "syncDir3").
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/organGestors")
public class OrganGestorResourceController extends BaseMutableResourceController<OrganGestorResource, Long> {

}
