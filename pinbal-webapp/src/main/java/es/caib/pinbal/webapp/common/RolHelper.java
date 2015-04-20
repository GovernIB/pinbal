/**
 * 
 */
package es.caib.pinbal.webapp.common;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitat per a gestionar el canvi de rol actual de l'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolHelper {

	private static final String ROLE_ADMIN = "ROLE_ADMIN";
	private static final String ROLE_REPRES = "ROLE_REPRES";
	private static final String ROLE_DELEG = "ROLE_DELEG";
	private static final String ROLE_AUDIT = "ROLE_AUDIT";
	private static final String ROLE_SUPERAUD = "ROLE_SUPERAUD";

	private static final String REQUEST_PARAMETER_CANVI_ROL = "canviRol";
	private static final String SESSION_ATTRIBUTE_ROL_ACTUAL = "RolHelper.rol.actual";



	public static void processarCanviRols(
			HttpServletRequest request) {
		String canviRol = request.getParameter(REQUEST_PARAMETER_CANVI_ROL);
		if (canviRol != null && canviRol.length() > 0) {
			LOGGER.debug("Processant canvi rol (rol=" + canviRol + ")");
			if (request.isUserInRole(canviRol)) {
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_ROL_ACTUAL,
						canviRol);
			}
		}
	}

	public static String getRolActual(HttpServletRequest request) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		List<String> rolsDisponibles = getRolsUsuariActual(request);
		if (rolActual == null || !rolsDisponibles.contains(rolActual)) {
			if (request.isUserInRole(ROLE_DELEG) && rolsDisponibles.contains(ROLE_DELEG)) {
				rolActual = ROLE_DELEG;
			} else if (request.isUserInRole(ROLE_REPRES) && rolsDisponibles.contains(ROLE_REPRES)) {
				rolActual = ROLE_REPRES;
			} else if (request.isUserInRole(ROLE_ADMIN)) {
				rolActual = ROLE_ADMIN;
			} else if (request.isUserInRole(ROLE_AUDIT) && rolsDisponibles.contains(ROLE_AUDIT)) {
				rolActual = ROLE_AUDIT;
			} else if (request.isUserInRole(ROLE_SUPERAUD)) {
				rolActual = ROLE_SUPERAUD;
			}
			if (rolActual != null)
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_ROL_ACTUAL,
						rolActual);
		}
		LOGGER.debug("Obtenint rol actual (rol=" + rolActual + ")");
		return rolActual;
	}

	public static boolean isRolActualAdministrador(HttpServletRequest request) {
		return ROLE_ADMIN.equals(getRolActual(request));
	}
	public static boolean isRolActualRepresentant(HttpServletRequest request) {
		return ROLE_REPRES.equals(getRolActual(request));
	}
	public static boolean isRolActualDelegat(HttpServletRequest request) {
		return ROLE_DELEG.equals(getRolActual(request));
	}
	public static boolean isRolActualAuditor(HttpServletRequest request) {
		return ROLE_AUDIT.equals(getRolActual(request));
	}
	public static boolean isRolActualSuperauditor(HttpServletRequest request) {
		return ROLE_SUPERAUD.equals(getRolActual(request));
	}

	public static List<String> getRolsUsuariActual(HttpServletRequest request) {
		LOGGER.debug("Obtenint rols disponibles per a l'usuari actual");
		List<String> rols = new ArrayList<String>();
		if (EntitatHelper.isDelegatEntitatActual(request) && request.isUserInRole(ROLE_DELEG))
			rols.add(ROLE_DELEG);
		if (EntitatHelper.isRepresentantEntitatActual(request) && request.isUserInRole(ROLE_REPRES))
			rols.add(ROLE_REPRES);
		if (request.isUserInRole(ROLE_ADMIN))
			rols.add(ROLE_ADMIN);
		if (EntitatHelper.isAuditorEntitatActual(request) && request.isUserInRole(ROLE_AUDIT))
			rols.add(ROLE_AUDIT);
		if (request.isUserInRole(ROLE_SUPERAUD))
			rols.add(ROLE_SUPERAUD);
		return rols;
	}

	public static void esborrarRolActual(HttpServletRequest request) {
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
	}

	public static String getRequestParameterCanviRol() {
		return REQUEST_PARAMETER_CANVI_ROL;
	}



	private static final Logger LOGGER = LoggerFactory.getLogger(RolHelper.class);

}
