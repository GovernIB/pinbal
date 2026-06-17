package es.caib.pinbal.ejb;

import es.caib.pinbal.logic.intf.dto.OrganGestorDto;
import es.caib.pinbal.logic.intf.dto.OrganGestorEstatEnum;
import es.caib.pinbal.logic.intf.dto.PaginacioAmbOrdreDto;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;


/**
 * Implementació de OrganGestorService com a EJB que empra una clase delegada
 * per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class OrganGestorService extends AbstractService<es.caib.pinbal.logic.intf.service.OrganGestorService> implements es.caib.pinbal.logic.intf.service.OrganGestorService {

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES" })
	public List<OrganGestorDto> findAll() {
		return getDelegateService().findAll();
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES" })
	public OrganGestorDto findItem(Long id) {
		return getDelegateService().findItem(id);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES" })
	public List<OrganGestorDto> findByEntitat(Long entitatId) {
		return getDelegateService().findByEntitat(entitatId);
	}

    @Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES" })
    public List<OrganGestorDto> findActivesByEntitat(Long entitatId) {
        return getDelegateService().findActivesByEntitat(entitatId);
    }

    @Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES" })
	public List<OrganGestorDto> findByEntitatAmbFiltre(Long entitatId, String filtre) {
		return getDelegateService().findByEntitatAmbFiltre(entitatId,filtre);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES" })
	public Page<OrganGestorDto> findPageOrgansGestorsAmbFiltrePaginat(
			Long entitatId,
			String filtreCodi,
			String filtreNom,
			String pareCodi,
			OrganGestorEstatEnum filtreEstat,
			PaginacioAmbOrdreDto paginacioDto) {
		return getDelegateService().findPageOrgansGestorsAmbFiltrePaginat(entitatId, filtreCodi, filtreNom, pareCodi, filtreEstat, paginacioDto);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES" })
	public boolean syncDir3OrgansGestors(Long entitatId) throws Exception {
		return getDelegateService().syncDir3OrgansGestors(entitatId);
	}

}
