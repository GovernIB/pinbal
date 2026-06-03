package es.caib.pinbal.back.base.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Objecte d'error de l'API REST que es retorna quan es hi ha errors
 * de validació en la informació enviada amb la petició.
 * 
 * @author Límit Tecnologies
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldValidationErrorResponse extends ErrorResponse {

	protected List<ValidationError> validationErrors;

	public FieldValidationErrorResponse(
			int status,
			String message) {
		super(status, message);
	}

	public void addValidationError(
			String field,
			Object rejectedValue,
			String code,
			String[] codes,
			Object[] arguments,
			String title) {
		if (Objects.isNull(validationErrors)) {
			validationErrors = new ArrayList<>();
		}
		validationErrors.add(new ValidationError(field, rejectedValue, code, codes, arguments, title));
	}

	@Getter
	@Setter
	@RequiredArgsConstructor
	public static class ValidationError {
		private final String field;
		private final Object rejectedValue;
		private final String code;
		private final String[] codes;
		private final Object[] arguments;
		private final String title;
		public String getMessage() {
			return title;
		}
	}

}
