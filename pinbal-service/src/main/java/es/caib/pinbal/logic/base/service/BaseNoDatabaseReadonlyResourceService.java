package es.caib.pinbal.logic.base.service;

import es.caib.pinbal.logic.intf.base.model.Resource;
import es.caib.pinbal.persist.base.entity.NoDatabaseResourceEntity;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * Servei amb la funcionalitat básica per a la gestió d'un recurs que no s'emmagatzema a base de daddes i que només es
 * pot consultar.
 *
 * @param <R> classe del recurs.
 * @param <ID> classe de la clau primària del recurs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
public abstract class BaseNoDatabaseReadonlyResourceService<R extends Resource<ID>, ID extends Serializable>
		extends BaseReadonlyResourceService<R, ID, NoDatabaseResourceEntity<R, ID>> {

	@Override
	protected boolean isEntityRepositoryOptional() {
		return true;
	}

	@Override
	protected R entityToResource(NoDatabaseResourceEntity<R, ID> entity) {
		R resource = entity.getResource();
		resource.setId(entity.getId());
		return resource;
	}

}
