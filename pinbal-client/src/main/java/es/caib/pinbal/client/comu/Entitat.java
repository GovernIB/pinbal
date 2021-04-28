/**
 * 
 */
package es.caib.pinbal.client.comu;

import java.util.List;

/**
 * Informació d'una entitat associada a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Entitat {

	private String codi;
	private String nom;
	private String nif;
	private List<Departament> departaments;
	private TotalAcumulat totalWeb;
	private TotalAcumulat totalRecobriment;
	
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
	public List<Departament> getDepartaments() {
		return departaments;
	}
	public void setDepartaments(List<Departament> departaments) {
		this.departaments = departaments;
	}
	public TotalAcumulat getTotalWeb() {
		return totalWeb;
	}
	public void setTotalWeb(TotalAcumulat totalWeb) {
		this.totalWeb = totalWeb;
	}
	public TotalAcumulat getTotalRecobriment() {
		return totalRecobriment;
	}
	public void setTotalRecobriment(TotalAcumulat totalRecobriment) {
		this.totalRecobriment = totalRecobriment;
	}

}
