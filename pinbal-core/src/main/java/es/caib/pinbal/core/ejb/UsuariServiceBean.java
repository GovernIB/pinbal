/**
 * 
 */
package es.caib.pinbal.core.ejb;

import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.InformeUsuariDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariProtegitException;
import es.caib.pinbal.core.service.exception.UsuariExternNotFoundException;
import org.jboss.annotation.ejb.TransactionTimeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * Implementació de UsuariService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class UsuariServiceBean implements UsuariService {

	@Autowired
	UsuariService delegate;



	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD", "PBL_WS", "tothom"})
	public void inicialitzarUsuariActual() {
		delegate.inicialitzarUsuariActual();
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
		return delegate.findAmbFiltrePaginat(id_entitat, isRepresentant, isDelegat, isAuditor,isAplicacio, 
											 codi, nom, nif, departament, pageable);
	}
	
	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD", "PBL_WS", "tothom"})
	public UsuariDto getDades() {
		return delegate.getDades();
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD", "PBL_WS", "tothom"})
	public UsuariDto getDades(String usuariCodi) {
		return delegate.getDades(usuariCodi);
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
		delegate.actualitzarDadesAdmin(
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
		delegate.actualitzarDadesRepresentant(
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
		delegate.actualitzarDadesAuditor(id, codi, nif, auditor, afegir);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public boolean establirPrincipal(
			Long id,
			String usuariCodi) throws EntitatNotFoundException, EntitatUsuariNotFoundException {
		return delegate.establirPrincipal(id, usuariCodi);
	}

    @Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES"})
    public boolean canviActiu(Long entitatId, String usuariCodi) throws EntitatUsuariNotFoundException, EntitatNotFoundException {
        return delegate.canviActiu(entitatId, usuariCodi);
    }

    @Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPORT"})
	public List<InformeUsuariDto> informeUsuarisAgrupatsEntitatDepartament() {
		return delegate.informeUsuarisAgrupatsEntitatDepartament();
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "tothom"})
	public UsuariDto getUsuariActual() {
		return delegate.getUsuariActual();
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "tothom"})
	public String getIdiomaUsuariActual() {
		return delegate.getIdiomaUsuariActual();
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "tothom"})
	public Integer getNumElementsPaginaDefecte() {
		return delegate.getNumElementsPaginaDefecte();
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "tothom"})
	public UsuariDto updateUsuariActual(UsuariDto dto, boolean updateEntitat) {
		return delegate.updateUsuariActual(dto, updateEntitat);
	}

    @Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD", "PBL_WS", "tothom"})
    public List<UsuariDto> findLikeCodiONom(String text) {
        return delegate.findLikeCodiONom(text);
    }

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD", "PBL_WS", "tothom"})
	public List<UsuariDto> findLikeCodiONomONif(String text) {
		return delegate.findLikeCodiONomONif(text);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD", "PBL_WS", "tothom"})
	public EntitatUsuariDto getEntitatUsuari(Long entitatId, String usuariCodi) {
        return delegate.getEntitatUsuari(entitatId, usuariCodi);
    }

    @Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD", "PBL_WS", "tothom"})
    public UsuariDto getUsuariExtern(String codi) throws Exception {
        return delegate.getUsuariExtern(codi);
    }

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD", "PBL_WS", "tothom"})
	public List<UsuariDto> getUsuarisExterns(String text) throws Exception {
		return delegate.getUsuarisExterns(text);
	}

    @Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD", "PBL_WS", "tothom"})
    public UsuariDto getUsuariEntitat(Long entitatId, String codi) {
        return delegate.getUsuariEntitat(entitatId, codi);
    }

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD", "PBL_WS", "tothom"})
	public List<UsuariDto> getUsuarisEntitat(Long entitatId, String text) {
		return delegate.getUsuarisEntitat(entitatId, text);
	}

    @Override
	@RolesAllowed({"PBL_ADMIN"})
	@TransactionTimeout(value = 1200)
    public void updateUsuariCodi(String codiAntic, String codiNou, String nom, String nif, String email, String idioma) {
        delegate.updateUsuariCodi(codiAntic, codiNou, nom, nif, email,idioma);
    }

}
