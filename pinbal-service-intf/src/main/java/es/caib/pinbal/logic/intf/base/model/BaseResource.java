package es.caib.pinbal.logic.intf.base.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Implementació base de la interfície de recurs.
 *
 * @param <ID> el tipus de la clau primària del recurs.
 */
public class BaseResource<ID extends Serializable> implements Resource<ID> {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected ID id;

	@Override
	public ID getId() {
		return id;
	}

	@Override
	public void setId(ID id) {
		this.id = id;
	}

}
