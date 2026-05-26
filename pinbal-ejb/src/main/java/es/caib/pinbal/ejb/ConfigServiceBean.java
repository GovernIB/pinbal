/**
 * 
 */
package es.caib.pinbal.ejb;

import es.caib.pinbal.logic.intf.dto.ConfigDto;
import es.caib.pinbal.logic.intf.dto.ConfigGroupDto;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de ConfigService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class ConfigServiceBean extends AbstractService<es.caib.pinbal.logic.intf.service.ConfigService> implements es.caib.pinbal.logic.intf.service.ConfigService {

	@Override
	@RolesAllowed({"PBL_ADMIN"})
	public ConfigDto updateProperty(ConfigDto property) throws Exception{
		return getDelegateService().updateProperty(property);
	}
	@Override
	@RolesAllowed({"PBL_ADMIN"})
	public List<ConfigGroupDto> findAll(){
		return getDelegateService().findAll();
	}

	@Override
	@RolesAllowed({"PBL_ADMIN"})
	public List<String> syncFromJBossProperties(){
		return getDelegateService().syncFromJBossProperties();
	}

    @Override
	@RolesAllowed({"PBL_ADMIN"})
    public void reiniciarTasques() {
        getDelegateService().reiniciarTasques();
    }
    
    @Override
	@PreAuthorize("isAuthenticated()")
	public String getTempsErrorsMonitorIntegracio() {
    	return getDelegateService().getTempsErrorsMonitorIntegracio();
    }

}
