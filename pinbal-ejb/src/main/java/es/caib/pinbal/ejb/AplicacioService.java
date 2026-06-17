/**
 * 
 */
package es.caib.pinbal.ejb;

import es.caib.pinbal.logic.intf.dto.CacheDto;
import es.caib.pinbal.logic.intf.dto.PaginaDto;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Primary
@Stateless
public class AplicacioService extends AbstractService<es.caib.pinbal.logic.intf.service.AplicacioService> implements es.caib.pinbal.logic.intf.service.AplicacioService {

    @Override
	@RolesAllowed({"PBL_ADMIN"})
    public PaginaDto<CacheDto> getAllCaches() {
        return getDelegateService().getAllCaches();
    }

	@Override
	@RolesAllowed({"PBL_ADMIN"})
	public void removeCache(String value) {
		getDelegateService().removeCache(value);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN"})
	public void removeAllCaches() {
		getDelegateService().removeAllCaches();
	}

    @Override
	@PermitAll
    public String getAppVersion() {
        return getDelegateService().getAppVersion();
    }

	@Override
	@PermitAll
	public void setAppVersion(String versio) {
		getDelegateService().setAppVersion(versio);
	}


}
