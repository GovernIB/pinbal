/**
 * 
 */
package es.caib.pinbal.core.service.exception;

/**
 * Excepció que es llança quan les dades de la petició no son vàlides.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ValidacioDadesPeticioException extends Exception {

	public ValidacioDadesPeticioException() {
		super();
	}

	public ValidacioDadesPeticioException(String message) {
		super(message);
	}

	public ValidacioDadesPeticioException(String message, Throwable cause) {
		super(message, cause);
	}

}
