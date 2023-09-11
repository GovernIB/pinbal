/**
 * 
 */
package es.caib.pinbal.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command per a validar les dades d'una regla.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Documented
@Constraint(validatedBy = ServeiGrupValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ServeiGrup {

	String message() default "servei.camp.grup.error";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}