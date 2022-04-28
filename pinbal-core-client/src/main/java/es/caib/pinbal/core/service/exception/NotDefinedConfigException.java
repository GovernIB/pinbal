/**
 * 
 */
package es.caib.pinbal.core.service.exception;

/**
 * Excepció que es llança quan s'intenta accedir a una propietat de configuració que no
 * està definida dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotDefinedConfigException extends RuntimeException {

	private String message;

	public NotDefinedConfigException(String key) {
		super("Trying to get a property not defined in database: " + key);
		this.message = super.getMessage();
	}

}
