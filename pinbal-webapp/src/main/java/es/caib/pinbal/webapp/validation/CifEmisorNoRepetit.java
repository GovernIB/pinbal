/**
 * 
 */
package es.caib.pinbal.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constraint de validació que controla que no es repeteixi
 * el cif de l'emisor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=CifEmisorNoRepetitValidator.class)
public @interface CifEmisorNoRepetit {

	String message() default "Ja existeix una altra emisor amb aquest cif";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
