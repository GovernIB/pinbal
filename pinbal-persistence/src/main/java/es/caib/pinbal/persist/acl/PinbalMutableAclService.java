package es.caib.pinbal.persist.acl;

import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;


public interface PinbalMutableAclService extends MutableAclService {

	public void deleteEntries(ObjectIdentity oid, Sid sid);
	
}
