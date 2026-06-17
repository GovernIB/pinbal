/**
 * 
 */
package es.caib.pinbal.client.recobriment;

import lombok.Getter;

/**
 * Excepció genèrica provinent de l'API REST.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@SuppressWarnings("serial")
public class RecobrimentException extends RuntimeException {

	private int status;
	private String trace;

	protected RecobrimentException(
			String message,
			int status,
			String trace) {
		super(message);
		this.status = status;
		this.trace = trace;
	}

}
