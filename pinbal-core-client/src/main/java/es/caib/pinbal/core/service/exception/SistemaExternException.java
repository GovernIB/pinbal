package es.caib.pinbal.core.service.exception;

/**
 * Excepció que es produeix al accedir a un sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class SistemaExternException extends RuntimeException {

	private String sistemaExternCodi;

	public SistemaExternException(
			String sistemaExternCodi,
			String message) {
		super(message);
		this.sistemaExternCodi = sistemaExternCodi;
	}

	public SistemaExternException(
			String sistemaExternCodi,
			String message,
			Throwable cause) {
		super(message, cause);
		this.sistemaExternCodi = sistemaExternCodi;
	}

	public String getSistemaExternCodi() {
		return sistemaExternCodi;
	}

}