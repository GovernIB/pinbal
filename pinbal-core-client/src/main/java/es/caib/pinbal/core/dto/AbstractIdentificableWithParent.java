/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

/**
 * Implementació per defecte de la interfície IdentificableWithParent.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AbstractIdentificableWithParent<ID extends Serializable, PID extends Serializable>
      extends AbstractIdentificable<ID> implements IdentificableWithParent<ID, PID> {

	private PID parentId;

	@Override
	public PID getParentId() {
		return parentId;
	}

	@Override
	public void setParentId(PID parentId) {
		this.parentId = parentId;
	}

}
