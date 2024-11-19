/**
 * 
 */
package es.caib.pinbal.core.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.pinbal.core.dto.IntegracioAccioDto;
import es.caib.pinbal.core.dto.IntegracioDto;
import es.caib.pinbal.core.service.exception.NotFoundException;

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



}
