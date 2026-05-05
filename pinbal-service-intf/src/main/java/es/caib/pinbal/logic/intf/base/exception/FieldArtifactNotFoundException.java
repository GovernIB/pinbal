package es.caib.pinbal.logic.intf.base.exception;

import es.caib.pinbal.logic.intf.base.model.FieldArtifactType;
import lombok.Getter;

/**
 * Excepció que es llença quan no es troba l'artefacte del camp.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class FieldArtifactNotFoundException extends NotFoundException {

	private final Class<?> resourceClass;
	private final FieldArtifactType type;
	private final String fieldName;

	public FieldArtifactNotFoundException(
			Class<?> resourceClass,
			FieldArtifactType type,
			String fieldName) {
		super("Field artifact " + getFieldArtifactId(resourceClass, type, fieldName));
		this.resourceClass = resourceClass;
		this.type = type;
		this.fieldName = fieldName;
	}

	private static String getFieldArtifactId(
			Class<?> resourceClass,
			FieldArtifactType type,
			String fieldName) {
		return "(resourceClass=" + resourceClass.getName() + ", type=" + type + ", fieldName=" + fieldName + ")";
	}

}
