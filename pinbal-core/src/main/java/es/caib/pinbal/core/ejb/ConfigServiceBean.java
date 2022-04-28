/**
 * 
 */
package es.caib.pinbal.core.ejb;

import es.caib.pinbal.core.dto.ConfigDto;
import es.caib.pinbal.core.dto.ConfigGroupDto;
import es.caib.pinbal.core.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * Implementació de ConfigService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ConfigServiceBean implements ConfigService {

	@Autowired
	ConfigService delegate;

	@Override
	@RolesAllowed({"PBL_ADMIN"})
	public ConfigDto updateProperty(ConfigDto property) throws Exception{
		return delegate.updateProperty(property);
	}
	@Override
	@RolesAllowed({"PBL_ADMIN"})
	public List<ConfigGroupDto> findAll(){
		return delegate.findAll();
	}

	@Override
	@RolesAllowed({"PBL_ADMIN"})
	public List<String> syncFromJBossProperties(){
		return delegate.syncFromJBossProperties();
	}

    @Override
	@RolesAllowed({"PBL_ADMIN"})
    public void reiniciarTasques() {
        delegate.reiniciarTasques();
    }

}
