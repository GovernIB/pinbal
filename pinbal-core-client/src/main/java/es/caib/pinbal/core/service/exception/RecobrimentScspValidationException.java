/**
 * 
 */
package es.caib.pinbal.core.service.exception;

/**
 * Excepció que es llança quan es reb un SCSP exception de validació en una
 * petició al servei de recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class RecobrimentScspValidationException extends RecobrimentScspException {

	public RecobrimentScspValidationException() {
		super();
	}

	public RecobrimentScspValidationException(String message) {
		super(message);
	}

	public RecobrimentScspValidationException(String message, Throwable cause) {
		super(message, cause);
	}

}
