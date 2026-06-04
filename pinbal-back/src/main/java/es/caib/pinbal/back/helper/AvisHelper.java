/**
 * 
 */
package es.caib.pinbal.back.helper;

import es.caib.pinbal.logic.intf.dto.AvisDto;
import es.caib.pinbal.logic.intf.service.AvisService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * Utilitat per obtenir els avisos de sessió..
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AvisHelper {

	private static final String REQUEST_PARAMETER_AVISOS = "AvisHelper.findAvisos";


	@SuppressWarnings("unchecked")
	public static void findAvisos(
			HttpServletRequest request, 
			AvisService avisService) {
		
		List<AvisDto> avisos = (List<AvisDto>) request.getAttribute(REQUEST_PARAMETER_AVISOS);
		if (avisos == null && !RequestHelper.isError(request) && avisService != null) {
			avisos = avisService.findActive();
			request.setAttribute(REQUEST_PARAMETER_AVISOS, avisos);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<AvisDto> getAvisos(
			HttpServletRequest request) {
		return (List<AvisDto>) request.getAttribute(REQUEST_PARAMETER_AVISOS);
	}
	

}
