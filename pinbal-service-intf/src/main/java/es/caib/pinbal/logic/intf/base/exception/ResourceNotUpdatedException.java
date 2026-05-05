package es.caib.pinbal.logic.intf.base.exception;

import lombok.Getter;

/**
 * Excepció que es llança quan s'ha intentat modificar un recurs però no s'ha
 * pogut per algun motiu.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class ResourceNotUpdatedException extends RuntimeException {

	private final Class<?> clazz;
	private final String pk;
	private final String reason;

	public ResourceNotUpdatedException(Class<?> clazz, String pk, String reason) {
		super("Couldn't update resource " + getResourceId(clazz, pk) + ": " + reason);
		this.clazz = clazz;
		this.pk = pk;
		this.reason = reason;
	}

	public ResourceNotUpdatedException(Class<?> clazz, String pk, String reason, Throwable t) {
		super("Couldn't update resource " + getResourceId(clazz, pk) + ": " + reason, t);
		this.clazz = clazz;
		this.pk = pk;
		this.reason = reason;
	}

	private static String getResourceId(Class<?> clazz, String pk) {
		return "(class=" + clazz.getName() + ", pk=" + pk + ")";
	}

}
