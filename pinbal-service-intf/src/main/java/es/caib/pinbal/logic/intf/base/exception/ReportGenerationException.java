package es.caib.pinbal.logic.intf.base.exception;

import lombok.Getter;

import java.io.Serializable;

/**
 * Excepció que es llança quan falla la generació d'un informe.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class ReportGenerationException extends RuntimeException {

	private final Class<?> resourceClass;
	private final Serializable id;
	private final String code;
	private final String errorMessage;

	public ReportGenerationException(
			Class<?> resourceClass,
			Serializable id,
			String code,
			String message) {
		this(resourceClass, id, code, message, null);
	}

	public ReportGenerationException(
			Class<?> resourceClass,
			Serializable id,
			String code,
			Throwable cause) {
		this(resourceClass, id, code, null, cause);
	}

	public ReportGenerationException(
			Class<?> resourceClass,
			String message,
			Throwable cause) {
		this(resourceClass, null, null, message, cause);
	}

	public ReportGenerationException(
			Class<?> resourceClass,
			String message) {
		this(resourceClass, null, null, message, null);
	}

	public ReportGenerationException(
			Class<?> resourceClass,
			Serializable id,
			String code,
			String message,
			Throwable cause) {
		super("Error generating report " + getReportId(resourceClass, id, code) + " generation failed" + (message != null ? ": " + message : ""), cause);
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
