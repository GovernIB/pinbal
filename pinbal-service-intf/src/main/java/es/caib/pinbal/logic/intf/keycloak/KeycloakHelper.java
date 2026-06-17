package es.caib.pinbal.logic.intf.keycloak;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Helper per a accedir a informació de l'usuari autenticat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class KeycloakHelper {

    public static String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    public static String getCurrentUserFullName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof KeycloakUserDetails) {
            return ((KeycloakUserDetails) auth.getPrincipal()).getFullName();
        }
        return null;
    }

    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof KeycloakUserDetails) {
            return ((KeycloakUserDetails) auth.getPrincipal()).getEmail();
        }
        return null;
    }

    public static Object getKeycloakPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof KeycloakUserDetails) {
            return ((KeycloakUserDetails) auth.getPrincipal()).getKeycloakPrincipal();
        }
        return null;
    }

}
