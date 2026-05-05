/**
 * 
 */
package es.caib.pinbal.logic.intf.service;

import es.caib.pinbal.logic.intf.dto.CacheDto;
import es.caib.pinbal.logic.intf.dto.PaginaDto;
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
