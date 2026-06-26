/**
 * 
 */
package es.caib.pinbal.ejb;

import es.caib.pinbal.logic.intf.dto.EntitatUsuariDto;
import es.caib.pinbal.logic.intf.dto.InformeUsuariDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatUsuariProtegitException;
import es.caib.pinbal.logic.intf.service.exception.UsuariExternNotFoundException;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

/**
 * Implementació de UsuariService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Primary
@Stateless
public class UsuariService extends AbstractService<es.caib.pinbal.logic.intf.service.UsuariService> implements es.caib.pinbal.logic.intf.service.UsuariService {

	@Override
	@RolesAllowed("**")
	public void inicialitzarUsuariActual() {
		getDelegateService().inicialitzarUsuariActual();
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public Page<EntitatUsuariDto> findAmbFiltrePaginat(
			Long id_entitat,
			Boolean isRepresentant,
			Boolean isDelegat,
			Boolean isAuditor,
			Boolean isAplicacio,
			String codi,
			String nom,
			String nif,
			String departament,
			Pageable pageable){
		return getDelegateService().findAmbFiltrePaginat(id_entitat, isRepresentant, isDelegat, isAuditor,isAplicacio, 
											 codi, nom, nif, departament, pageable);
	}
	
	@Override
	@RolesAllowed("**")
	public UsuariDto getDades() {
		return getDelegateService().getDades();
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto getDades(String usuariCodi) {
		return getDelegateService().getDades(usuariCodi);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void actualitzarDadesAdmin(
			Long id,
			String codi,
			String nif,
			String departament,
			boolean representant,
			boolean delegat,
			boolean auditor,
			boolean aplicacio,
			boolean afegir,
			boolean actiu) throws EntitatNotFoundException, UsuariExternNotFoundException {
		getDelegateService().actualitzarDadesAdmin(
				id,
				codi,
				nif,
				departament,
				representant,
				delegat,
				auditor,
				aplicacio,
				afegir,
				actiu);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public void actualitzarDadesRepresentant(
			Long id,
			String codi,
			String nif,
			String departament,
			boolean representant,
			boolean delegat,
			boolean aplicacio,
			boolean afegir,
			boolean actiu) throws EntitatNotFoundException, EntitatUsuariProtegitException, UsuariExternNotFoundException {
		getDelegateService().actualitzarDadesRepresentant(
				id,
				codi,
				nif,
				departament,
				representant,
				delegat,
				aplicacio,
				afegir,
				actiu);
	}

	@Override
	@RolesAllowed("PBL_AUDIT")
	public void actualitzarDadesAuditor(
			Long id,
			String codi,
			String nif,
			boolean auditor,
			boolean afegir) throws EntitatNotFoundException, EntitatUsuariProtegitException, UsuariExternNotFoundException {
		getDelegateService().actualitzarDadesAuditor(id, codi, nif, auditor, afegir);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public boolean establirPrincipal(
			Long id,
			String usuariCodi) throws EntitatNotFoundException, EntitatUsuariNotFoundException {
		return getDelegateService().establirPrincipal(id, usuariCodi);
	}

    @Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES"})
    public boolean canviActiu(Long entitatId, String usuariCodi) throws EntitatUsuariNotFoundException, EntitatNotFoundException {
        return getDelegateService().canviActiu(entitatId, usuariCodi);
    }

    @Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPORT"})
	public List<InformeUsuariDto> informeUsuarisAgrupatsEntitatDepartament() {
		return getDelegateService().informeUsuarisAgrupatsEntitatDepartament();
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto getUsuariActual() {
		return getDelegateService().getUsuariActual();
	}

	@Override
	@RolesAllowed("**")
	public String getIdiomaUsuariActual() {
		return getDelegateService().getIdiomaUsuariActual();
	}

	@Override
	@RolesAllowed("**")
	public Integer getNumElementsPaginaDefecte() {
		return getDelegateService().getNumElementsPaginaDefecte();
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto updateUsuariActual(UsuariDto dto, boolean updateEntitat) {
		return getDelegateService().updateUsuariActual(dto, updateEntitat);
	}

    @Override
	@RolesAllowed("**")
    public List<UsuariDto> findLikeCodiONom(String text) {
        return getDelegateService().findLikeCodiONom(text);
    }

	@Override
	@RolesAllowed("**")
	public List<UsuariDto> findLikeCodiONomONif(String text) {
		return getDelegateService().findLikeCodiONomONif(text);
	}

	@Override
	@RolesAllowed("**")
	public EntitatUsuariDto getEntitatUsuari(Long entitatId, String usuariCodi) {
        return getDelegateService().getEntitatUsuari(entitatId, usuariCodi);
    }

    @Override
	@RolesAllowed("**")
    public UsuariDto getUsuariExtern(String codi) throws Exception {
        return getDelegateService().getUsuariExtern(codi);
    }

	@Override
	@RolesAllowed("**")
	public List<UsuariDto> getUsuarisExterns(String text) throws Exception {
		return getDelegateService().getUsuarisExterns(text);
	}

    @Override
	@RolesAllowed("**")
    public UsuariDto getUsuariEntitat(Long entitatId, String codi) {
        return getDelegateService().getUsuariEntitat(entitatId, codi);
    }

	@Override
	@RolesAllowed("**")
	public List<UsuariDto> getUsuarisEntitat(Long entitatId, String text) {
		return getDelegateService().getUsuarisEntitat(entitatId, text);
	}

    @Override
	@RolesAllowed({"PBL_ADMIN"})
	@TransactionTimeout(value = 1200)
    public Long updateUsuariCodi(String codiAntic, String codiNou) {
        return getDelegateService().updateUsuariCodi(codiAntic, codiNou) ;
    }

    @Override
	@RolesAllowed({"PBL_ADMIN"})
	@TransactionTimeout(value = 1200)
    public void updateUsuariCodi(String codiAntic, String codiNou, String nom, String nif, String email, String idioma) {
        getDelegateService().updateUsuariCodi(codiAntic, codiNou, nom, nif, email,idioma);
    }

}
