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
	@RolesAllowed("PBL_ADMIN")
	public List<OrganGestorDto> findAll() {
		return delegate.findAll();
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public OrganGestorDto findItem(Long id) {
		return delegate.findItem(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<OrganGestorDto> findByEntitat(Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public Page<OrganGestorDto> findPageOrgansGestorsAmbFiltrePaginat(
			Long entitatId,
			String filtre,
			Pageable pageable) {
		return delegate.findPageOrgansGestorsAmbFiltrePaginat(entitatId, filtre, pageable);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public boolean syncDir3OrgansGestors(Long entitatId) throws Exception {
		return delegate.syncDir3OrgansGestors(entitatId);
	}

//	@Override
//	@RolesAllowed("IPA_ADMIN")
//	public List<PermisOrganGestorDto> findPermisos(Long entitatId) throws NotFoundException {
//		return delegate.findPermisos(entitatId);
//	}
//
//	@Override
//	@RolesAllowed("IPA_ADMIN")
//	public void updatePermis(Long id, PermisDto permis, Long entitatId) throws NotFoundException {
//		delegate.updatePermis(id, permis, entitatId);
//	}
//
//	@Override
//	@RolesAllowed("IPA_ADMIN")
//	public void deletePermis(Long id, Long permisId, Long entitatId) throws NotFoundException {
//		delegate.deletePermis(id, permisId, entitatId);
//	}
//
//	@Override
//	@RolesAllowed("tothom")
//	public List<OrganGestorDto> findAccessiblesUsuariActual(Long entitatId) {
//		return delegate.findAccessiblesUsuariActual(entitatId);
//	}

}
