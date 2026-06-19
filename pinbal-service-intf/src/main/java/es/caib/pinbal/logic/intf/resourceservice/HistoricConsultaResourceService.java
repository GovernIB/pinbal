package es.caib.pinbal.logic.intf.resourceservice;

import es.caib.pinbal.logic.intf.base.service.ReadonlyResourceService;
import es.caib.pinbal.logic.intf.model.HistoricConsultaResource;

/**
 * Definició del servei de consulta de consultes SCSP històriques (arxivades).
 * <p>
 * L'àmbit de dades el determina el rol de l'usuari autenticat.
 *
 * @author Límit Tecnologies
 */
public interface HistoricConsultaResourceService extends ReadonlyResourceService<HistoricConsultaResource, Long> {

}
