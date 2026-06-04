/**
 * 
 */
package es.caib.pinbal.back.helper;

import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;

import javax.servlet.http.HttpServletRequest;


/**
 * Utilitat per a consultar les peticions múltiples pendents
 * per al delegat actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PeticionsMultiplesPendentsHelper {

	private static final String REQUEST_ATTRIBUTE_PENDENTS = "PeticionsMultiplesPendentsHelper.pendents";

	public static Integer countPendents(
			HttpServletRequest request,
			ConsultaService consultaService) throws EntitatNotFoundException {
		Integer count = (Integer)request.getAttribute(REQUEST_ATTRIBUTE_PENDENTS);
		if (count == null && consultaService != null) {
			EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
			if (entitatActual != null) {
				Long lcount = new Long(
						consultaService.countConsultesMultiplesProcessant(
								entitatActual.getId()));
				request.setAttribute(
						REQUEST_ATTRIBUTE_PENDENTS,
						new Integer(lcount.intValue()));
			}
		}
		return count;
	}

	public static Integer countPendents(
			HttpServletRequest request) throws EntitatNotFoundException {
		return countPendents(request, null);
	}

}
