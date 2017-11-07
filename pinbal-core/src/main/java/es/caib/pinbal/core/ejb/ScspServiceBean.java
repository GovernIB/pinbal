/**
 * 
 */
package es.caib.pinbal.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.pinbal.core.dto.ClauPrivadaDto;
import es.caib.pinbal.core.dto.ClauPublicaDto;
import es.caib.pinbal.core.dto.EmissorCertDto;
import es.caib.pinbal.core.dto.OrganismeCessionariDto;
import es.caib.pinbal.core.dto.ParamConfDto;
import es.caib.pinbal.core.service.ScspService;
import es.caib.pinbal.core.service.exception.ClauPrivadaNotFoundException;
import es.caib.pinbal.core.service.exception.ClauPublicaNotFoundException;
import es.caib.pinbal.core.service.exception.EmissorCertNotFoundException;
import es.caib.pinbal.core.service.exception.ParamConfNotFoundException;

/**
 * Implementació de ConsultaService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ScspServiceBean implements ScspService {

	@Autowired
	ScspService delegate;

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ParamConfDto findParamConfByNom(String nom) {
		return delegate.findParamConfByNom(nom);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ParamConfDto createParamConf(ParamConfDto dto) {
		return delegate.createParamConf(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ParamConfDto updateParamConf(ParamConfDto dto) throws ParamConfNotFoundException {
		return delegate.updateParamConf(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ParamConfDto deleteParamConf(String nom) throws ParamConfNotFoundException {
		return delegate.deleteParamConf(nom);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<ParamConfDto> findAllParamConf() {
		return delegate.findAllParamConf();
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EmissorCertDto findEmissorCertById(Long id) {
		return delegate.findEmissorCertById(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EmissorCertDto createEmissorCert(EmissorCertDto dto) {
		return delegate.createEmissorCert(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EmissorCertDto updateEmissorCert(EmissorCertDto dto) throws EmissorCertNotFoundException {
		return delegate.updateEmissorCert(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public EmissorCertDto deleteEmissorCert(Long id) throws EmissorCertNotFoundException {
		return delegate.deleteEmissorCert(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<EmissorCertDto> findAllEmissorCert() {
		return delegate.findAllEmissorCert();
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPrivadaDto findClauPrivadaById(Long id) {
		return delegate.findClauPrivadaById(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPrivadaDto createClauPrivada(ClauPrivadaDto dto) {
		return delegate.createClauPrivada(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPrivadaDto updateClauPrivada(ClauPrivadaDto dto) throws ClauPrivadaNotFoundException {
		return delegate.updateClauPrivada(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPrivadaDto deleteClauPrivada(Long id) throws ClauPrivadaNotFoundException {
		return delegate.deleteClauPrivada(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<ClauPrivadaDto> findAllClauPrivada() {
		return delegate.findAllClauPrivada();
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<OrganismeCessionariDto> findAllOrganismeCessionari() {
		return delegate.findAllOrganismeCessionari();
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPublicaDto findClauPublicaById(Long id) {
		return delegate.findClauPublicaById(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPublicaDto createClauPublica(ClauPublicaDto dto) {
		return delegate.createClauPublica(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPublicaDto updateClauPublica(ClauPublicaDto dto) throws ClauPublicaNotFoundException {
		return delegate.updateClauPublica(dto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ClauPublicaDto deleteClauPublica(Long id) throws ClauPublicaNotFoundException {
		return delegate.deleteClauPublica(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<ClauPublicaDto> findAllClauPublica() {
		return delegate.findAllClauPublica();
	}

}
