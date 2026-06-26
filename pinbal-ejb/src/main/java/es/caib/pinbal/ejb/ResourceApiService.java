/**
 *
 */
package es.caib.pinbal.ejb;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import lombok.experimental.Delegate;
import org.springframework.context.annotation.Primary;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * Implementació de ResourceApiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Primary
@Stateless
public class ResourceApiService extends AbstractServiceEjb<es.caib.pinbal.logic.intf.base.service.ResourceApiService> implements es.caib.pinbal.logic.intf.base.service.ResourceApiService {

	@Delegate
	private es.caib.pinbal.logic.intf.base.service.ResourceApiService delegateService;

	@Override
	protected void setDelegateService(es.caib.pinbal.logic.intf.base.service.ResourceApiService delegateService) {
		this.delegateService = delegateService;
	}

//	@Override
//	@RolesAllowed("**")
//	public void resourceRegister(Class<? extends Resource<?>> resourceClass) {
//		getDelegateService().resourceRegister(resourceClass);
//	}
//
//	@Override
//	@RolesAllowed("**")
//	public List<Class<? extends Resource<?>>> resourceFindAllowed() {
//		return getDelegateService().resourceFindAllowed();
//	}
//
//	@Override
//	@RolesAllowed("**")
//	public ResourcePermissions permissionsCurrentUser(
//			Class<?> resourceClass,
//			Serializable resourceId) {
//		return getDelegateService().permissionsCurrentUser(resourceClass, resourceId);
//	}

}