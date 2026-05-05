package es.caib.pinbal.logic.intf.base.validation;

import javax.validation.ConstraintValidatorContext;

/**
 * Interfície per a validacions personalitzades.
 *
 * @param <T> la classe sobre la que s'executa la validació.
 *
 * @author Límit Tecnologies
 */
public interface CustomValidator<T> {

	boolean validate(T value, ConstraintValidatorContext context);

	default String getFieldMessage() {
		return "{" + this.getClass().getName() + ".fieldMessage}";
	}

}
