package es.caib.pinbal.logic.intf.base.model;

import es.caib.pinbal.logic.intf.base.util.TypeUtil;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Referència genèrica cap a una altra entitat del model.
 * 
 * @author Límit Tecnologies
 */
@Getter
@Setter
public class ResourceReference<R extends Resource<ID>, ID extends Serializable> implements Resource<ID> {

	@NotNull
	protected ID id;
	protected String description;

	public static <R extends Resource<ID>, ID extends Serializable> ResourceReference<R, ID> toResourceReference(R resource) {
		return toResourceReference(resource.getId(), null);
	}
	public static <R extends Resource<ID>, ID extends Serializable> ResourceReference<R, ID> toResourceReference(
			R resource,
			String description) {
		return toResourceReference(resource.getId(), description);
	}
	public static <R extends Resource<ID>, ID extends Serializable> ResourceReference<R, ID> toResourceReference(ID id) {
		return toResourceReference(id, null);
	}
	public static <R extends Resource<ID>, ID extends Serializable> ResourceReference<R, ID> toResourceReference(
			ID id,
			String description) {
		ResourceReference<R, ID> genericReference = new ResourceReference<R, ID>();
		genericReference.setId(id);
		genericReference.setDescription(description);
		return genericReference;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceReference other = (ResourceReference) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		Class<R> resourceClass = null;
		try {
			resourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(getClass(), 0);
		} catch (Exception ignored) {
		}
		return "Referencia al recurs " + (resourceClass != null ? resourceClass.getName() : "<unknown>") +
				" (id=" + id + ", " +
				"description=" + description + ")";
	}

}
