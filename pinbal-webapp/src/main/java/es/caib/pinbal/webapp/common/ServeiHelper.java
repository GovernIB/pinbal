/**
 * 
 */
package es.caib.pinbal.webapp.common;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;

/**
 * Utilitat per a gestionar els serveis disponibles o permesos
 * per a l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiHelper {

	private static final String SESSION_ATTRIBUTE_SERVEIS = "ServeiHelper.serveis";
	private static final String SESSION_ATTRIBUTE_ENTITAT_ID = "ServeiHelper.entitat.id";


	public static List<ServeiDto> getServeis(
			HttpServletRequest request,
			Long entitatId) throws EntitatNotFoundException, ProcedimentNotFoundException {
		return getServeis(request, entitatId, null);
	}
	@SuppressWarnings("unchecked")
	public static List<ServeiDto> getServeis(
			HttpServletRequest request,
			Long entitatId,
			ServeiService serveiService) throws EntitatNotFoundException, ProcedimentNotFoundException {
		Long eid = (Long)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ID);
		List<ServeiDto> serveis = null;
		// Només obté els serveis de la sessió si no ha canviat l'entitat
		if (entitatId.equals(eid)) {
			serveis = (List<ServeiDto>)request.getSession().getAttribute(
					SESSION_ATTRIBUTE_SERVEIS);
		}
		if (request.getUserPrincipal() != null) {
			if (serveis == null) {
				String usuari = request.getUserPrincipal().getName();
				LOGGER.debug("Consulta del llistat de serveis pel delegat (usuari=" + usuari + ")");
				serveis = serveiService.findPermesosAmbProcedimentPerDelegat(entitatId, null);
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_SERVEIS,
						serveis);
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_ENTITAT_ID,
						entitatId);
			}
		}
		return serveis;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ServeiHelper.class);

}
