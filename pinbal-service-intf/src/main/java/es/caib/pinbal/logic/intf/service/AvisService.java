/**
 * 
 */
package es.caib.pinbal.logic.intf.service;

import es.caib.pinbal.logic.intf.dto.AvisDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a la gestió d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AvisService {

	@PreAuthorize("hasRole('PBL_ADMIN')")
	AvisDto create(AvisDto avis);

	@PreAuthorize("hasRole('PBL_ADMIN')")
	AvisDto update(AvisDto avis);

	@PreAuthorize("hasRole('PBL_ADMIN')")
	AvisDto updateActiva(Long id, boolean activa);

	@PreAuthorize("hasRole('PBL_ADMIN')")
	AvisDto delete(Long id);

	@PreAuthorize("isAuthenticated()")
	AvisDto findById(Long id);

	@PreAuthorize("hasRole('PBL_ADMIN')")
	Page<AvisDto> findPaginat(Pageable pageable);

	@PreAuthorize("isAuthenticated()")
	List<AvisDto> findActive();

	@PreAuthorize("hasRole('PBL_ADMIN')")
	List<Long> findAllIds();

}
