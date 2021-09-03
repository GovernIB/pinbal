/**
 * 
 */
package es.caib.pinbal.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.pinbal.core.dto.AvisDto;
import es.caib.pinbal.core.service.AvisService;

/**
 * Implementació de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AvisServiceBean implements AvisService {

	@Autowired
	AvisService delegate;

	@Override
	@RolesAllowed("PBL_ADMIN")
	public AvisDto create(AvisDto avis) {
		return delegate.create(avis);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public AvisDto update(AvisDto avis) {
		return delegate.update(avis);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public AvisDto updateActiva(Long id, boolean activa) {
		return delegate.updateActiva(id, activa);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public AvisDto delete(Long id) {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed("tothom")
	public AvisDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public Page<AvisDto> findPaginat(Pageable pageable) {
		return delegate.findPaginat(pageable);
	}

	@Override
	@RolesAllowed("tothom")
	public List<AvisDto> findActive() {
		return delegate.findActive();
	}

}
