/**
 * 
 */
package es.caib.pinbal.webapp.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.webapp.common.ContingutEstaticHelper;
import es.caib.pinbal.webapp.common.PeticionsMultiplesPendentsHelper;
import es.caib.pinbal.webapp.common.RolHelper;

/**
 * Interceptor per a gestionar el rol de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SeleccioRolInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private ConsultaService consultaService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (!ContingutEstaticHelper.isContingutEstatic(request)) {
			RolHelper.processarCanviRols(
					request);
			if (RolHelper.isRolActualDelegat(request)) {
				PeticionsMultiplesPendentsHelper.countPendents(
						request,
						consultaService);
			}
		}
		return true;
	}

}
