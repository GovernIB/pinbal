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
 * Constraint de validació que controla que no es repeteixi
 * el codi de servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=CodiServeiNoRepetitValidator.class)
public @interface CodiServeiNoRepetit {

	String message() default "Ja existeix una altre servei amb aquest codi";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String campCodi();

	String campCreacio();

}
