/**
 * 
 */
package es.caib.pinbal.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.core.api.dto.AvisDto;

/**
 * Declaració dels mètodes per a la gestió d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AvisService {

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	AvisDto create(AvisDto avis);

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	AvisDto update(AvisDto avis);

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	AvisDto updateActiva(Long id, boolean activa);

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	AvisDto delete(Long id);

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DELEG')")
	AvisDto findById(Long id);

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	Page<AvisDto> findPaginat(Pageable pageable);

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DELEG')")
	List<AvisDto> findActive();


}
