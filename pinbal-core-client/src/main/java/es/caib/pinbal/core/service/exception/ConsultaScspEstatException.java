/**
 * 
 */
package es.caib.pinbal.core.service.exception;

/**
 * Excepció que es llança quan hi ha errors recuperant l'estat d'una consulta
 * SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ConsultaScspEstatException extends ConsultaScspException {

	public ConsultaScspEstatException(String idPeticion) {
		super(idPeticion);
	}

	public ConsultaScspEstatException(String idPeticion, String message) {
		super(idPeticion, message);
	}

	public ConsultaScspEstatException(String idPeticion, Throwable cause) {
		super(idPeticion, cause);
	}

	public ConsultaScspEstatException(String idPeticion, String message, Throwable cause) {
		super(idPeticion, message, cause);
	}

}
