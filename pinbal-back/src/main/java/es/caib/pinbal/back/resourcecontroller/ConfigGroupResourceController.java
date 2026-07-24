package es.caib.pinbal.back.resourcecontroller;

import es.caib.pinbal.back.base.controller.BaseReadonlyResourceController;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.model.ConfigGroupResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de només lectura dels grups de propietats de configuració.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/configgroups")
public class ConfigGroupResourceController extends BaseReadonlyResourceController<ConfigGroupResource, String> {

}
