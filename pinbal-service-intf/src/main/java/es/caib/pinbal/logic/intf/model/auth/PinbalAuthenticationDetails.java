package es.caib.pinbal.logic.intf.model.auth;

/**
 * Interfície amb els detalls de l'autenticació.
 *
 * @author Límit Tecnologies
 */
public interface PinbalAuthenticationDetails {

	String getJwtToken();
	String getPreferredUsername();
	String getName();
	String getEmail();
	String getNif();
	String[] getOriginalRoles();

}
