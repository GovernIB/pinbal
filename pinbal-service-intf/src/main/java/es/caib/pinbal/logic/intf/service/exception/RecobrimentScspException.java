/**
 * 
 */
package es.caib.pinbal.logic.intf.service.exception;

/**
 * Excepció que es llança quan es reb un SCSP exception en una
 * petició al servei de recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class RecobrimentScspException extends Exception {

	public RecobrimentScspException() {
		super();
	}

	public RecobrimentScspException(String message) {
		super(message);
	}

	public RecobrimentScspException(String message, Throwable cause) {
		super(message, cause);
	}

}
