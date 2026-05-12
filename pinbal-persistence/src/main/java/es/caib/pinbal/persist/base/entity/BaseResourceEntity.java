package es.caib.pinbal.persist.base.entity;

import es.caib.pinbal.logic.intf.base.model.Resource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;

/**
 * Base per a definir les demés entitats de l'aplicació.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseResourceEntity<R extends Resource<?>, PK extends Serializable>
		implements ResourceEntity<R, PK> {

	@Nullable
	@Override
	public abstract PK getId();

	@Override
	public boolean isNew() {
		return null == getId();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BaseResourceEntity<?, ?> that = (BaseResourceEntity<?, ?>)o;
		return Objects.equals(getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}

	@Override
	public String toString() {
		String idPart = isNew() ? "<new>" : "(id=" + getId() + ")";
		return getClass().getSimpleName() + idPart;
	}

}