/**
 * 
 */
package es.caib.pinbal.ejb;

import org.springframework.context.annotation.Primary;
import org.springframework.security.acls.model.*;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * Implementació de AclService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class AclServiceBean extends AbstractService<AclService> implements AclService {


	@Override
	@RolesAllowed("**")
	public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
		return getDelegateService().findChildren(parentIdentity);
	}

	@Override
	@RolesAllowed("**")
	public Acl readAclById(
			ObjectIdentity object) throws NotFoundException {
		return getDelegateService().readAclById(object);
	}

	@Override
	@RolesAllowed("**")
	public Acl readAclById(
			ObjectIdentity object,
			List<Sid> sids) throws NotFoundException {
		return getDelegateService().readAclById(object, sids);
	}

	@Override
	@RolesAllowed("**")
	public Map<ObjectIdentity, Acl> readAclsById(
			List<ObjectIdentity> objects) throws NotFoundException {
		return getDelegateService().readAclsById(objects);
	}

	@Override
	@RolesAllowed("**")
	public Map<ObjectIdentity, Acl> readAclsById(
			List<ObjectIdentity> objects,
			List<Sid> sids) throws NotFoundException {
		return getDelegateService().readAclsById(objects, sids);
	}

}
