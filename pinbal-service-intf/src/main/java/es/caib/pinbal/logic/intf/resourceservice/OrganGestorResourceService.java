package es.caib.pinbal.logic.intf.resourceservice;

import es.caib.pinbal.logic.intf.base.service.MutableResourceService;
import es.caib.pinbal.logic.intf.model.OrganGestorResource;

/**
 * Definició del servei de consulta d'òrgans gestors.
 * <p>
 * Els òrgans gestors no es creen/modifiquen/esborren manualment: es sincronitzen des de DIR3
 * (acció {@code syncDir3}, vegeu {@code @ResourceConfig} a {@link OrganGestorResource}).
 * {@code create}/{@code update}/{@code delete} no estan suportats.
 *
 * @author Límit Tecnologies
 */
public interface OrganGestorResourceService extends MutableResourceService<OrganGestorResource, Long> {

}
