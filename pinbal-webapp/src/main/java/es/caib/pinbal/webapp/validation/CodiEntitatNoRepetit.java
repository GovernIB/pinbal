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
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=CodiEntitatNoRepetitValidator.class)
public @interface CodiEntitatNoRepetit {

	String message() default "Ja existeix una altra entitat amb aquest codi";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String campId();

	String campCodi();

}
