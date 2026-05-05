package es.caib.pinbal.logic.intf.base.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.data.domain.Sort;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotació per a configurar un recurs de l'API REST.
 * 
 * @author Límit Tecnologies
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResourceConfig {

	@AliasFor("name")
	String value() default "";
	@AliasFor("value")
	String name() default "";
	String resourceDescription() default "";
	String resourceDescriptionI18n() default "";
	String descriptionField() default "";
	String orderField() default "";
	ResourceSort[] defaultSortFields() default {};
	String[] quickFilterFields() default {};
	ResourceArtifact[] artifacts() default {};
	ResourceAccessConstraint[] accessConstraints() default {};
	String[] mappingIgnoredFields() default {};

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@interface ResourceSort {
		String field() default "";
		Sort.Direction direction() default Sort.Direction.ASC;
	}

}
