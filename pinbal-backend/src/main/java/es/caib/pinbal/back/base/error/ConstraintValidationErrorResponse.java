package es.caib.pinbal.back.base.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * Objecte d'error de l'API REST que es retorna quan es produeix un error
 * amb una restricció de base de dades.
 * 
 * @author Límit Tecnologies
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConstraintValidationErrorResponse extends ErrorResponse {

	private final String constraintName;

	public ConstraintValidationErrorResponse(
			int status,
			String message,
			String constraintName) {
		super(status, message);
		this.constraintName = constraintName;
	}

	public boolean isConstraintValidationError() {
		return true;
	}

}
