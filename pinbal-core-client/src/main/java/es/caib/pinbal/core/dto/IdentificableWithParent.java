/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

/**
 * Interfície que han d'implementar tots els DTOs identificables amb pare.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface IdentificableWithParent<ID extends Serializable, PID extends Serializable> extends Identificable<ID> {

	public PID getParentId();

	public void setParentId(PID parentId);

}
