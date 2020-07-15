/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

/**
 * Implementació per defecte de la interfície Identificable.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AbstractIdentificable<ID extends Serializable> implements Identificable<ID> {

	private ID id;

	@Override
	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

	public String getDefaultIdentifier() {
		return this.getClass().getSimpleName() + "#" + id;
	}

}
