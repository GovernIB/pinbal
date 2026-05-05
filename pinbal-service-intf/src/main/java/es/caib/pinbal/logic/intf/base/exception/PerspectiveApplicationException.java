package es.caib.pinbal.logic.intf.base.exception;

import lombok.Getter;

import java.io.Serializable;

/**
 * Excepció que es llança quan falla l'aplicació d'una perspectiva'.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class PerspectiveApplicationException extends RuntimeException {

	private final Class<?> resourceClass;
	private final Serializable id;
	private final String code;
	private final String errorMessage;

	public PerspectiveApplicationException(
			Class<?> resourceClass,
			Serializable id,
			String code,
			String message) {
		this(resourceClass, id, code, message, null);
	}

	public PerspectiveApplicationException(
			Class<?> resourceClass,
			Serializable id,
			String code,
			Throwable cause) {
		this(resourceClass, id, code, null, cause);
	}

	public PerspectiveApplicationException(
			Class<?> resourceClass,
			Serializable id,
			String code,
			String message,
			Throwable cause) {
		super("Perspective " + getPerspectiveId(resourceClass, id, code) + " application failed" + (message != null ? ": " + message : ""), cause);
		this.resourceClass = resourceClass;
		this.id = id;
		this.code = code;
		this.errorMessage = message;
	}

	public static String getPerspectiveId(Class<?> resourceClass, Serializable id, String code) {
		String processedId = id != null ? "id=" + id + ", " : "";
		return "(resourceClass=" + resourceClass.getName() + ", " + processedId + "code=" + code + ")";
	}

}
