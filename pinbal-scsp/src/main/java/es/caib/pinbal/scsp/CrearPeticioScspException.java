/**
 * 
 */
package es.caib.pinbal.scsp;

/**
 * Excepció que es llança quan una solucitud no és vàlida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class CrearPeticioScspException extends Exception {

	public CrearPeticioScspException(String message) {
		super(message);
	}

	public CrearPeticioScspException(String message, Throwable cause) {
		super(message, cause);
	}

}
