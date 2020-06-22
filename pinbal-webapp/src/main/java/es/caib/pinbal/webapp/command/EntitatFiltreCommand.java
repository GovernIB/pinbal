/**
 * 
 */
package es.caib.pinbal.webapp.command;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per a filtrar les entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatFiltreCommand {

	private String codi;
	private String nom;
	
	private String cif;
	private Boolean activa;
	private String tipus;
	
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
	public String getCif() {
		return cif;
	}
	public void setCif(String cif) {
		this.cif = cif;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public Boolean getActiva() {
		return activa;
	}
	public void setActiva(Boolean activa) {
		this.activa = activa;
	}

}
