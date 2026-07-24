package es.caib.pinbal.persist.resourcerepository;

import es.caib.pinbal.persist.base.repository.BaseRepository;
import es.caib.pinbal.persist.resourceentity.EmissorCertResourceEntity;

/**
 * Repositori del recurs {@link es.caib.pinbal.logic.intf.model.EmissorCertResource}.
 *
 * @author Límit Tecnologies
 */
public interface EmissorCertResourceRepository extends BaseRepository<EmissorCertResourceEntity, Long> {

	EmissorCertResourceEntity findByCif(String cif);

}
