package es.caib.pinbal.logic.intf.base.exception;

import lombok.Getter;

/**
 * Excepció que es llança quan s'ha intentat crear un recurs però no s'ha
 * pogut per algun motiu.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class ResourceNotCreatedException extends RuntimeException {

	private final Class<?> clazz;
	private final String reason;

	public ResourceNotCreatedException(Class<?> clazz, String reason) {
		super("Couldn't create resource " + getResourceId(clazz) + ": " + reason);
		this.clazz = clazz;
		this.reason = reason;
	}

	public ResourceNotCreatedException(Class<?> clazz, String reason, Throwable t) {
		super("Couldn't create resource " + getResourceId(clazz) + ": " + reason, t);
		this.clazz = clazz;
		this.reason = reason;
	}

	private static String getResourceId(Class<?> clazz) {
		return "(class=" + clazz.getName() + ")";
	}

}
