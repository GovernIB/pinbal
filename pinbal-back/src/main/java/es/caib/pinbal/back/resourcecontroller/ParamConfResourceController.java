package es.caib.pinbal.back.resourcecontroller;

import es.caib.pinbal.back.base.controller.BaseMutableResourceController;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.model.ParamConfResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de manteniment dels paràmetres de configuració SCSP.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/paramconfs")
public class ParamConfResourceController extends BaseMutableResourceController<ParamConfResource, String> {

}
