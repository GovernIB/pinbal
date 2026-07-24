package es.caib.pinbal.logic.intf.resourceservice;

import es.caib.pinbal.logic.intf.base.service.MutableResourceService;
import es.caib.pinbal.logic.intf.model.ServeiResource;

/**
 * Definició del servei de consulta i modificació dels serveis SCSP.
 * <p>
 * Només permet modificar el subconjunt de configuració pròpia de PINBAL (rol, tipus de
 * document del titular, entitat proveïdora, actiu...) sobre serveis ja existents: la
 * configuració SCSP completa (URLs, seguretat, claus, XSD, camps específics...) es continua
 * gestionant des del manteniment JSP.
 *
 * @author Límit Tecnologies
 */
public interface ServeiResourceService extends MutableResourceService<ServeiResource, Long> {

}
