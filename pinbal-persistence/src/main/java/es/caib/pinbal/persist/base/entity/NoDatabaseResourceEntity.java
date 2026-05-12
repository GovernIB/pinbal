package es.caib.pinbal.persist.base.entity;

import es.caib.pinbal.logic.intf.base.model.Resource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Implementació de ResourceEntity pels serveis que no accedeixen a base de dades.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
public class NoDatabaseResourceEntity<R extends Resource<?>, PK extends Serializable> extends BaseResourceEntity<R, PK> {

	private PK id;
	private R resource;

	@Builder
	public NoDatabaseResourceEntity(
		PK id,
		R resource) {
		this.id = id;
		this.resource = resource;
	}

}
