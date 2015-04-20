/**
 * 
 */
package es.caib.pinbal.webapp.command;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per a filtrar els procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentFiltreCommand {

	private String codi;
	private String nom;
	private String departament;

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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
