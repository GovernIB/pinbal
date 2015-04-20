package es.caib.pinbal.core.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Objecte DTO amb informació d'un procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentDto implements Serializable {

	private Long id;

	private String codi;
	private String nom;
	private String departament;
	private boolean actiu;

	private Long entitatId;

	private List<String> serveisActius = new ArrayList<String>();

	public ProcedimentDto() {

	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDepartament() {
		return departament;
	}
	public void setDepartament(String departament) {
		this.departament = departament;
	}
	public boolean isActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public List<String> getServeisActius() {
		return serveisActius;
	}
	public void setServeisActius(List<String> serveisActius) {
		this.serveisActius = serveisActius;
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
