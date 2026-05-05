package es.caib.pinbal.logic.intf.base.exception;

import lombok.Getter;

/**
 * Excepció que es llança quan no es troba un component de l'aplicació.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class ComponentNotFoundException extends NotFoundException {

	private final Class<?> clazz;
	private final String type;

	public ComponentNotFoundException(Class<?> clazz) {
		super("Component " + getComponentInfo(clazz, null));
		this.clazz = clazz;
		this.type = null;
	}

	public ComponentNotFoundException(Class<?> clazz, String type) {
		super("Component " + getComponentInfo(clazz, type));
		this.clazz = clazz;
		this.type = type;
	}

	public ComponentNotFoundException(Class<?> clazz, String type, Throwable t) {
		super("Component " + getComponentInfo(clazz, type), t);
		this.clazz = clazz;
		this.type = type;
	}

	private static String getComponentInfo(Class<?> clazz, String type) {
		return type == null ? "(class=" + clazz.getName() + ")" : "(class=" + clazz.getName() + ", type=" + type + ")";
	}

}
