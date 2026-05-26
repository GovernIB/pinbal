/**
 * 
 */
package es.caib.pinbal.ejb;

import es.caib.pinbal.logic.intf.dto.*;
import es.caib.pinbal.logic.intf.service.exception.*;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de ConsultaService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class ScspServiceBean extends AbstractService<es.caib.pinbal.logic.intf.service.ScspService> implements es.caib.pinbal.logic.intf.service.ScspService {

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ParamConfDto findParamConfByNom(String nom) {
		return getDelegateService().findParamConfByNom(nom);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ParamConfDto createParamConf(ParamConfDto dto) {
		return getDelegateService().createParamConf(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ParamConfDto updateParamConf(ParamConfDto dto) throws ParamConfNotFoundException {
		return getDelegateService().updateParamConf(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ParamConfDto deleteParamConf(String nom) throws ParamConfNotFoundException {
		return getDelegateService().deleteParamConf(nom);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public Page<ParamConfDto> findAllParamConf(Pageable pageable) {
		return getDelegateService().findAllParamConf(pageable);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EmissorCertDto findEmissorCertById(Long id) {
		return getDelegateService().findEmissorCertById(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EmissorCertDto findEmissorCertByCif(String cif) {
		return getDelegateService().findEmissorCertByCif(cif);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EmissorCertDto createEmissorCert(EmissorCertDto dto) {
		return getDelegateService().createEmissorCert(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EmissorCertDto updateEmissorCert(EmissorCertDto dto) throws EmissorCertNotFoundException {
		return getDelegateService().updateEmissorCert(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EmissorCertDto deleteEmissorCert(Long id) throws EmissorCertNotFoundException {
		return getDelegateService().deleteEmissorCert(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public Page<EmissorCertDto> findAllEmissorCert(Pageable pageable) {
		return getDelegateService().findAllEmissorCert(pageable);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPrivadaDto findClauPrivadaById(Long id) {
		return getDelegateService().findClauPrivadaById(id);
	}

    @Override
	@RolesAllowed("PBL_ADMIN")
    public ClauPrivadaDto findClauPrivadaByNom(String nom) {
        return getDelegateService().findClauPrivadaByNom(nom);
    }

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPrivadaDto findClauPrivadaByAlies(String alies) {
		return getDelegateService().findClauPrivadaByAlies(alies);
	}

    @Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPrivadaDto createClauPrivada(ClauPrivadaDto dto) throws EntitatNotFoundException {
		return getDelegateService().createClauPrivada(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPrivadaDto updateClauPrivada(ClauPrivadaDto dto) throws ClauPrivadaNotFoundException, EntitatNotFoundException {
		return getDelegateService().updateClauPrivada(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPrivadaDto deleteClauPrivada(Long id) throws ClauPrivadaNotFoundException {
		return getDelegateService().deleteClauPrivada(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public Page<ClauPrivadaDto> findAllClauPrivada(Pageable pageable) {
		return getDelegateService().findAllClauPrivada(pageable);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<OrganismeCessionariDto> findAllOrganismeCessionari() {
		return getDelegateService().findAllOrganismeCessionari();
	}

    @Override
    @RolesAllowed("PBL_ADMIN")
    public List<OrganismeCessionariDto> findAllOrganismeCessionariActiu() {
        return getDelegateService().findAllOrganismeCessionariActiu();
    }

    @Override
    @RolesAllowed("PBL_ADMIN")
    public OrganismeCessionariDto findOrganismeCessionariById(Long organismeId) {
        return getDelegateService().findOrganismeCessionariById(organismeId);
    }

    @Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPublicaDto findClauPublicaById(Long id) {
		return getDelegateService().findClauPublicaById(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPublicaDto findClauPublicaByNom(String nom) {
		return getDelegateService().findClauPublicaByNom(nom);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPublicaDto findClauPublicaByAlies(String alies) {
		return getDelegateService().findClauPublicaByAlies(alies);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPublicaDto createClauPublica(ClauPublicaDto dto) {
		return getDelegateService().createClauPublica(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPublicaDto updateClauPublica(ClauPublicaDto dto) throws ClauPublicaNotFoundException {
		return getDelegateService().updateClauPublica(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPublicaDto deleteClauPublica(Long id) throws ClauPublicaNotFoundException {
		return getDelegateService().deleteClauPublica(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public Page<ClauPublicaDto> findAllClauPublica(Pageable pageable) {
		return getDelegateService().findAllClauPublica(pageable);
	}

}
