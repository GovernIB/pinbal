package es.caib.pinbal.persist.resourcerepository;

import es.caib.pinbal.persist.base.repository.BaseRepository;
import es.caib.pinbal.persist.resourceentity.EntitatUsuariResourceEntity;
import org.springframework.data.jpa.repository.Query;

/**
 * Repositori per a la gestió d'entitats de tipus {@link EntitatUsuariResourceEntity}.
 *
 * @author Límit Tecnologies
 */
public interface EntitatUsuariResourceRepository extends BaseRepository<EntitatUsuariResourceEntity, Long> {

	@Query("select eu from EntitatUsuariResourceEntity eu where eu.entitat.id = ?1 and eu.usuari.id = ?2")
	EntitatUsuariResourceEntity findByEntitatIdAndUsuariCodi(Long entitatId, String usuariCodi);

}
