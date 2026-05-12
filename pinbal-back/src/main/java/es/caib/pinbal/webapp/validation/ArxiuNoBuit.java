/**
 * 
 */
package es.caib.pinbal.webapp.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Constraint de validació que controla que un arxiu no estigui
 * buit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ArxiuNoBuitValidator.class)
public @interface ArxiuNoBuit {

	String message() default "Aquest camp és obligatori";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
