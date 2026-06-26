package es.caib.pinbal.back.resourcecontroller;

import es.caib.pinbal.back.base.controller.BaseReadonlyResourceController;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.model.ConsultaResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de consulta de consultes SCSP recents.
 * <p>
 * Una mateixa pantalla serveix per a administrador i delegat: l'àmbit de dades
 * es resol al servei segons el rol de l'usuari autenticat.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/consultes")
public class ConsultaResourceController extends BaseReadonlyResourceController<ConsultaResource, Long> {

}
