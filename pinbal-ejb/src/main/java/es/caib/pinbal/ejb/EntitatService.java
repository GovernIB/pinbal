/**
 * 
 */
package es.caib.pinbal.ejb;

import es.caib.pinbal.client.comu.EntitatInfo;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto.EntitatTipusDto;
import es.caib.pinbal.logic.intf.dto.OrganGestorDto;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatServeiNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ServeiNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

/**
 * Implementació de EntitatService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Primary
@Stateless
public class EntitatService extends AbstractService<es.caib.pinbal.logic.intf.service.EntitatService> implements es.caib.pinbal.logic.intf.service.EntitatService {

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EntitatDto create(EntitatDto creada) {
		return getDelegateService().create(creada);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EntitatDto delete(Long entitatId) throws EntitatNotFoundException {
		return getDelegateService().delete(entitatId);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_SUPERAUD" })
	public List<EntitatDto> findAll() {
		return getDelegateService().findAll();
	}

    @Override
//	@RolesAllowed("**")
	@RolesAllowed("PBL_WS")
    public List<EntitatInfo> getEntitatsInfo() {
        return getDelegateService().getEntitatsInfo();
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
		return getDelegateService().findAmbFiltrePaginat(codi, nom, cif, activa, tipus, pageable, unitatArrel);
	}

	@Override
	@RolesAllowed("**")
	public EntitatDto findById(Long id) {
		return getDelegateService().findById(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EntitatDto findByCodi(String codi) {
		return getDelegateService().findByCodi(codi);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EntitatDto findTopByTipus(EntitatTipusDto tipus) {
		return getDelegateService().findTopByTipus(tipus);
	}
	
	@Override
	@RolesAllowed("PBL_ADMIN")
	public EntitatDto findByCif(String cif) {
		return getDelegateService().findByCif(cif);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EntitatDto update(EntitatDto modificada) throws EntitatNotFoundException {
		return getDelegateService().update(modificada);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EntitatDto updateActiva(Long id, boolean activa) throws EntitatNotFoundException {
		return getDelegateService().updateActiva(id, activa);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void addServei(Long id, String serveiCodi) throws EntitatNotFoundException, ServeiNotFoundException {
		getDelegateService().addServei(id, serveiCodi);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void removeServei(
			Long id,
			String serveiCodi) throws EntitatNotFoundException, EntitatServeiNotFoundException {
		getDelegateService().removeServei(id, serveiCodi);
	}

	@Override
	@RolesAllowed("**")
	public List<EntitatDto> findActivesAmbUsuariCodi(String usuariCodi) {
		return getDelegateService().findActivesAmbUsuariCodi(usuariCodi);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<EntitatDto> findDisponiblesPerRedireccionsBus(String serveiCodi) throws ServeiNotFoundException {
		return getDelegateService().findDisponiblesPerRedireccionsBus(serveiCodi);
	}

    @Override
	@RolesAllowed("**")
    public Long getEntitatIdPerDefecte(String usuari) {
        return getDelegateService().getEntitatIdPerDefecte(usuari);
    }

    @Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_SUPERAUD" })
    public List<EntitatDto> findActives() {
        return getDelegateService().findActives();
    }

    @Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES"})
	public List<OrganGestorDto> getOrgansGestors(Long id) {
		return getDelegateService().getOrgansGestors(id);
	}

}
