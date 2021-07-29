/**
 * 
 */
package es.caib.pinbal.core.service.exception;

/**
 * Excepció que es llança quan hi ha errors en la generació del missatge de
 * petició SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ConsultaScspGeneracioException extends ConsultaScspException {

	public ConsultaScspGeneracioException() {
		super(null);
	}

	public ConsultaScspGeneracioException(String message) {
		super(null, message);
	}

	public ConsultaScspGeneracioException(Throwable cause) {
		super(null, cause);
	}

	public ConsultaScspGeneracioException(String message, Throwable cause) {
		super(null, message, cause);
	}

}
