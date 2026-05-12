/**
 * 
 */
package es.caib.pinbal.webapp.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.pinbal.core.service.AvisService;
import es.caib.pinbal.webapp.helper.AvisHelper;



/**
 * Interceptor per a mostrar avisos als usuaris
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AvisosInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AvisService avisService;



	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		String restApiPrefix = request.getContextPath() + "/api/recobriment";
		boolean isPeticioApi = request.getRequestURI().startsWith(restApiPrefix);
		if (!isPeticioApi) {
			AvisHelper.findAvisos(
					request,
					avisService);
		}
		return true;
	}

}
