package es.caib.pinbal.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

	private OrganGestorDto organGestor;
	private String codiSia;
	private Boolean valorCampAutomatizado;
	private ProcedimentClaseTramiteEnumDto valorCampClaseTramite;

	private List<ProcedimentServeiSimpleDto> serveisActius = new ArrayList<ProcedimentServeiSimpleDto>();

	public ProcedimentDto() {

	}

	public String getNomAmbCodi() {
		return nom + " (" + codi + ")";
	}
	public String getCodiNom() {
		return codi + " - " + nom;
	}

	public String getNomAmbDepartament() {
		if (departament != null && !departament.isEmpty()) {
			return nom + " (" + departament + ")";
		} else {
			return nom;
		}
	}
	
	public String getOrganGestorStr() {
		if (this.organGestor == null) {
			return "";
		}
		return this.organGestor.getCodi() + " - " + this.organGestor.getNom();
	}
	public boolean isOrganGestorActiu() {
		if (this.organGestor == null)
			return true;
		return this.organGestor.isActiu();
	}
	
	private static final long serialVersionUID = 3986823331500016935L;

}
