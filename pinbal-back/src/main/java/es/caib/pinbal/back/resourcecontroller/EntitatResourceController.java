package es.caib.pinbal.back.resourcecontroller;

import es.caib.pinbal.back.base.controller.BaseMutableResourceController;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.model.EntitatResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de consulta i modificació d'entitats.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/entitats")
public class EntitatResourceController extends BaseMutableResourceController<EntitatResource, Long> {

}
