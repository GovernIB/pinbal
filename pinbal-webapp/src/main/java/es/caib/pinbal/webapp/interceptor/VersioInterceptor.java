/**
 * 
 */
package es.caib.pinbal.webapp.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.pinbal.core.service.VersioService;

/**
 * Interceptor per a evitar processar les pàgines amb Sitemesh.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class VersioInterceptor extends HandlerInterceptorAdapter {

	private static final String APPLICATION_ATTRIBUTE_VERSIO_ACTUAL = "versioActual";

	@Autowired
	private VersioService versioService;



	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		String versioActual = (String)request.getSession().getServletContext().getAttribute(APPLICATION_ATTRIBUTE_VERSIO_ACTUAL);
		if (versioActual == null) {
			versioActual = versioService.getVersioActual();
			request.getSession().getServletContext().setAttribute(
					APPLICATION_ATTRIBUTE_VERSIO_ACTUAL,
					versioActual);
		}
		return true;
	}

}
