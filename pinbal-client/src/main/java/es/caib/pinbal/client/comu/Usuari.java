/**
 * 
 */
package es.caib.pinbal.client.comu;

/**
 * Informació d'un usuari associat a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Usuari {

	private String codi;
	private String nom;
	private String nif;
	
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
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}

}
