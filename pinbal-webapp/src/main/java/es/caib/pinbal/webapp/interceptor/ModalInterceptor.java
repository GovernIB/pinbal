/**
 * 
 */
package es.caib.pinbal.webapp.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.pinbal.webapp.helper.ModalHelper;

/**
 * Interceptor per a evitar processar les pàgines amb Sitemesh.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ModalInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		boolean resposta = ModalHelper.comprovarModalInterceptor(request, response);
		return resposta;
	}

}
