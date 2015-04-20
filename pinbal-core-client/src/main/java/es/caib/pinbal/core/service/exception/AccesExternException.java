/**
 * 
 */
package es.caib.pinbal.core.service.exception;

/**
 * Excepció que es llança quan la consulta no existeix.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class AccesExternException extends Exception {

	public AccesExternException() {
		super();
	}

	public AccesExternException(String message) {
		super(message);
	}

	public AccesExternException(String message, Throwable cause) {
		super(message, cause);
	}

}
