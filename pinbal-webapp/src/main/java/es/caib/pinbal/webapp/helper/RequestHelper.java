/**
 * 
 */
package es.caib.pinbal.webapp.helper;

import javax.servlet.http.HttpServletRequest;

/**
 * Utilitats per a l'objecte HttpServletRequest.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RequestHelper {

	public static boolean isError(
			HttpServletRequest request) {
		return request.getAttribute("javax.servlet.error.request_uri") != null;
	}

}
