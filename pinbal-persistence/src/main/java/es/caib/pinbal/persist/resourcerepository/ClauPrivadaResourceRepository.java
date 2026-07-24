package es.caib.pinbal.persist.resourcerepository;

import es.caib.pinbal.persist.base.repository.BaseRepository;
import es.caib.pinbal.persist.resourceentity.ClauPrivadaResourceEntity;

/**
 * Repositori del recurs {@link es.caib.pinbal.logic.intf.model.ClauPrivadaResource}.
 *
 * @author Límit Tecnologies
 */
public interface ClauPrivadaResourceRepository extends BaseRepository<ClauPrivadaResourceEntity, Long> {

	ClauPrivadaResourceEntity findByNom(String nom);

	ClauPrivadaResourceEntity findByAlies(String alies);

	ClauPrivadaResourceEntity findTopByOrganismeCifAndPerEntitatTrueOrderByDataAltaDesc(String organismeCif);

}
