package es.caib.pinbal.logic.intf.base.exception;

import lombok.Getter;

/**
 * Excepció que es llança quan es produeixen errors en el mapeig recurs - entitat.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class ObjectMappingException extends RuntimeException {

	private final Class<?> sourceClass;
	private final Class<?> targetClass;
	private final String error;

	public ObjectMappingException(Class<?> sourceClass, Class<?> targetClass, String error) {
		super("Coudn't map " + sourceClass.getName() + " to " + targetClass.getName() + ": " + error);
		this.sourceClass = sourceClass;
		this.targetClass = targetClass;
		this.error = error;
	}

	public ObjectMappingException(Class<?> sourceClass, Class<?> targetClass, Throwable t) {
		super("Coudn't map " + sourceClass.getName() + " to " + targetClass.getName(), t);
		this.sourceClass = sourceClass;
		this.targetClass = targetClass;
		this.error = null;
	}

}
