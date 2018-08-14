/**
 * 
 */
package es.caib.pinbal.core.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;


/**
 * Helper per a la gestió de permisos dins les ACLs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisosHelper {

	public static void assignarPermisUsuari(
			String userName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission,
			MutableAclService aclService) {
		assignarPermisos(
				new PrincipalSid(userName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				aclService);
	}
	public static void assignarPermisRol(
			String roleName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission,
			MutableAclService aclService) {
		assignarPermisos(
				new GrantedAuthoritySid(getMapeigRol(roleName)),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				aclService);
	}

	public static void revocarPermisUsuari(
			String userName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission,
			MutableAclService aclService) {
		revocarPermisos(
				new PrincipalSid(userName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				aclService);
	}
	public static void revocarPermisRol(
			String roleName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission,
			MutableAclService aclService) {
		revocarPermisos(
				new GrantedAuthoritySid(getMapeigRol(roleName)),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				aclService);
	}

	public static void revocarPermisosServei(
			Class<?> objectClass,
			Long objectIdentifier,
			MutableAclService aclService) {
		ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
		try {
			MutableAcl acl = (MutableAcl)aclService.readAclById(oid);
			List<AccessControlEntry> aces = acl.getEntries();
			for (int aceIndex = aces.size() - 1; aceIndex >= 0; aceIndex--) {
				acl.deleteAce(aceIndex);
			}
			aclService.updateAcl(acl);
		} catch (NotFoundException ex) {
			// Si no troba cap permis per al servei no fa res
		}
	}

	public static void mourePermisUsuari(
			String sourceUserName,
			String targetUserName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission,
			MutableAclService aclService) {
		assignarPermisos(
				new PrincipalSid(targetUserName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				aclService);
		revocarPermisos(
				new PrincipalSid(sourceUserName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				aclService);
	}
	public static void mourePermisRol(
			String sourceRoleName,
			String targetRoleName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission,
			MutableAclService aclService) {
		assignarPermisos(
				new GrantedAuthoritySid(getMapeigRol(targetRoleName)),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				aclService);
		revocarPermisos(
				new GrantedAuthoritySid(getMapeigRol(sourceRoleName)),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				aclService);
	}

	public static List<AccessControlEntry> getAclSids(
			Class<?> objectClass,
			Long objectIdentifier,
			MutableAclService aclService) {
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			MutableAcl acl = (MutableAcl)aclService.readAclById(oid);
			return acl.getEntries();
		} catch (NotFoundException nfex) {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void filterGrantedAny(
			Collection<?> objectIdentifiers,
			ObjectIdentifierExtractor objectIdentifierExtractor,
			Class<?> clazz,
			Permission[] permissions,
			MutableAclService aclService,
			Authentication auth) {
		Iterator<?> it = objectIdentifiers.iterator();
		while (it.hasNext()) {
			Long objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(
					it.next());
			if (!isGrantedAny(
					objectIdentifier,
					clazz,
					permissions,
					aclService,
					auth))
				it.remove();
		}
	}
	public static boolean isGrantedAny(
			Long objectIdentifier,
			Class<?> clazz,
			Permission[] permissions,
			MutableAclService aclService,
			Authentication auth) {
		boolean[] granted = verificarPermisos(
				objectIdentifier,
				clazz,
				permissions,
				aclService,
				auth);
		for (int i = 0; i < granted.length; i++) {
			if (granted[i])
				return true;
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void filterGrantedAll(
			Collection<?> objectIdentifiers,
			ObjectIdentifierExtractor objectIdentifierExtractor,
			Class<?> clazz,
			Permission[] permissions,
			MutableAclService aclService,
			Authentication auth) {
		Iterator<?> it = objectIdentifiers.iterator();
		while (it.hasNext()) {
			Long objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(
					it.next());
			if (!isGrantedAll(
					objectIdentifier,
					clazz,
					permissions,
					aclService,
					auth))
				it.remove();
		}
	}
	public static boolean isGrantedAll(
			Long objectIdentifier,
			Class<?> clazz,
			Permission[] permissions,
			MutableAclService aclService,
			Authentication auth) {
		boolean[] granted = verificarPermisos(
				objectIdentifier,
				clazz,
				permissions,
				aclService,
				auth);
		boolean result = true;
		for (int i = 0; i < granted.length; i++) {
			if (!granted[i]) {
				result = false;
				break;
			}
		}
		return result;
	}



	private static void assignarPermisos(
			Sid sid,
			Class<?> objectClass,
			Serializable objectIdentifier,
			Permission[] permissions,
			MutableAclService aclService) {
		ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
		MutableAcl acl = null;
		try {
			acl = (MutableAcl)aclService.readAclById(oid);
		} catch (NotFoundException nfex) {
			acl = aclService.createAcl(oid);
		}
		for (Permission permission: permissions)
			acl.insertAce(
					acl.getEntries().size(),
					permission,
					sid,
					true);
		aclService.updateAcl(acl);
	}

	private static void revocarPermisos(
			Sid sid,
			Class<?> objectClass,
			Serializable objectIdentifier,
			Permission[] permissions,
			MutableAclService aclService) throws NotFoundException {
		ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
		MutableAcl acl = (MutableAcl)aclService.readAclById(oid);
		List<Integer> indexosPerEsborrar = new ArrayList<Integer>();
		int aceIndex = 0;
		for (AccessControlEntry ace: acl.getEntries()) {
			if (ace.getSid().equals(sid)) {
				for (Permission p: permissions) {
					if (p.equals(ace.getPermission()))
						indexosPerEsborrar.add(aceIndex);
				}
			}
			aceIndex++;
		}
		for (Integer index: indexosPerEsborrar)
			acl.deleteAce(index);
		aclService.updateAcl(acl);
	}

	private static boolean[] verificarPermisos(
			Long objectIdentifier,
			Class<?> clazz,
			Permission[] permissions,
			MutableAclService aclService,
			Authentication auth) {
		List<Sid> sids = new ArrayList<Sid>();
		sids.add(new PrincipalSid(auth.getName()));
		for (GrantedAuthority ga: auth.getAuthorities())
			sids.add(new GrantedAuthoritySid(ga.getAuthority()));
		boolean[] granted = new boolean[permissions.length];
		for (int i = 0; i < permissions.length; i++)
			granted[i] = false;
		List<Permission> ps = new ArrayList<Permission>();
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(
					clazz,
					objectIdentifier);
			Acl acl = aclService.readAclById(oid);
			for (int i = 0; i < permissions.length; i++) {
				try {
					ps.add(permissions[i]);
					granted[i] = acl.isGranted(
							ps,
							sids,
							false);
				} catch (NotFoundException ex) {}
			}
		} catch (NotFoundException ex) {}
		return granted;
	}

	public interface ObjectIdentifierExtractor<T> {
		public Long getObjectIdentifier(T object);
	}



	private static String getMapeigRol(String rol) {
		String propertyMapeig = PropertiesHelper.getProperties().getProperty(
				"es.caib.pinbal.mapeig.rol." + rol);
		if (propertyMapeig != null)
			return propertyMapeig;
		else
			return rol;
	}

}
