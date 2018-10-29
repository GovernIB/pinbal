/**
 * 
 */
package es.caib.pinbal.core.ejb;

import java.io.IOException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.pinbal.core.service.VersioService;

/**
 * Implementació de VersioService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class VersioServiceBean implements VersioService {

	@Autowired
	VersioService delegate;

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD", "PBL_WS", "tothom"})
	public String getVersioActual() throws IOException {
		return delegate.getVersioActual();
	}

}
