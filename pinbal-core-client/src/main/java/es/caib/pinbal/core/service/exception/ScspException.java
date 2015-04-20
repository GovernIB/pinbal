/**
 * 
 */
package es.caib.pinbal.core.service.exception;

/**
 * Excepció que es llança a causa d'errors al interactuar
 * amb les llibreries SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ScspException extends Exception {

	public ScspException() {
		super();
	}

	public ScspException(String message) {
		super(message);
	}

	public ScspException(String message, Throwable cause) {
		super(message, cause);
	}

}
