package es.caib.pinbal.logic.intf.base.exception;

import lombok.Getter;

/**
 * Excepció que es llança quan no es pot identificar el permís especificat.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class UnknownPermissionException extends NotFoundException {

	public UnknownPermissionException(Object permission) {
		super("permission for " + permission);
	}

}
