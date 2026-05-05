package es.caib.pinbal.logic.intf.base.annotation;

import es.caib.pinbal.logic.intf.base.model.ResourceArtifactType;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotació per a configurar un artefacte.
 * 
 * @author Limit Tecnologies
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceArtifact {

	ResourceArtifactType type();
	String code();
	boolean requiresId() default false;
	Class<? extends Serializable> formClass() default Serializable.class;
	ResourceAccessConstraint[] accessConstraints() default {};

}
