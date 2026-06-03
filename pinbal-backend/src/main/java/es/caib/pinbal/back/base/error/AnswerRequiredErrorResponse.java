package es.caib.pinbal.back.base.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import lombok.Getter;
import lombok.Setter;

/**
 * Objecte d'error de l'API REST que es retorna quan es requereix
 * alguna resposta en una petició onChange.
 * 
 * @author Límit Tecnologies
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerRequiredErrorResponse extends ErrorResponse {

	protected AnswerRequiredException.AnswerRequiredError answerRequiredError;

	public AnswerRequiredErrorResponse(
			int status,
			String message,
			AnswerRequiredException.AnswerRequiredError answerRequiredError) {
		super(status, message);
		this.answerRequiredError = answerRequiredError;
	}

	public boolean isAnswerRequiredError() {
		return true;
	}

}
