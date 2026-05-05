package es.caib.pinbal.logic.intf.base.permission;

import es.caib.pinbal.logic.intf.base.exception.UnknownPermissionException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

/**
 * Permisos addicionals als que proporciona per defecte Spring Security.
 * 
 * @author Límit Tecnologies
 */
public class ExtendedPermission extends BasePermission {

	public static final Permission PERM0 = new ExtendedPermission(1 << 5, '0');				// 32
	public static final Permission PERM1 = new ExtendedPermission(1 << 6, '1');				// 64
	public static final Permission PERM2 = new ExtendedPermission(1 << 7, '2');				// 128
	public static final Permission PERM3 = new ExtendedPermission(1 << 8, '3');				// 256
	public static final Permission PERM4 = new ExtendedPermission(1 << 9, '4'); 			// 512
	public static final Permission PERM5 = new ExtendedPermission(1 << 10, '5');			// 1024
	public static final Permission PERM6 = new ExtendedPermission(1 << 11, '6');			// 2048
	public static final Permission PERM7 = new ExtendedPermission(1 << 12, '7');			// 4096
	public static final Permission PERM8 = new ExtendedPermission(1 << 13, '8');			// 8192
	public static final Permission PERM9 = new ExtendedPermission(1 << 14, '9');			// 16384
	public static final Permission PERMX = new ExtendedPermission(1 << 15, 'X');			// 32768

	protected ExtendedPermission(int mask) {
		super(mask);
	}

	protected ExtendedPermission(int mask, char code) {
		super(mask, code);
	}

	public static String getName(int mask) {
		return getEnumValue(mask).name();
	}

	public static PermissionEnum getEnumValue(int mask) {
		switch(mask) {
		case 1: return PermissionEnum.READ;
		case 1 << 1: return PermissionEnum.WRITE;
		case 1 << 2: return PermissionEnum.CREATE;
		case 1 << 3: return PermissionEnum.DELETE;
		case 1 << 4: return PermissionEnum.ADMINISTRATION;
		case 1 << 5: return PermissionEnum.PERM0;
		case 1 << 6: return PermissionEnum.PERM1;
		case 1 << 7: return PermissionEnum.PERM2;
		case 1 << 8: return PermissionEnum.PERM3;
		case 1 << 9: return PermissionEnum.PERM4;
		case 1 << 10: return PermissionEnum.PERM5;
		case 1 << 11: return PermissionEnum.PERM6;
		case 1 << 12: return PermissionEnum.PERM7;
		case 1 << 13: return PermissionEnum.PERM8;
		case 1 << 14: return PermissionEnum.PERM9;
		case 1 << 15: return PermissionEnum.PERMX;
		}
		throw new UnknownPermissionException("Permís no definit per a la màscara (mask=" + mask + ")");
	}

	public static Permission fromEnumValue(PermissionEnum permissionEnum) {
		switch (permissionEnum) {
		case READ: return READ;
		case WRITE: return WRITE;
		case CREATE: return CREATE;
		case DELETE: return DELETE;
		case ADMINISTRATION: return ADMINISTRATION;
		case PERM0: return PERM0;
		case PERM1: return PERM1;
		case PERM2: return PERM2;
		case PERM3: return PERM3;
		case PERM4: return PERM4;
		case PERM5: return PERM5;
		case PERM6: return PERM6;
		case PERM7: return PERM7;
		case PERM8: return PERM8;
		case PERM9: return PERM9;
		case PERMX: return PERMX;
		default:
			return null;
		}
	}

	public static ExtendedPermission fromMask(int mask) {
		return new ExtendedPermission(mask);
	}

	public static ExtendedPermission fromMaskAndCode(int mask, char code) {
		return new ExtendedPermission(mask, code);
	}

}
