package es.caib.pinbal.webapp.interceptor;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RolHelper;
import es.caib.pinbal.webapp.common.ServeiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * Interceptor dels serveis disponibles per l'usuari actual
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeisInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private ServeiService serveiService;

	@Override
	public boolean preHandle(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler) throws Exception {
		if (!request.getServletPath().startsWith("/api")) {
			EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
			List<ServeiDto> serveis = null;
			if (request.getUserPrincipal() != null && RolHelper.isRolActualDelegat(request)) { // Si hi ha algún usuari autenticat
				String usuari = request.getUserPrincipal().getName();
				LOGGER.debug("Consulta del llistat de serveis pel delegat (usuari=" + usuari + ")");
				serveis = serveiService.findPermesosAmbProcedimentPerDelegat(entitatActual.getId(), null);
			}
			request.setAttribute(
					ServeiHelper.REQUEST_ATTRIBUTE_SERVEIS,
					serveis);
		}
		return true;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ServeisInterceptor.class);

}
