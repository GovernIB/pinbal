/**
 * 
 */
package es.caib.pinbal.core.ejb;

import es.caib.pinbal.client.comu.EntitatInfo;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatDto.EntitatTipusDto;
import es.caib.pinbal.core.dto.OrganGestorDto;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * Implementació de EntitatService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class EntitatServiceBean implements EntitatService {

	@Autowired
	EntitatService delegate;

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EntitatDto create(EntitatDto creada) {
		return delegate.create(creada);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EntitatDto delete(Long entitatId) throws EntitatNotFoundException {
		return delegate.delete(entitatId);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_SUPERAUD" })
	public List<EntitatDto> findAll() {
		return delegate.findAll();
	}

    @Override
	@RolesAllowed("PBL_WS")
    public List<EntitatInfo> getEntitatsInfo() {
        return delegate.getEntitatsInfo();
    }

    @Override
	@RolesAllowed("PBL_ADMIN")
	public Page<EntitatDto> findAmbFiltrePaginat(
			String codi,
			String nom,
			String cif,
			Boolean activa,
			String tipus,
			Pageable pageable, 
			String unitatArrel) {
		return delegate.findAmbFiltrePaginat(codi, nom, cif, activa, tipus, pageable, unitatArrel);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD", "tothom" })
	public EntitatDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EntitatDto findByCodi(String codi) {
		return delegate.findByCodi(codi);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EntitatDto findTopByTipus(EntitatTipusDto tipus) {
		return delegate.findTopByTipus(tipus);
	}
	
	@Override
	@RolesAllowed("PBL_ADMIN")
	public EntitatDto findByCif(String cif) {
		return delegate.findByCif(cif);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EntitatDto update(EntitatDto modificada) throws EntitatNotFoundException {
		return delegate.update(modificada);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EntitatDto updateActiva(Long id, boolean activa) throws EntitatNotFoundException {
		return delegate.updateActiva(id, activa);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void addServei(Long id, String serveiCodi) throws EntitatNotFoundException, ServeiNotFoundException {
		delegate.addServei(id, serveiCodi);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void removeServei(
			Long id,
			String serveiCodi) throws EntitatNotFoundException, EntitatServeiNotFoundException {
		delegate.removeServei(id, serveiCodi);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD", "tothom" })
	public List<EntitatDto> findActivesAmbUsuariCodi(String usuariCodi) {
		return delegate.findActivesAmbUsuariCodi(usuariCodi);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<EntitatDto> findDisponiblesPerRedireccionsBus(String serveiCodi) throws ServeiNotFoundException {
		return delegate.findDisponiblesPerRedireccionsBus(serveiCodi);
	}

    @Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD", "tothom" })
    public Long getEntitatIdPerDefecte(String usuari) {
        return delegate.getEntitatIdPerDefecte(usuari);
    }

    @Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_SUPERAUD" })
    public List<EntitatDto> findActives() {
        return delegate.findActives();
    }

    @Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES"})
	public List<OrganGestorDto> getOrgansGestors(Long id) {
		return delegate.getOrgansGestors(id);
	}

}
