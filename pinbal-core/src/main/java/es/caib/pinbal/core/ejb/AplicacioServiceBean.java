/**
 * 
 */
package es.caib.pinbal.core.ejb;

import es.caib.pinbal.core.dto.CacheDto;
import es.caib.pinbal.core.dto.PaginaDto;
import es.caib.pinbal.core.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AplicacioServiceBean implements AplicacioService {

	@Autowired
	AplicacioService delegate;

    @Override
	@RolesAllowed({"PBL_ADMIN"})
    public PaginaDto<CacheDto> getAllCaches() {
        return delegate.getAllCaches();
    }

	@Override
	@RolesAllowed({"PBL_ADMIN"})
	public void removeCache(String value) {
		delegate.removeCache(value);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN"})
	public void removeAllCaches() {
		delegate.removeAllCaches();
	}


}
