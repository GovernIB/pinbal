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
 * el el nom o alies de la clau privada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ClauPrivadaNoRepetidaValidator.class)
public @interface ClauPrivadaNoRepetida {

	String message() default "Ja existeix una altra clau privada amb aquest nom o alies";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
