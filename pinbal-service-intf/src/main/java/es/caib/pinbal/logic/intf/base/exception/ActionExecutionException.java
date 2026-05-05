package es.caib.pinbal.logic.intf.base.exception;

import lombok.Getter;

import java.io.Serializable;

/**
 * Excepció que es llança quan falla l'execució d'una acció.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class ActionExecutionException extends RuntimeException {

	private final Class<?> resourceClass;
	private final Serializable id;
	private final String code;
	private final String errorMessage;

	public ActionExecutionException(
			Class<?> resourceClass,
			Serializable id,
			String code,
			String message) {
		this(resourceClass, id, code, message, null);
	}

	public ActionExecutionException(
			Class<?> resourceClass,
			Serializable id,
			String code,
			Throwable cause) {
		this(resourceClass, id, code, null, cause);
	}

	public ActionExecutionException(
			Class<?> resourceClass,
			Serializable id,
			String code,
			String message,
			Throwable cause) {
		super("Action " + getReportId(resourceClass, id, code) + " execution failed" + (message != null ? ": " + message : ""), cause);
		this.resourceClass = resourceClass;
		this.id = id;
		this.code = code;
		this.errorMessage = message;
	}

	public static String getReportId(Class<?> resourceClass, Serializable id, String code) {
		String processedId = id != null ? "id=" + id + ", " : "";
		return "(resourceClass=" + resourceClass.getName() + ", " + processedId + "code=" + code + ")";
	}

}
