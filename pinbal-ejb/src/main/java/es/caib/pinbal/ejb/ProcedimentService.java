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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

/**
 * Implementació de ProcedimentService que empra una clase delegada per accedir
 * a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Primary
@Stateless
public class ProcedimentService extends AbstractService<es.caib.pinbal.logic.intf.service.ProcedimentService> implements es.caib.pinbal.logic.intf.service.ProcedimentService {

	@Override
	@RolesAllowed("PBL_REPRES")
	public ProcedimentDto create(ProcedimentDto creat) throws EntitatNotFoundException {
		return getDelegateService().create(creat);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public ProcedimentDto delete(Long procedimentId) throws ProcedimentNotFoundException {
		return getDelegateService().delete(procedimentId);
	}

	@Override
	@RolesAllowed({"PBL_REPRES", "PBL_REPORT"})
	public ProcedimentDto findAmbEntitatICodi(Long entitatId, String codi) throws EntitatNotFoundException {
		return getDelegateService().findAmbEntitatICodi(entitatId, codi);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD" })
	public List<ProcedimentDto> findAmbEntitat(Long entitatId) throws EntitatNotFoundException {
		return getDelegateService().findAmbEntitat(entitatId);
	}
	
	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD" })
	public List<ProcedimentDto> findAmbEntitat(Long entitatId, String filtre) throws EntitatNotFoundException {
		return getDelegateService().findAmbEntitat(entitatId, filtre);
	}

    @Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES" })
    public List<CodiValor> findAmbEntitatPerOrigen(Long entitatId) throws EntitatNotFoundException {
        return getDelegateService().findAmbEntitatPerOrigen(entitatId);
    }

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES" })
	public List<CodiValor> findAmbEntitatPerFills(Long entitatId, String codiSia) throws EntitatNotFoundException {
		return getDelegateService().findAmbEntitatPerFills(entitatId, codiSia);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN", "PBL_REPRES" })
	public List<String> findCodiSiaFills(Long entitatId, String codiSia) throws EntitatNotFoundException {
		return getDelegateService().findCodiSiaFills(entitatId, codiSia);
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
		return getDelegateService().findAmbFiltrePaginat(
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
		return getDelegateService().findById(id);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public ProcedimentDto update(ProcedimentDto modificat) throws ProcedimentNotFoundException {
		return getDelegateService().update(modificat);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public ProcedimentDto updateActiu(Long id, boolean actiu) throws ProcedimentNotFoundException {
		return getDelegateService().updateActiu(id, actiu);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public void serveiEnable(Long id, String serveiCodi) throws ProcedimentNotFoundException, ServeiNotFoundException {
		getDelegateService().serveiEnable(id, serveiCodi);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public void serveiDisable(
			Long id,
			String serveiCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException {
		getDelegateService().serveiDisable(id, serveiCodi);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public void serveiPermisAllow(
			Long id,
			String serveiCodi,
			String usuariCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException, EntitatUsuariNotFoundException {
		getDelegateService().serveiPermisAllow(id, serveiCodi, usuariCodi);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public void serveiPermisDeny(
			Long id,
			String serveiCodi,
			String usuariCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException, EntitatUsuariNotFoundException {
		getDelegateService().serveiPermisDeny(id, serveiCodi, usuariCodi);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public void serveiPermisDenyAll(String usuariCodi, Long entitatId) throws EntitatUsuariNotFoundException {
		getDelegateService().serveiPermisDenyAll(usuariCodi, entitatId);
	}

    @Override
	@RolesAllowed({"PBL_REPRES", "PBL_WS"})
    public void serveiPermisAllowSelected(String usuariCodi, List<ProcedimentServeiSimpleDto> procedimentsServeis, Long entitatId) throws EntitatUsuariNotFoundException, ProcedimentServeiNotFoundException {
        getDelegateService().serveiPermisAllowSelected(usuariCodi, procedimentsServeis, entitatId);
    }

    @Override
	@RolesAllowed("PBL_REPRES")
    public void serveiPermisDenySelected(String usuariCodi, List<ProcedimentServeiSimpleDto> procedimentsServeis, Long entitatId) throws EntitatUsuariNotFoundException {
        getDelegateService().serveiPermisDenySelected(usuariCodi, procedimentsServeis, entitatId);
    }

    @Override
	@RolesAllowed("PBL_REPRES")
    public List<ProcedimentServeiNomDto> serveiDisponibles(String usuariCodi, Long procedimentId, Long entitatId) throws EntitatUsuariNotFoundException {
        return getDelegateService().serveiDisponibles(usuariCodi, procedimentId, entitatId);
    }

    @Override
	@RolesAllowed("PBL_REPRES")
	public List<EntitatUsuariDto> findUsuarisAmbPermisPerServei(
			Long id,
			String serveiCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException {
		return getDelegateService().findUsuarisAmbPermisPerServei(id, serveiCodi);
	}

    @Override
	@RolesAllowed("PBL_REPRES")
    public Page<EntitatUsuariDto> findUsuarisAmbPermisPerServei(Long id, String serveiCodi, String codi, String nif, String nom, Pageable pageable) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException {
        return getDelegateService().findUsuarisAmbPermisPerServei(id, serveiCodi, codi, nif, nom, pageable);
    }

    @Override
	@RolesAllowed("**")
	public List<ProcedimentDto> findAmbEntitatPerDelegat(Long entitatId) throws EntitatNotFoundException {
		return getDelegateService().findAmbEntitatPerDelegat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<ProcedimentDto> findActiusAmbEntitatIServeiCodi(
			Long entitatId,
			String serveiCodi) throws EntitatNotFoundException {
		return getDelegateService().findActiusAmbEntitatIServeiCodi(entitatId, serveiCodi);
	}

	@Override
	@RolesAllowed("**")
	public List<ProcedimentDto> findAmbServeiCodi(String serveiCodi) {
		return getDelegateService().findAmbServeiCodi(serveiCodi);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPORT"})
	public List<InformeProcedimentDto> informeProcedimentsAgrupatsEntitatDepartament() {
		return getDelegateService().informeProcedimentsAgrupatsEntitatDepartament();
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public boolean putProcedimentCodi(
			Long procedimentId,
			String serveiCodi,
			String procedimentCodi) throws ProcedimentNotFoundException, ServeiNotFoundException {
		return getDelegateService().putProcedimentCodi(procedimentId, serveiCodi, procedimentCodi);

	}
	
	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<ProcedimentDto> findAll() {
		return getDelegateService().findAll();
	}

    @Override
	@RolesAllowed("PBL_REPRES")
    public List<ServeiDto> serveisDisponiblesPerProcediment(Long procedimentId) throws ProcedimentNotFoundException {
        return getDelegateService().serveisDisponiblesPerProcediment(procedimentId);
    }

    @Override
    @RolesAllowed("PBL_REPRES")
    public void migrarProcedimentServei(Long procedimentId, String serveiCodiOrigen, String serveiCodiDesti) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ProcedimentServeiExistsException {
        getDelegateService().migrarProcedimentServei(procedimentId, serveiCodiOrigen, serveiCodiDesti);
    }

}
