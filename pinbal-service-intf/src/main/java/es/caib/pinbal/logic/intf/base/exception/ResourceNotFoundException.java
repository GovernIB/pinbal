package es.caib.pinbal.logic.intf.base.exception;

import lombok.Getter;

/**
 * Excepció que es llença quan no es troba el recurs.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class ResourceNotFoundException extends NotFoundException {

	private final Class<?> clazz;
	private final String id;

	public ResourceNotFoundException(Class<?> clazz, String id) {
		super("Resource " + getResourceId(clazz, id));
		this.clazz = clazz;
		this.id = id;
	}

	private static String getResourceId(Class<?> clazz, String pk) {
		return "(class=" + clazz.getName() + ", pk=" + pk + ")";
	}

}
