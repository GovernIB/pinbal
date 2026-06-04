/**
 * 
 */
package es.caib.pinbal.back.validation;

import es.caib.pinbal.logic.intf.dto.ConsultaDto.DocumentTipus;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constraint de validació que controla que el nombre de
 * document d'identitat sigui vàlid. Els tipus de document
 * suportats son: NIF, DNI, NIE i CIF. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=DocumentIdentitatValidator.class)
public @interface DocumentIdentitat {

	String message() default "Número de document invàlid";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	DocumentTipus documentTipus();

}
