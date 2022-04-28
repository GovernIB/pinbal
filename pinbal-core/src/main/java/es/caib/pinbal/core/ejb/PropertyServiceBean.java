/**
 * 
 */
package es.caib.pinbal.core.ejb;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.pinbal.core.service.PropertyService;

/**
 * Implementació de PropertyService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PropertyServiceBean implements PropertyService {

	@Autowired
	PropertyService delegate;



	@Override
	public String get(String key) {
		return delegate.get(key);
	}

    @Override
    public String get(String key, String defaultValue) {
        return delegate.get(key, defaultValue);
    }

}
