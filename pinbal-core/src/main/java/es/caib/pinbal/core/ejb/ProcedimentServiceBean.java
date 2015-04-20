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

import es.caib.pinbal.core.dto.InformeProcedimentDto;
import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;

/**
 * Implementació de ProcedimentService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ProcedimentServiceBean implements ProcedimentService {

	@Autowired
	ProcedimentService delegate;



	@Override
	@RolesAllowed("PBL_REPRES")
	public ProcedimentDto create(ProcedimentDto creat)
			throws EntitatNotFoundException {
		return delegate.create(creat);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public ProcedimentDto delete(Long procedimentId)
			throws ProcedimentNotFoundException {
		return delegate.delete(procedimentId);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public ProcedimentDto findAmbEntitatICodi(Long entitatId, String codi) throws EntitatNotFoundException {
		return delegate.findAmbEntitatICodi(entitatId, codi);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD"})
	public List<ProcedimentDto> findAmbEntitat(Long entitatId)
			throws EntitatNotFoundException {
		return delegate.findAmbEntitat(entitatId);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public PaginaLlistatDto<ProcedimentDto> findAmbFiltrePaginat(
			Long entitatId,
			String codi,
			String nom,
			String departament,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException {
		return delegate.findAmbFiltrePaginat(
				entitatId,
				codi,
				nom,
				departament,
				paginacioAmbOrdre);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public ProcedimentDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public ProcedimentDto update(
			ProcedimentDto modificat) throws ProcedimentNotFoundException {
		return delegate.update(modificat);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public ProcedimentDto updateActiu(
			Long id, boolean actiu) throws ProcedimentNotFoundException {
		return delegate.updateActiu(id, actiu);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public void serveiEnable(
			Long id,
			String serveiCodi) throws ProcedimentNotFoundException, ServeiNotFoundException {
		delegate.serveiEnable(id, serveiCodi);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public void serveiDisable(
			Long id,
			String serveiCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException {
		delegate.serveiDisable(id, serveiCodi);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public void serveiPermisAllow(
			Long id,
			String serveiCodi,
			String usuariCodi) throws ProcedimentNotFoundException,
			ProcedimentServeiNotFoundException, EntitatUsuariNotFoundException {
		delegate.serveiPermisAllow(id, serveiCodi, usuariCodi);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public void serveiPermisDeny(Long id, String serveiCodi, String usuariCodi)
			throws ProcedimentNotFoundException,
			ProcedimentServeiNotFoundException, EntitatUsuariNotFoundException {
		delegate.serveiPermisDeny(id, serveiCodi, usuariCodi);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public List<String> findUsuarisAmbPermisPerServei(Long id, String serveiCodi)
			throws ProcedimentNotFoundException,
			ProcedimentServeiNotFoundException {
		return delegate.findUsuarisAmbPermisPerServei(id, serveiCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ProcedimentDto> findAmbEntitatPerDelegat(
			Long entitatId) throws EntitatNotFoundException {
		return delegate.findAmbEntitatPerDelegat(entitatId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ProcedimentDto> findActiusAmbEntitatIServeiCodi(
			Long entitatId,
			String serveiCodi) throws EntitatNotFoundException {
		return delegate.findActiusAmbEntitatIServeiCodi(
				entitatId,
				serveiCodi);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<InformeProcedimentDto> informeProcedimentsAgrupatsEntitatDepartament() {
		return delegate.informeProcedimentsAgrupatsEntitatDepartament();
	}

}
