/**
 * 
 */
package es.caib.pinbal.core.service.exception;

/**
 * Excepció que es llança al produir-se errors en la generació
 * del justificant d'una consulta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class JustificantGeneracioException extends Exception {

	public JustificantGeneracioException() {
		super();
	}

	public JustificantGeneracioException(String message) {
		super(message);
	}

	public JustificantGeneracioException(String message, Throwable cause) {
		super(message, cause);
	}

}
