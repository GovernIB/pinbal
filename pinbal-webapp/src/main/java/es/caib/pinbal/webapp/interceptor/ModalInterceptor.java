/**
 * 
 */
package es.caib.pinbal.webapp.interceptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor per a evitar processar les pàgines amb Sitemesh.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ModalInterceptor extends HandlerInterceptorAdapter {

	private static final String PREFIX_MODAL = "/pinbal/modal";

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getRequestURI().startsWith(PREFIX_MODAL)) {
			String newUri = request.getRequestURI().substring(PREFIX_MODAL.length());
			RequestDispatcher dispatcher = request.getRequestDispatcher(newUri);
		    dispatcher.forward(request, response);
			return false;
		}
		return true;
	}

}
