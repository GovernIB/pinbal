/**
 * 
 */
package es.caib.pinbal.webapp.common;

import javax.servlet.http.HttpServletRequest;

/**
 * Utilitat per a verificar si una petició HTTP correspon a
 * contingut estàtic.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContingutEstaticHelper {

	public static boolean isContingutEstatic(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String path = uri.substring(request.getContextPath().length());
		for (String pce : pathsContingutEstatic)
			if (path.startsWith(pce))
				return true;
		return false;
	}

	private static final String[] pathsContingutEstatic = {
			"/css/",
			"/ico/",
			"/img/",
			"/js/",
			"/recobriment/"};

}
