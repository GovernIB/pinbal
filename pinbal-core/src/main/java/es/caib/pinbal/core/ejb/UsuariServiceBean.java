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

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.InformeUsuariDto;
import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariProtegitException;
import es.caib.pinbal.core.service.exception.UsuariExternNotFoundException;

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
	public PaginaLlistatDto<EntitatUsuariDto> findAmbFiltrePaginat(
			Long id_entitat,
			String codi,
			String nom,
			String nif,
			String departament,
			PaginacioAmbOrdreDto paginacioAmbOrdre){
		return delegate.findAmbFiltrePaginat(id_entitat, codi, nom, nif, departament, paginacioAmbOrdre);
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
			boolean afegir) throws EntitatNotFoundException, UsuariExternNotFoundException {
		delegate.actualitzarDadesAdmin(id, codi, nif, departament, representant, delegat, auditor, aplicacio, afegir);
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
			boolean afegir) throws EntitatNotFoundException, EntitatUsuariProtegitException, UsuariExternNotFoundException {
		delegate.actualitzarDadesRepresentant(id, codi, nif, departament, representant, delegat, aplicacio, afegir);
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
	@RolesAllowed("PBL_ADMIN")
	public List<InformeUsuariDto> informeUsuarisAgrupatsEntitatDepartament() {
		return delegate.informeUsuarisAgrupatsEntitatDepartament();
	}

}
