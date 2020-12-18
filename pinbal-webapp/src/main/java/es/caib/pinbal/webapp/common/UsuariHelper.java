/**
 * 
 */
package es.caib.pinbal.webapp.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.core.service.exception.AccesExternException;

/**
 * Utilitat per a gestionar les dades de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariHelper {

	private static final String SESSION_ATTRIBUTE_DADES_USUARI_ACTUAL = "UsuariHelper.dades.usuari.actual";
	private static final String SESSION_ATTRIBUTE_USUARI_CREACIO_EXEC = "UsuariHelper.usuari.creacio.exec";



	public static UsuariDto getDadesUsuariActual(
			HttpServletRequest request) throws AccesExternException {
		return getDadesUsuariActual(request, null);
	}
	public static UsuariDto getDadesUsuariActual(
			HttpServletRequest request,
			UsuariService usuariService) throws AccesExternException {
		UsuariDto dadesUsuari = null;
		if (request.getUserPrincipal() != null) {
			dadesUsuari = (UsuariDto)request.getSession().getAttribute(
					SESSION_ATTRIBUTE_DADES_USUARI_ACTUAL);
			if (dadesUsuari == null && usuariService != null) {
				dadesUsuari  = usuariService.getDades();
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_DADES_USUARI_ACTUAL,
						dadesUsuari);
			}
		}
		return dadesUsuari;
	}

	public static void inicialitzarUsuariActual(
			HttpServletRequest request,
			UsuariService usuariService) {
		if (!isUsuariCreacioExecutat(request)) {
			if (request.getUserPrincipal() != null) {
				LOGGER.debug("Inicialitzant l'usuari actual (codi=" + request.getUserPrincipal().getName() + ")");
				usuariService.inicialitzarUsuariActual();
			}
			request.getSession().setAttribute(
					SESSION_ATTRIBUTE_USUARI_CREACIO_EXEC,
					new Boolean(true));
		}
	}

	
	
	public static void processarLocale(
			HttpServletRequest request,
			HttpServletResponse response,
			UsuariService usuariService) {
		if (request.getUserPrincipal() != null) {
			try {
				String idioma_usuari = usuariService.getUsuariActual().getIdioma();			

				LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		        localeResolver.setLocale(
		        		request, 
		        		response, 
		        		StringUtils.parseLocaleString(idioma_usuari));
			} catch (Exception e) {
				LOGGER.error("Error establint l'idioma de l'usuari " + request.getUserPrincipal(), e);
			}
		}
	}
	


	private static boolean isUsuariCreacioExecutat(HttpServletRequest request) {
		Boolean refrescat = (Boolean)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_USUARI_CREACIO_EXEC);
		return refrescat != null && refrescat.booleanValue();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(UsuariHelper.class);

}
