/**
 * 
 */
package es.caib.pinbal.client.comu;

import java.util.List;

/**
 * Informació d'un procediment associat a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Procediment {

	private String codi;
	private String nom;
	private boolean actiu;
	private List<Servei> serveis;
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
	public boolean isActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}
	public List<Servei> getServeis() {
		return serveis;
	}
	public void setServeis(List<Servei> serveis) {
		this.serveis = serveis;
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
