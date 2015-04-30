/**
 * 
 */
package es.caib.pinbal.core.security;

import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.core.GrantedAuthority;

/**
 * Implementació de AuthorizationStrategy que conté una parella
 * (class, AuthorizationStrategy).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SimpleAclAuthorizationStrategy extends AclAuthorizationStrategyImpl {

	private String type;

	public SimpleAclAuthorizationStrategy(
			String type,
			GrantedAuthority... auths) {
		super(auths);
		this.type = type;
	}

	public boolean isForAcl(Acl acl) {
		return acl.getObjectIdentity().getType().equals(type);
	}

}
