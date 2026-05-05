package es.caib.pinbal.logic.intf.base.exception;

import es.caib.pinbal.logic.intf.base.model.ResourceArtifactType;
import lombok.Getter;

/**
 * Excepció que es llença quan no es troba el formulari de l'artefacte.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class ArtifactFormNotFoundException extends NotFoundException {

	private final Class<?> resourceClass;
	private final ResourceArtifactType type;
	private final String code;

	public ArtifactFormNotFoundException(
			Class<?> resourceClass,
			ResourceArtifactType type,
			String code) {
		super("Artifact " + getArtifactId(resourceClass, type, code));
		this.resourceClass = resourceClass;
		this.type = type;
		this.code = code;
	}

	private static String getArtifactId(
			Class<?> resourceClass,
			ResourceArtifactType type,
			String code) {
		return "(resourceClass=" + resourceClass.getName() + ", type=" + type + ", code=" + code + ")";
	}

}
