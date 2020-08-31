package es.caib.pinbal.core.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Objecte DTO amb informació d'un procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ProcedimentDto extends AbstractIdentificable<Long> implements Serializable {

	private Long id;

	private String codi;
	private String nom;
	private String departament;
	private boolean actiu;

	private Long entitatId;
	private String entitatNom;

	private String organGestor;
	private String codiSia;

	private List<ProcedimentServeiSimpleDto> serveisActius = new ArrayList<ProcedimentServeiSimpleDto>();

	public ProcedimentDto() {

	}

	public String getNomAmbDepartament() {
		if (departament != null && !departament.isEmpty()) {
			return nom + " (" + departament + ")";
		} else {
			return nom;
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = 3986823331500016935L;

}
