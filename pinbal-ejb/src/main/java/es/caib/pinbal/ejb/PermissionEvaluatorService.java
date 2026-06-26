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
 * Implementació de PermissionEvaluatorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Primary
@Stateless
public class PermissionEvaluatorService extends AbstractServiceEjb<es.caib.pinbal.logic.intf.base.service.PermissionEvaluatorService> implements es.caib.pinbal.logic.intf.base.service.PermissionEvaluatorService {

	@Delegate
	private es.caib.pinbal.logic.intf.base.service.PermissionEvaluatorService delegateService;

	@Override
	protected void setDelegateService(es.caib.pinbal.logic.intf.base.service.PermissionEvaluatorService delegateService) {
		this.delegateService = delegateService;
	}

//	@Override
//	@PermitAll
//	public boolean hasPermission(
//			Authentication authentication,
//			Object targetDomainObject,
//			Object permission) {
//		return getDelegateService().hasPermission(authentication, targetDomainObject, permission);
//	}
//
//	@Override
//	@PermitAll
//	public boolean hasPermission(
//			Authentication authentication,
//			Serializable targetId,
//			String targetType,
//			Object permission) {
//		return getDelegateService().hasPermission(authentication, targetId, targetType, permission);
//	}

}