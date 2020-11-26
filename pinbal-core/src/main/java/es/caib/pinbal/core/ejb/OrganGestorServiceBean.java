package es.caib.pinbal.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.pinbal.core.dto.OrganGestorDto;
import es.caib.pinbal.core.service.OrganGestorService;


/**
 * Implementació de OrganGestorService com a EJB que empra una clase delegada
 * per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class OrganGestorServiceBean implements OrganGestorService {

	@Autowired
	OrganGestorService delegate;

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES" })
	public List<OrganGestorDto> findAll() {
		return delegate.findAll();
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES" })
	public OrganGestorDto findItem(Long id) {
		return delegate.findItem(id);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES" })
	public List<OrganGestorDto> findByEntitat(Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES" })
	public Page<OrganGestorDto> findPageOrgansGestorsAmbFiltrePaginat(
			Long entitatId,
			String filtre,
			Pageable pageable) {
		return delegate.findPageOrgansGestorsAmbFiltrePaginat(entitatId, filtre, pageable);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES" })
	public boolean syncDir3OrgansGestors(Long entitatId) throws Exception {
		return delegate.syncDir3OrgansGestors(entitatId);
	}

}
