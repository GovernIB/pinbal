/**
 * 
 */
package es.caib.pinbal.webapp.command;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per a filtrar les organs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OrganGestorFiltreCommand {
	private String codi;
	private String nom;

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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
