/**
 * 
 */
package es.caib.pinbal.webapp.interceptor;

import es.caib.pinbal.webapp.helper.AjaxHelper;
import es.caib.pinbal.webapp.helper.ModalHelper;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		if (resposta)
			resposta = AjaxHelper.comprovarAjaxInterceptor(request, response);
		return resposta;
	}

}
