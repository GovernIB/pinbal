/**
 * 
 */
package es.caib.pinbal.webapp.common;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.webapp.controller.AuditorController;
import es.caib.pinbal.webapp.controller.AuditorUsuariController;
import es.caib.pinbal.webapp.controller.ConsultaController;
import es.caib.pinbal.webapp.controller.ProcedimentController;
import es.caib.pinbal.webapp.controller.RepresentantUsuariController;

/**
 * Utilitat per a gestionar les entitats de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatHelper {

	private static final String REQUEST_PARAMETER_CANVI_ENTITAT = "canviEntitat";
	private static final String SESSION_ATTRIBUTE_ENTITATS = "EntitatHelper.entitats";
	private static final String SESSION_ATTRIBUTE_ENTITAT_ACTUAL_INDEX = "EntitatHelper.entitat.actual.index";
	private static final String SESSION_ATTRIBUTE_ENTITAT_ACTUAL_REPRESENTANT = "EntitatHelper.entitat.actual.representant";
	private static final String SESSION_ATTRIBUTE_ENTITAT_ACTUAL_DELEGAT = "EntitatHelper.entitat.actual.delegat";
	private static final String SESSION_ATTRIBUTE_ENTITAT_ACTUAL_AUDITOR = "EntitatHelper.entitat.actual.auditor";


	public static List<EntitatDto> getEntitats(
			HttpServletRequest request) {
		return getEntitats(request, null, false);
	}
	@SuppressWarnings("unchecked")
	public static List<EntitatDto> getEntitats(
			HttpServletRequest request,
			EntitatService entitatService,
			boolean refrescarSiNoExisteixen) {
		List<EntitatDto> entitats = (List<EntitatDto>)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ENTITATS);
		if (request.getUserPrincipal() != null) {
			String usuari = request.getUserPrincipal().getName();
			if (entitats == null && refrescarSiNoExisteixen) {
				LOGGER.debug("Consulta del llistat d'entitats (usuari=" + usuari + ", uri=" + request.getRequestURI() + ")");
				entitats = entitatService.findActivesAmbUsuariCodi(usuari);
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_ENTITATS,
						entitats);
				canviEntitatActual(
						request,
						entitatService,
						0);
			}
		}
		return entitats;
	}

	public static void processarCanviEntitats(
			HttpServletRequest request,
			EntitatService entitatService) {
		String canviEntitat = request.getParameter(REQUEST_PARAMETER_CANVI_ENTITAT);
		if (canviEntitat != null && canviEntitat.length() > 0) {
			LOGGER.debug("Processant canvi entitat (id=" + canviEntitat + ")");
			try {
				Long canviEntitatId = new Long(canviEntitat);
				List<EntitatDto> entitats = getEntitats(request, entitatService, false);
				if (entitats != null) {
					int index = 0;
					for (EntitatDto entitat: entitats) {
						if (canviEntitatId.equals(entitat.getId())) {
							canviEntitatActual(
									request,
									entitatService,
									index);
							break;
						}
						index++;
					}
				}
			} catch (NumberFormatException ignored) {
			}
			//RolHelper.esborrarRolActual(request);
		}
	}

	public static EntitatDto getEntitatActual(
			HttpServletRequest request) {
		return getEntitatActual(request, null);
	}
	@SuppressWarnings("unchecked")
	public static EntitatDto getEntitatActual(
			HttpServletRequest request,
			EntitatService entitatService) {
		List<EntitatDto> entitats = (List<EntitatDto>)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ENTITATS);
		Integer entitatActualIndex = (Integer)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL_INDEX);
		if (entitats != null && entitats.size() > 0 && entitatActualIndex != null) {
			if (entitatService != null)
				return entitatService.findById(
						entitats.get(entitatActualIndex).getId());
			else
				return entitats.get(entitatActualIndex);
		}
		return null;
	}

	public static Integer getEntitatActualIndex(HttpServletRequest request) {
		return (Integer)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL_INDEX);
	}

	public static String getRequestParameterCanviEntitat() {
		return REQUEST_PARAMETER_CANVI_ENTITAT;
	}

	public static boolean isRepresentantEntitatActual(HttpServletRequest request) {
		return (Boolean)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL_REPRESENTANT);
	}
	public static boolean isDelegatEntitatActual(HttpServletRequest request) {
		return (Boolean)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL_DELEGAT);
	}
	public static boolean isAuditorEntitatActual(HttpServletRequest request) {
		return (Boolean)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL_AUDITOR);
	}



	private static void canviEntitatActual(
			HttpServletRequest request,
			EntitatService entitatService,
			int indexEntitatActual) {
		request.getSession().setAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL_INDEX,
				indexEntitatActual);
		request.getSession().setAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL_REPRESENTANT,
				isRepresentantEntitat(request, entitatService));
		request.getSession().setAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL_DELEGAT,
				isDelegatEntitat(request, entitatService));
		request.getSession().setAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL_AUDITOR,
				isAuditorEntitat(request, entitatService));
		request.getSession().removeAttribute(
				AuditorController.SESSION_ATTRIBUTE_FILTRE);
		request.getSession().removeAttribute(
				AuditorController.SESSION_ATTRIBUTE_GENFORM);
		request.getSession().removeAttribute(
				AuditorController.SESSION_ATTRIBUTE_GENIDS);
		request.getSession().removeAttribute(
				AuditorUsuariController.SESSION_ATTRIBUTE_FILTRE);
		request.getSession().removeAttribute(
				ConsultaController.SESSION_ATTRIBUTE_FILTRE);
		request.getSession().removeAttribute(
				ProcedimentController.SESSION_ATTRIBUTE_FILTRE);
		request.getSession().removeAttribute(
				RepresentantUsuariController.SESSION_ATTRIBUTE_FILTRE);
	}

	private static boolean isRepresentantEntitat(
			HttpServletRequest request,
			EntitatService entitatService) {
		String user = request.getUserPrincipal().getName();
		EntitatDto entitatActual = getEntitatActual(request, entitatService);
		if (entitatActual != null) {
			for (EntitatUsuariDto usuari: entitatActual.getUsuarisRepresentant()) {
				if (user.equals(usuari.getUsuari().getCodi())) {
					return usuari.isRepresentant();
				}
			}
		}
		return false;
	}
	private static boolean isDelegatEntitat(
			HttpServletRequest request,
			EntitatService entitatService) {
		String user = request.getUserPrincipal().getName();
		EntitatDto entitatActual = getEntitatActual(request, entitatService);
		if (entitatActual != null) {
			for (EntitatUsuariDto usuari: entitatActual.getUsuarisDelegat()) {
				if (user.equals(usuari.getUsuari().getCodi())) {
					return usuari.isDelegat();
				}
			}
		}
		return false;
	}
	private static boolean isAuditorEntitat(
			HttpServletRequest request,
			EntitatService entitatService) {
		String user = request.getUserPrincipal().getName();
		EntitatDto entitatActual = getEntitatActual(request, entitatService);
		if (entitatActual != null) {
			for (EntitatUsuariDto usuari: entitatActual.getUsuarisAuditor()) {
				if (user.equals(usuari.getUsuari().getCodi())) {
					return usuari.isAuditor();
				}
			}
		}
		return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(EntitatHelper.class);

}
