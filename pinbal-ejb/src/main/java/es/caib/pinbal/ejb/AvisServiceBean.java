/**
 * 
 */
package es.caib.pinbal.ejb;

import es.caib.pinbal.logic.intf.dto.AvisDto;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class AvisServiceBean extends AbstractService<es.caib.pinbal.logic.intf.service.AvisService> implements es.caib.pinbal.logic.intf.service.AvisService {

	@Override
	@RolesAllowed("PBL_ADMIN")
	public AvisDto create(AvisDto avis) {
		return getDelegateService().create(avis);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public AvisDto update(AvisDto avis) {
		return getDelegateService().update(avis);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public AvisDto updateActiva(Long id, boolean activa) {
		return getDelegateService().updateActiva(id, activa);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public AvisDto delete(Long id) {
		return getDelegateService().delete(id);
	}

	@Override
	@RolesAllowed("**")
	public AvisDto findById(Long id) {
		return getDelegateService().findById(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public Page<AvisDto> findPaginat(Pageable pageable) {
		return getDelegateService().findPaginat(pageable);
	}

	@Override
	@RolesAllowed("**")
	public List<AvisDto> findActive() {
		return getDelegateService().findActive();
	}

    @Override
	@RolesAllowed("PBL_ADMIN")
    public List<Long> findAllIds() {
        return getDelegateService().findAllIds();
    }

}
