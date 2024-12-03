/**
 * 
 */
package es.caib.pinbal.core.ejb;

import es.caib.pinbal.core.dto.FiltreActiuEnumDto;
import es.caib.pinbal.core.dto.InformeProcedimentDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.dto.ProcedimentServeiNomDto;
import es.caib.pinbal.core.dto.ProcedimentServeiSimpleDto;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * Implementació de ProcedimentService que empra una clase delegada per accedir
 * a la funcionalitat del servei.
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
	public ProcedimentDto create(ProcedimentDto creat) throws EntitatNotFoundException {
		return delegate.create(creat);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public ProcedimentDto delete(Long procedimentId) throws ProcedimentNotFoundException {
		return delegate.delete(procedimentId);
	}

	@Override
	@RolesAllowed({"PBL_REPRES", "PBL_REPORT"})
	public ProcedimentDto findAmbEntitatICodi(Long entitatId, String codi) throws EntitatNotFoundException {
		return delegate.findAmbEntitatICodi(entitatId, codi);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD" })
	public List<ProcedimentDto> findAmbEntitat(Long entitatId) throws EntitatNotFoundException {
		return delegate.findAmbEntitat(entitatId);
	}
	
	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD" })
	public List<ProcedimentDto> findAmbEntitat(Long entitatId, String filtre) throws EntitatNotFoundException {
		return delegate.findAmbEntitat(entitatId, filtre);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD" })
	public Page<ProcedimentDto> findAmbFiltrePaginat(
			Long entitatId,
			String codi,
			String nom,
			String departament,
			Long organGestorId,
			String codiSia,
			FiltreActiuEnumDto actiu,
			PaginacioAmbOrdreDto paginacioParams) throws EntitatNotFoundException {
		return delegate.findAmbFiltrePaginat(
				entitatId,
				codi,
				nom,
				departament,
				organGestorId,
				codiSia,
				actiu,
				paginacioParams);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public ProcedimentDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public ProcedimentDto update(ProcedimentDto modificat) throws ProcedimentNotFoundException {
		return delegate.update(modificat);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public ProcedimentDto updateActiu(Long id, boolean actiu) throws ProcedimentNotFoundException {
		return delegate.updateActiu(id, actiu);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public void serveiEnable(Long id, String serveiCodi) throws ProcedimentNotFoundException, ServeiNotFoundException {
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
			String usuariCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException, EntitatUsuariNotFoundException {
		delegate.serveiPermisAllow(id, serveiCodi, usuariCodi);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public void serveiPermisDeny(
			Long id,
			String serveiCodi,
			String usuariCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException, EntitatUsuariNotFoundException {
		delegate.serveiPermisDeny(id, serveiCodi, usuariCodi);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public void serveiPermisDenyAll(String usuariCodi, Long entitatId) throws EntitatUsuariNotFoundException {
		delegate.serveiPermisDenyAll(usuariCodi, entitatId);
	}

    @Override
	@RolesAllowed({"PBL_REPRES", "PBL_WS"})
    public void serveiPermisAllowSelected(String usuariCodi, List<ProcedimentServeiSimpleDto> procedimentsServeis, Long entitatId) throws EntitatUsuariNotFoundException, ProcedimentServeiNotFoundException {
        delegate.serveiPermisAllowSelected(usuariCodi, procedimentsServeis, entitatId);
    }

    @Override
	@RolesAllowed("PBL_REPRES")
    public void serveiPermisDenySelected(String usuariCodi, List<ProcedimentServeiSimpleDto> procedimentsServeis, Long entitatId) throws EntitatUsuariNotFoundException {
        delegate.serveiPermisDenySelected(usuariCodi, procedimentsServeis, entitatId);
    }

    @Override
	@RolesAllowed("PBL_REPRES")
    public List<ProcedimentServeiNomDto> serveiDisponibles(String usuariCodi, Long procedimentId, Long entitatId) throws EntitatUsuariNotFoundException {
        return delegate.serveiDisponibles(usuariCodi, procedimentId, entitatId);
    }

    @Override
	@RolesAllowed("PBL_REPRES")
	public List<String> findUsuarisAmbPermisPerServei(
			Long id,
			String serveiCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException {
		return delegate.findUsuarisAmbPermisPerServei(id, serveiCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ProcedimentDto> findAmbEntitatPerDelegat(Long entitatId) throws EntitatNotFoundException {
		return delegate.findAmbEntitatPerDelegat(entitatId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ProcedimentDto> findActiusAmbEntitatIServeiCodi(
			Long entitatId,
			String serveiCodi) throws EntitatNotFoundException {
		return delegate.findActiusAmbEntitatIServeiCodi(entitatId, serveiCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ProcedimentDto> findAmbServeiCodi(String serveiCodi) {
		return delegate.findAmbServeiCodi(serveiCodi);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPORT"})
	public List<InformeProcedimentDto> informeProcedimentsAgrupatsEntitatDepartament() {
		return delegate.informeProcedimentsAgrupatsEntitatDepartament();
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public boolean putProcedimentCodi(
			Long procedimentId,
			String serveiCodi,
			String procedimentCodi) throws ProcedimentNotFoundException, ServeiNotFoundException {
		return delegate.putProcedimentCodi(procedimentId, serveiCodi, procedimentCodi);

	}
	
	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<ProcedimentDto> findAll() {
		return delegate.findAll();
	}

}
