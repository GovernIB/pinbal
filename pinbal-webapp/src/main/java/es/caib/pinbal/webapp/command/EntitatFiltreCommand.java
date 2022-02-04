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
	private String unitatArrel;
	
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

	public String getUnitatArrel() {
		return unitatArrel;
	}
	public void setUnitatArrel(String unitatArrel) {
		this.unitatArrel = unitatArrel;
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

	// Elimina els espais en els camps de cerca
	public void eliminarEspaisCampsCerca() {
		this.codi = eliminarEspais(this.codi);
		this.nom = eliminarEspais(this.nom);
		this.cif = eliminarEspais(this.cif);
		this.unitatArrel = eliminarEspais(this.unitatArrel);
	}

	private String eliminarEspais(String str) {
		return (str != null) ? str.trim() : null;
	}

}
