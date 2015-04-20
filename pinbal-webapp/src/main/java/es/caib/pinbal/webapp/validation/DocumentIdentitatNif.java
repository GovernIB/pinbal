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
 * Constraint de validació que controla que el nombre de
 * document d'identitat de tipus NIF sigui vàlid.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=DocumentIdentitatNifValidator.class)
public @interface DocumentIdentitatNif {

	String message() default "Número de document invàlid";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
