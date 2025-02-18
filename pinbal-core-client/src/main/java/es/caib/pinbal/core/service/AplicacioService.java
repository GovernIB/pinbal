/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.pinbal.core.dto.CacheDto;
import es.caib.pinbal.core.dto.IntegracioAccioDto;
import es.caib.pinbal.core.dto.IntegracioDto;
import es.caib.pinbal.core.dto.PaginaDto;
import es.caib.pinbal.core.service.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes comuns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioService {

	/**
	 * Obté les integracions disponibles.
	 * 
	 * @return La llista d'integracions.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<IntegracioDto> integracioFindAll();

	/**
	 * Obté la llista de les darreres accions realitzades a una integració.
	 * 
	 * @param codi
	 *             Codi de la integració.
	 * @return La llista amb les darreres accions.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la integració amb el codi especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(
			String codi) throws NotFoundException;


	@PreAuthorize("hasRole('ROLE_ADMIN')")
    public PaginaDto<CacheDto> getAllCaches();

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void removeCache(String value);

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void removeAllCaches();
}
