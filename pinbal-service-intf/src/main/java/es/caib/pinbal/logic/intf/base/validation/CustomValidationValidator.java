package es.caib.pinbal.logic.intf.base.validation;

import es.caib.pinbal.logic.intf.base.util.I18nUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Implementació del validador per l'anotació CustomValidation.
 *
 * @author Límit Tecnologies
 */
public class CustomValidationValidator implements ConstraintValidator<CustomValidation, Object> {

	private CustomValidation customValidation;

	@Override
	public void initialize(CustomValidation customValidation) {
		this.customValidation = customValidation;
	}

	@Override
	public boolean isValid(Object target, ConstraintValidatorContext context) {
		ValidatorAndValid<?> validatorAndValid = validate(target, context);
		if (customValidation.targetFields().length > 0) {
			for (String fieldName: customValidation.targetFields()) {
				addFieldConstraintViolation(
						context,
						fieldName,
						validatorAndValid.getValidator());
			}
			context.disableDefaultConstraintViolation();
		}
		return validatorAndValid.isValid();
	}

	@SneakyThrows
	private <T> ValidatorAndValid<T> validate(T target, ConstraintValidatorContext context) {
		CustomValidator<T> validator;
		if (customValidation.springBean()) {
			validator = CustomValidatorLocator.getInstance().getCustomValidatorWithClass(
					(Class<? extends CustomValidator<T>>)customValidation.customValidatorType());
		} else {
			Class<? extends CustomValidator<T>> validatorType = (Class<? extends CustomValidator<T>>)customValidation.customValidatorType();
			validator = validatorType.getConstructor().newInstance();
		}
		return new ValidatorAndValid<>(
				validator,
				validator.validate(target, context));
	}

	private void addFieldConstraintViolation(
			ConstraintValidatorContext context,
			String fieldName,
			CustomValidator<?> validator) {
		String fieldMessage = validator.getFieldMessage();
		String message = fieldMessage != null ? fieldMessage : customValidation.message();
		if (message.startsWith("{") && message.endsWith("}")) {
			message = I18nUtil.getInstance().getI18nMessage(
					message.substring(1, message.length() - 1).trim());
		}
		context.buildConstraintViolationWithTemplate(message).
				addPropertyNode(fieldName).
				addConstraintViolation();
	}

	@Getter
	@RequiredArgsConstructor
	private static class ValidatorAndValid<T> {
		private final CustomValidator<T> validator;
		private final boolean valid;
	}

}
