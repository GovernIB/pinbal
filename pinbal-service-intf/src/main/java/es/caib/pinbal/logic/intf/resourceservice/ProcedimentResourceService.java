package es.caib.pinbal.logic.intf.resourceservice;

import es.caib.pinbal.logic.intf.base.service.MutableResourceService;
import es.caib.pinbal.logic.intf.model.ProcedimentResource;

/**
 * Definició del servei de consulta i modificació de procediments administratius.
 * <p>
 * Només cobreix les dades bàsiques del procediment. L'assignació de serveis, la graella de
 * permisos per servei/usuari, el clonatge i l'assistent de migració de serveis es continuen
 * gestionant des del manteniment JSP.
 *
 * @author Límit Tecnologies
 */
public interface ProcedimentResourceService extends MutableResourceService<ProcedimentResource, Long> {

}
