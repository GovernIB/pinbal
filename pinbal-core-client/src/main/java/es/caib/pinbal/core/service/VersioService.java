/**
 * 
 */
package es.caib.pinbal.core.service;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Declaració dels mètodes per a gestionar la versió de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface VersioService {

	/**
	 * Obté la versió actual de l'aplicació.
	 * 
	 * @return La versió actual.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_DELEG') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD') or hasRole('ROLE_WS')")
	public String getVersioActual();

}
