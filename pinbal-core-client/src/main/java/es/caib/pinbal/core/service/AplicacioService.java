/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.pinbal.core.dto.CacheDto;
import es.caib.pinbal.core.dto.PaginaDto;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Declaració dels mètodes comuns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioService {


	@PreAuthorize("hasRole('ROLE_ADMIN')")
    public PaginaDto<CacheDto> getAllCaches();

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void removeCache(String value);

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void removeAllCaches();
}
