/**
 * 
 */
package es.caib.pinbal.webapp.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.webapp.common.ContingutEstaticHelper;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RolHelper;
import es.caib.pinbal.webapp.common.ServeiHelper;

/**
 * Interceptor per a gestionar l'entitat activa i obtenir el llistat d'entitats
 * per a cada pàgina.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SeleccioEntitatInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ServeiService serveiService;


	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (!ContingutEstaticHelper.isContingutEstatic(request)) {
			EntitatHelper.getEntitats(
					request,
					entitatService,
					true);
			EntitatHelper.processarCanviEntitats(
					request,
					entitatService);
			if (RolHelper.isRolActualDelegat(request)) {
				ServeiHelper.getServeis(
						request,
						EntitatHelper.getEntitatActual(request).getId(),
						serveiService);
			}
		}
		return true;
	}

}
