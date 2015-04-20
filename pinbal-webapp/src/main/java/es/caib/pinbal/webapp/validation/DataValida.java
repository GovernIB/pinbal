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
 * Constraint de validació que controla que la data és
 * vàlida. Per exemple: 31 de febrer és invàlid. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=DataValidaValidator.class)
public @interface DataValida {

	String message() default "Data no vàlida";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String format();

}
