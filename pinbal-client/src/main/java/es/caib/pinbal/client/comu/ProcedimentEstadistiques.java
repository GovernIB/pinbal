/**
 * 
 */
package es.caib.pinbal.client.comu;

import es.caib.pinbal.client.comu.ServeiEstadistiques.ConsultesOkError;

import java.util.List;

/**
 * Informació d'un procediment associat a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentEstadistiques {

	private String codi;
	private String nom;
	private boolean actiu;
	private ConsultesOkError consultesWeb;
	private ConsultesOkError consultesRecobriment;
	private ConsultesOkError consultesTotal;
	private TotalAcumulat totalWeb;
	private TotalAcumulat totalRecobriment;
	private List<ServeiEstadistiques> serveis;
	
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
	public ConsultesOkError getConsultesWeb() {
		return consultesWeb;
	}
	public void setConsultesWeb(ConsultesOkError consultesWeb) {
		this.consultesWeb = consultesWeb;
	}
	public ConsultesOkError getConsultesRecobriment() {
		return consultesRecobriment;
	}
	public void setConsultesRecobriment(ConsultesOkError consultesRecobriment) {
		this.consultesRecobriment = consultesRecobriment;
	}
	public ConsultesOkError getConsultesTotal() {
		return consultesTotal;
	}
	public void setConsultesTotal(ConsultesOkError consultesTotal) {
		this.consultesTotal = consultesTotal;
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
	public List<ServeiEstadistiques> getServeis() {
		return serveis;
	}
	public void setServeis(List<ServeiEstadistiques> serveis) {
		this.serveis = serveis;
	}

}
