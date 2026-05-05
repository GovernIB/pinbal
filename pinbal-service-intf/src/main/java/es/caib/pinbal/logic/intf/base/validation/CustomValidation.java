package es.caib.pinbal.logic.intf.base.validation;

import es.caib.pinbal.logic.intf.base.config.BaseConfig;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validació personalitzada.
 *
 * @author Límit Tecnologies
 * @see CustomValidator
 */
@Documented
@Constraint(validatedBy = CustomValidationValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CustomValidation.List.class)
public @interface CustomValidation {

	Class<? extends CustomValidator<?>> customValidatorType();
	boolean springBean() default false;
	String[] targetFields() default {};

	String message() default "{" + BaseConfig.BASE_PACKAGE + ".validation.constraints.CustomValidation}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

	/**
	 * Defines several CustomValidation annotations on the same element.
	 *
	 * @see CustomValidation
	 */
	@Documented
	@Target({ ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@interface List {
		CustomValidation[] value();
	}

}
