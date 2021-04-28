package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Objecte DTO amb informació d'un procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InformeGeneralEstatDto implements Serializable {

	private String entitatCodi;
	private String entitatNom;
	private String entitatCif;
	private String departament;
	private String procedimentCodi;
	private String procedimentNom;
	private String serveiCodi;
	private String serveiNom;
	private EmisorDto serveiEmisor;
	private Integer serveiUsuaris;
	private Integer peticionsCorrectes;
	private Integer peticionsErronees;
	private Integer peticionsPendents;

	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getEntitatNom() {
		return entitatNom;
	}
	public void setEntitatNom(String entitatNom) {
		this.entitatNom = entitatNom;
	}
	public String getEntitatCif() {
		return entitatCif;
	}
	public void setEntitatCif(String entitatCif) {
		this.entitatCif = entitatCif;
	}
	public String getDepartament() {
		return departament;
	}
	public void setDepartament(String departament) {
		this.departament = departament;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public String getProcedimentNom() {
		return procedimentNom;
	}
	public void setProcedimentNom(String procedimentNom) {
		this.procedimentNom = procedimentNom;
	}
	public String getServeiCodi() {
		return serveiCodi;
	}
	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
	}
	public String getServeiNom() {
		return serveiNom;
	}
	public void setServeiNom(String serveiNom) {
		this.serveiNom = serveiNom;
	}
	public EmisorDto getServeiEmisor() {
		return serveiEmisor;
	}
	public void setServeiEmisor(EmisorDto serveiEmisor) {
		this.serveiEmisor = serveiEmisor;
	}
	public Integer getServeiUsuaris() {
		return serveiUsuaris;
	}
	public void setServeiUsuaris(Integer serveiUsuaris) {
		this.serveiUsuaris = serveiUsuaris;
	}
	public Integer getPeticionsCorrectes() {
		return peticionsCorrectes;
	}
	public void setPeticionsCorrectes(Integer peticionsCorrectes) {
		this.peticionsCorrectes = peticionsCorrectes;
	}
	public Integer getPeticionsErronees() {
		return peticionsErronees;
	}
	public void setPeticionsErronees(Integer peticionsErronees) {
		this.peticionsErronees = peticionsErronees;
	}
	public Integer getPeticionsPendents() {
		return peticionsPendents;
	}
	public void setPeticionsPendents(Integer peticionsPendents) {
		this.peticionsPendents = peticionsPendents;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = 3986823331500016935L;

}
