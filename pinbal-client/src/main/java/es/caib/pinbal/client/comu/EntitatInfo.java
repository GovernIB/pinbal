/**
 * 
 */
package es.caib.pinbal.client.comu;

/**
 * Informació d'una entitat associada a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatInfo {

	private String codi;
	private String nom;
	private String cif;
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

}
