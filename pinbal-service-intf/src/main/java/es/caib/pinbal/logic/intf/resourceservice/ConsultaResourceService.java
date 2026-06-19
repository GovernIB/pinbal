package es.caib.pinbal.logic.intf.resourceservice;

import es.caib.pinbal.logic.intf.base.service.ReadonlyResourceService;
import es.caib.pinbal.logic.intf.model.ConsultaResource;

/**
 * Definició del servei de consulta de consultes SCSP recents.
 * <p>
 * L'àmbit de dades el determina el rol de l'usuari autenticat.
 *
 * @author Límit Tecnologies
 */
public interface ConsultaResourceService extends ReadonlyResourceService<ConsultaResource, Long> {

}
