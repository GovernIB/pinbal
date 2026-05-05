/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import java.io.Serializable;

/**
 * Interfície que han d'implementar tots els DTOs identificables.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface Identificable<ID extends Serializable> {

	public ID getId();

	public String getDefaultIdentifier();

}
