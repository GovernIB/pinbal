/**
 * 
 */
package es.caib.pinbal.webapp.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.webapp.common.UsuariHelper;

/**
 * Interceptor per a mantenir la informació de la taula d'usuaris.
 * Verifica si els usuaris autenticats estan creats a la taula i si
 * no hi estan els crea.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private UsuariService usuariService;



	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		UsuariHelper.inicialitzarUsuariActual(request, usuariService);
		UsuariHelper.getDadesUsuariActual(request, usuariService);
		return true;
	}

}
