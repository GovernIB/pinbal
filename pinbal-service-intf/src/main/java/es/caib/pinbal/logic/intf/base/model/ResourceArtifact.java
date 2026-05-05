package es.caib.pinbal.logic.intf.base.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Informació sobre un artefacte d'un recurs.
 * 
 * @author Límit Tecnologies
 */
@Getter
@Setter
@AllArgsConstructor
public class ResourceArtifact {

	private ResourceArtifactType type;
	private String code;
	private Boolean requiresId;
	@JsonIgnore
	private Class<? extends Serializable> formClass;

	public boolean isFormClassActive() {
		return formClass != null;
	}

}
