/**
 * 
 */
package es.caib.pinbal.webapp.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.pinbal.core.dto.EntitatDto;

/**
 * Command per a filtrar els usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariFiltreCommand {

	private String codi;
	private String nif;
	private String nom;
	private String departament;

	private EntitatDto entitat = null;
	
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}

}
