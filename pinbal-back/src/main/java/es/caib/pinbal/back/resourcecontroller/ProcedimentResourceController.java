package es.caib.pinbal.back.resourcecontroller;

import es.caib.pinbal.back.base.controller.BaseMutableResourceController;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.model.ProcedimentResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de consulta i modificació de procediments administratius.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/procediments")
public class ProcedimentResourceController extends BaseMutableResourceController<ProcedimentResource, Long> {

}
