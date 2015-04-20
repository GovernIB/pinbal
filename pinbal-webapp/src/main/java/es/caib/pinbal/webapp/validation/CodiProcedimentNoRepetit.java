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
 * el codi de procediment dins d'una determinada entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=CodiProcedimentNoRepetitValidator.class)
public @interface CodiProcedimentNoRepetit {

	String message() default "Ja existeix un altre procediment amb aquest codi";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String campId();

	String campEntitatId();

	String campCodi();

}
