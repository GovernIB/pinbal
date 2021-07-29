/**
 * 
 */
package es.caib.pinbal.core.service.exception;

/**
 * Excepció que es llança quan hi ha errors durant la cridada al servei SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ConsultaScspComunicacioException extends ConsultaScspException {

	public ConsultaScspComunicacioException(String idPeticion) {
		super(idPeticion);
	}

	public ConsultaScspComunicacioException(String idPeticion, String message) {
		super(idPeticion, message);
	}

	public ConsultaScspComunicacioException(String idPeticion, Throwable cause) {
		super(idPeticion, cause);
	}

	public ConsultaScspComunicacioException(String idPeticion, String message, Throwable cause) {
		super(idPeticion, message, cause);
	}

}
