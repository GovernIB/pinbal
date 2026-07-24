package es.caib.pinbal.back.resourcecontroller;

import es.caib.pinbal.back.base.controller.BaseMutableResourceController;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.model.CacheResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de consulta i buidatge de les caches de l'aplicació.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/caches")
public class CacheResourceController extends BaseMutableResourceController<CacheResource, String> {

}
