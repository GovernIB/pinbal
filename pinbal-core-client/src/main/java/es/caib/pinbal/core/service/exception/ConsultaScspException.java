/**
 * 
 */
package es.caib.pinbal.core.service.exception;

/**
 * Excepció que es llança degut a errors en les consultes SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public abstract class ConsultaScspException extends Exception {

	private String idPeticion;

	public ConsultaScspException(String idPeticion) {
		super();
		this.idPeticion = idPeticion;
	}

	public ConsultaScspException(String idPeticion, String message) {
		super(message);
		this.idPeticion = idPeticion;
	}

	public ConsultaScspException(String idPeticion, Throwable cause) {
		super(cause);
		this.idPeticion = idPeticion;
	}

	public ConsultaScspException(String idPeticion, String message, Throwable cause) {
		super(message, cause);
		this.idPeticion = idPeticion;
	}

	public String getIdPeticion() {
		return idPeticion;
	}

}
