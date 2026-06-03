package es.caib.pinbal.back.base.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * Objecte d'error de l'API REST que es retorna quan es cancel·la alguna
 * acció que involucra modificació de dades.
 * 
 * @author Límit Tecnologies
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModificationCanceledErrorResponse extends ErrorResponse {

	private final String action;

	public ModificationCanceledErrorResponse(
			int status,
			String message,
			String action) {
		super(status, message);
		this.action = action;
	}

	public boolean isModificationCanceledError() {
		return true;
	}

}
