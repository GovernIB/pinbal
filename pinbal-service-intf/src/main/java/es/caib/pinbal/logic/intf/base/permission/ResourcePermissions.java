package es.caib.pinbal.logic.intf.base.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Informació dels possibles permisos sobre un recurs.
 * 
 * @author Límit Tecnologies
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourcePermissions {

	protected boolean readGranted;
	protected boolean writeGranted;
	protected boolean createGranted;
	protected boolean deleteGranted;

	public static ResourcePermissions readOnly() {
		ResourcePermissions rp = new ResourcePermissions();
		rp.setReadGranted(true);
		return rp;
	}

	public static ResourcePermissions writeOnly() {
		ResourcePermissions rp = new ResourcePermissions();
		rp.setWriteGranted(true);
		return rp;
	}

	public static ResourcePermissions createOnly() {
		ResourcePermissions rp = new ResourcePermissions();
		rp.setCreateGranted(true);
		return rp;
	}

	public static ResourcePermissions deleteOnly() {
		ResourcePermissions rp = new ResourcePermissions();
		rp.setDeleteGranted(true);
		return rp;
	}

}
