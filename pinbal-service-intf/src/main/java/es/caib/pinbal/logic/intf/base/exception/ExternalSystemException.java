package es.caib.pinbal.logic.intf.base.exception;

import lombok.Getter;

/**
 * Excepció que es llança quan es produeix un error accedint a un sistema extern.
 *
 * @author Límit Tecnologies
 */
@Getter
public class ExternalSystemException extends RuntimeException {

	private final String systemId;

	public ExternalSystemException(String systemId, String message, Throwable cause) {
		super(getSystemId(systemId) + " " + message, cause);
		this.systemId = systemId;
	}

	private static String getSystemId(String systemId) {
		return systemId != null ? "[" + systemId + "]" : "";
	}

}
