package es.caib.pinbal.persist.resourcerepository;

import es.caib.pinbal.persist.base.repository.BaseRepository;
import es.caib.pinbal.persist.resourceentity.ClauPublicaResourceEntity;

/**
 * Repositori del recurs {@link es.caib.pinbal.logic.intf.model.ClauPublicaResource}.
 *
 * @author Límit Tecnologies
 */
public interface ClauPublicaResourceRepository extends BaseRepository<ClauPublicaResourceEntity, Long> {

	ClauPublicaResourceEntity findByNom(String nom);

	ClauPublicaResourceEntity findByAlies(String alies);

}
