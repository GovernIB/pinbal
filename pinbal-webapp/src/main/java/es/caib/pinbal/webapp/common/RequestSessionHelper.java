/**
 * 
 */
package es.caib.pinbal.webapp.common;

import javax.servlet.http.HttpServletRequest;

/**
 * Utilitat per a gestionar els objectes de la sessió de l'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RequestSessionHelper {

	public static Object obtenirObjecteSessio(
			HttpServletRequest request,
			String clau) {
		return request.getSession().getAttribute(clau);
	}
	public static void actualitzarObjecteSessio(
			HttpServletRequest request,
			String clau,
			Object valor) {
		request.getSession().setAttribute(clau, valor);
	}
	public static void esborrarObjecteSessio(
			HttpServletRequest request,
			String clau) {
		request.getSession().removeAttribute(clau);
	}
	public static boolean existeixObjecteSessio(
			HttpServletRequest request,
			String clau) {
		return request.getSession().getAttribute(clau) != null;
	}

}
