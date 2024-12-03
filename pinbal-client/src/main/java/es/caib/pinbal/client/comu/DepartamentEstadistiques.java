/**
 * 
 */
package es.caib.pinbal.client.comu;

import java.util.List;

/**
 * Informació d'un departament associat a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DepartamentEstadistiques {

	private String codi;
	private String nom;
	private List<ProcedimentEstadistiques> procediments;
	private List<Usuari> usuaris;
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
	public List<ProcedimentEstadistiques> getProcediments() {
		return procediments;
	}
	public void setProcediments(List<ProcedimentEstadistiques> procediments) {
		this.procediments = procediments;
	}
	public List<Usuari> getUsuaris() {
		return usuaris;
	}
	public void setUsuaris(List<Usuari> usuaris) {
		this.usuaris = usuaris;
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
