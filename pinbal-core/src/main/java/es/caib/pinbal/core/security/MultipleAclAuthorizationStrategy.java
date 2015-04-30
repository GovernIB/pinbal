/**
 * 
 */
package es.caib.pinbal.core.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.model.Acl;

/**
 * Implementació de AuthorizationStrategy que empra els SimpleAclAuthorizationStrategy.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MultipleAclAuthorizationStrategy implements AclAuthorizationStrategy {

	private SimpleAclAuthorizationStrategy[] strategies;

	public MultipleAclAuthorizationStrategy(
			SimpleAclAuthorizationStrategy... strategies) {
		this.strategies = strategies;
	}

	public void securityCheck(Acl acl, int changeType) {
		for (SimpleAclAuthorizationStrategy strategy: strategies) {
			if (strategy.isForAcl(acl)) {
				strategy.securityCheck(acl, changeType);
				return;
			}
		}
		throw new AccessDeniedException(
                "No s'ha trobat cap strategy per una ACL del tipus " + acl.getObjectIdentity().getType());
	}

}
