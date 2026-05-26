package es.caib.pinbal.logic.base.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Mètodes per a interactuar amb l'usuari autenticat.
 *
 * @author Límit Tecnologies
 */
@Component
public class AuthenticationHelper {

	/**
	 * Retorna el nom de l'usuari actual.
	 *
	 * @return el nom de l'usuari actual.
	 */
	public String getCurrentUserName() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getName();
	}

	/**
	 * Retorna la llista de rols de l'usuari actual.
	 *
	 * @return la llista de rols.
	 */
	public String[] getCurrentUserRoles() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getAuthorities().stream().
				map(GrantedAuthority::getAuthority).
				toArray(String[]::new);
	}

	/**
	 * Retorna true si l'usuari actual te el rol especificat.
	 *
	 * @param role
	 *            el rol a verificar.
	 * @return true si l'usuari actual te el rol especificat i false en cas contrari.
	 */
	public boolean isCurrentUserInRole(String role) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getAuthorities().stream().
				anyMatch(ga -> ga.getAuthority().equals(role));
	}

	/**
	 * Retorna true si l'usuari de l'objecte d'autenticació te el rol especificat.
	 *
	 * @param auth
	 *            l'objecte d'autenticació.
	 * @param role
	 *            el rol a verificar.
	 * @return true si l'usuari actual te el rol especificat i false en cas contrari.
	 */
	public boolean isCurrentUserInRole(Authentication auth, String role) {
		boolean isInRole = false;
		for (GrantedAuthority ga: auth.getAuthorities()) {
			if (ga != null && ga.getAuthority().equals(role)) {
				isInRole = true;
				break;
			}
		}
		return isInRole;
	}

}
