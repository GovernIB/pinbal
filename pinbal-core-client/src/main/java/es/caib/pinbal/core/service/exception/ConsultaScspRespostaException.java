/**
 * 
 */
package es.caib.pinbal.core.service.exception;

/**
 * Excepció que es llança quan la resposta a la petició SCSP ha retornat errors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ConsultaScspRespostaException extends ConsultaScspException {

	public ConsultaScspRespostaException(String idPeticion) {
		super(idPeticion);
	}

	public ConsultaScspRespostaException(String idPeticion, String message) {
		super(idPeticion, message);
	}

	public ConsultaScspRespostaException(String idPeticion, Throwable cause) {
		super(idPeticion, cause);
	}

	public ConsultaScspRespostaException(String idPeticion, String message, Throwable cause) {
		super(idPeticion, message, cause);
	}

}
