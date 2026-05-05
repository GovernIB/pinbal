package es.caib.pinbal.logic.intf.base.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Excepció que es llança quan es processa l'event onChange provinent del front
 * i es detecta que, per a poder processar-lo, és necessari disposar de la
 * resposta a una pregunta que no ens ha arribat.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class AnswerRequiredException extends RuntimeException {

	private final Class<?> clazz;
	private final String answerCode;
	private final String question;
	private final Object questionData;
	private final Boolean trueFalseAnswerRequired;
	private Boolean stringAnswerFieldActive;
	private List<CustomAnswer> availableAnswers;

	public AnswerRequiredException(
			Class<?> clazz,
			String answerCode,
			String question) {
		this(clazz, answerCode, question, (Object)null);
	}
	public AnswerRequiredException(
			Class<?> clazz,
			String answerCode,
			String question,
			Object questionData) {
		super("Answer '" + answerCode + "' required to process changes");
		this.clazz = clazz;
		this.answerCode = answerCode;
		this.question = question;
		this.questionData = questionData;
		this.trueFalseAnswerRequired = null;
	}

	public AnswerRequiredException(
			Class<?> clazz,
			String answerCode,
			String question,
			List<CustomAnswer> availableAnswers) {
		this(clazz, answerCode, question, null, availableAnswers);
	}
	public AnswerRequiredException(
			Class<?> clazz,
			String answerCode,
			String question,
			Object questionData,
			List<CustomAnswer> availableAnswers) {
		super("Answer '" + answerCode + "' required to process changes");
		this.clazz = clazz;
		this.answerCode = answerCode;
		this.question = question;
		this.questionData = questionData;
		this.trueFalseAnswerRequired = availableAnswers == null;
		this.availableAnswers = availableAnswers;
	}

	public AnswerRequiredException(
			Class<?> clazz,
			String answerCode,
			String question,
			Boolean trueFalseAnswerRequired,
			Boolean stringAnswerFieldActive) {
		this(clazz, answerCode, question, null, trueFalseAnswerRequired, stringAnswerFieldActive);
	}
	public AnswerRequiredException(
			Class<?> clazz,
			String answerCode,
			String question,
			Object questionData,
			Boolean trueFalseAnswerRequired,
			Boolean stringAnswerFieldActive) {
		super("Answer '" + answerCode + "' required to process changes");
		this.clazz = clazz;
		this.answerCode = answerCode;
		this.question = question;
		this.questionData = questionData;
		this.trueFalseAnswerRequired = trueFalseAnswerRequired;
		this.stringAnswerFieldActive = stringAnswerFieldActive;
	}

	public AnswerRequiredError toAnswerRequiredError() {
		return new AnswerRequiredError(
				answerCode,
				question,
				questionData,
				trueFalseAnswerRequired,
				stringAnswerFieldActive,
				availableAnswers);
	}

	@Getter
	@AllArgsConstructor
	public static class AnswerRequiredError {
		private String answerCode;
		private String question;
		private Object questionData;
		private Boolean trueFalseAnswerRequired;
		private Boolean stringAnswerFieldActive;
		private List<CustomAnswer> availableAnswers;
	}

	@Getter
	public static class AnswerValue {
		private String stringValue;
		private Boolean booleanValue;
		public AnswerValue() {
			super();
		}
		public AnswerValue(Object value) {
			super();
			if (value != null) {
				if (value instanceof Boolean) {
					this.booleanValue = (Boolean)value;
				} else {
					this.stringValue = value.toString();
				}
			}
		}
		public AnswerValue(String stringValue) {
			super();
			this.stringValue = stringValue;
		}
		public AnswerValue(Boolean booleanValue) {
			super();
			this.booleanValue = booleanValue;
		}
		public boolean valueAsBoolean() {
			return booleanValue != null && booleanValue;
		}
	}

	@Getter
	@AllArgsConstructor
	public static class CustomAnswer {
		private String value;
		private String description;
	}

}
