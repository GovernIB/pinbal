package es.caib.pinbal.logic.intf.base.exception;

import lombok.Getter;

/**
 * Excepció que es llança quan no es troba un camp del recurs.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class ResourceFieldNotFoundException extends NotFoundException {

	private final Class<?> clazz;
	private final String fieldName;

	public ResourceFieldNotFoundException(Class<?> clazz, String fieldName) {
		super("Resource field " + getResourceFieldId(clazz, fieldName));
		this.clazz = clazz;
		this.fieldName = fieldName;
	}

	private static String getResourceFieldId(Class<?> clazz, String field) {
		return "(class=" + clazz.getName() + ", field=" + field + ")";
	}

}
