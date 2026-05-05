package es.caib.pinbal.logic.intf.base.permission;

import org.springframework.security.acls.model.Permission;

/**
 * Enumeració amb els possibles permisos que es poden assignar a un recurs.
 * 
 * @author Límit Tecnologies
 */
public enum PermissionEnum {
	READ,
	WRITE,
	CREATE,
	DELETE,
	ADMINISTRATION,
	PERM0,
	PERM1,
	PERM2,
	PERM3,
	PERM4,
	PERM5,
	PERM6,
	PERM7,
	PERM8,
	PERM9,
	PERMX,
	NULL;

	public static Permission toPermission(PermissionEnum permissionEnum) {
		switch(permissionEnum) {
		case READ: return ExtendedPermission.READ;
		case WRITE: return ExtendedPermission.WRITE;
		case CREATE: return ExtendedPermission.CREATE;
		case DELETE: return ExtendedPermission.DELETE;
		case ADMINISTRATION: return ExtendedPermission.ADMINISTRATION;
		case PERM0: return ExtendedPermission.PERM0;
		case PERM1: return ExtendedPermission.PERM1;
		case PERM2: return ExtendedPermission.PERM2;
		case PERM3: return ExtendedPermission.PERM3;
		case PERM4: return ExtendedPermission.PERM4;
		case PERM5: return ExtendedPermission.PERM5;
		case PERM6: return ExtendedPermission.PERM6;
		case PERM7: return ExtendedPermission.PERM7;
		case PERM8: return ExtendedPermission.PERM8;
		case PERM9: return ExtendedPermission.PERM9;
		case PERMX: return ExtendedPermission.PERMX;
		default: return null;
		}
	}

	public static PermissionEnum fromPermission(Permission permission) {
		if (ExtendedPermission.READ.equals(permission)) {
			return READ;
		} else if (ExtendedPermission.WRITE.equals(permission)) {
			return WRITE;
		} else if (ExtendedPermission.CREATE.equals(permission)) {
			return CREATE;
		} else if (ExtendedPermission.DELETE.equals(permission)) {
			return DELETE;
		} else if (ExtendedPermission.ADMINISTRATION.equals(permission)) {
			return ADMINISTRATION;
		} else if (ExtendedPermission.PERM0.equals(permission)) {
			return PERM0;
		} else if (ExtendedPermission.PERM1.equals(permission)) {
			return PERM1;
		} else if (ExtendedPermission.PERM2.equals(permission)) {
			return PERM2;
		} else if (ExtendedPermission.PERM3.equals(permission)) {
			return PERM3;
		} else if (ExtendedPermission.PERM4.equals(permission)) {
			return PERM4;
		} else if (ExtendedPermission.PERM5.equals(permission)) {
			return PERM5;
		} else if (ExtendedPermission.PERM6.equals(permission)) {
			return PERM6;
		} else if (ExtendedPermission.PERM7.equals(permission)) {
			return PERM7;
		} else if (ExtendedPermission.PERM8.equals(permission)) {
			return PERM8;
		} else if (ExtendedPermission.PERM9.equals(permission)) {
			return PERM9;
		} else if (ExtendedPermission.PERMX.equals(permission)) {
			return PERMX;
		} else {
			return null;
		}
	}

}
