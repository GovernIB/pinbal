/**
 *
 */
package es.caib.pinbal.webapp.common;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import es.caib.pinbal.core.dto.ServeiDto;

/**
 * Utilitat per a gestionar els serveis disponibles o permesos
 * per a l'usuari actual.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiHelper {

	public static final String REQUEST_ATTRIBUTE_SERVEIS = "serveis";

	@SuppressWarnings("unchecked")
	public static List<ServeiDto> getServeis(HttpServletRequest request) {
		if (request.getAttribute(REQUEST_ATTRIBUTE_SERVEIS) != null) {
			return (List<ServeiDto>) request.getAttribute(REQUEST_ATTRIBUTE_SERVEIS);
		} else {
			return null;
		}
	}

}
