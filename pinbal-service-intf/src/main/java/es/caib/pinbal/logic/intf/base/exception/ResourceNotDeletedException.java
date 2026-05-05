package es.caib.pinbal.logic.intf.base.exception;

import lombok.Getter;

/**
 * Excepció que es llança quan s'ha intentat esborrar un recurs però no s'ha
 * pogut per algun motiu.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class ResourceNotDeletedException extends RuntimeException {

	private final Class<?> clazz;
	private final String pk;
	private final String reason;

	public ResourceNotDeletedException(Class<?> clazz, String pk, String reason) {
		super("Couldn't delete resource " + getResourceId(clazz, pk) + ": " + reason);
		this.clazz = clazz;
		this.pk = pk;
		this.reason = reason;
	}

	public ResourceNotDeletedException(Class<?> clazz, String pk, String reason, Throwable t) {
		super("Couldn't delete resource " + getResourceId(clazz, pk) + ": " + reason, t);
		this.clazz = clazz;
		this.pk = pk;
		this.reason = reason;
	}

	private static String getResourceId(Class<?> clazz, String pk) {
		return "(class=" + clazz.getName() + ", pk=" + pk + ")";
	}

}
