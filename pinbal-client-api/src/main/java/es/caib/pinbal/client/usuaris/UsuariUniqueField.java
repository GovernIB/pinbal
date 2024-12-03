package es.caib.pinbal.client.usuaris;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UsuariUniqueFieldValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UsuariUniqueField {

    String message() default "Un, i només un, dels camps codi, nif or nom ha d'estar emplenat.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}