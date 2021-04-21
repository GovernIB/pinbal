/**
 * 
 */
package es.caib.pinbal.client.comu;

/**
 * Informació d'un servei associat a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Servei {

	private String codi;
	private String nom;
	private String emisor;
	private Integer usuarisAmbPermisos;
	private Integer consultesOk;
	private Integer consultesError;
	private ConsultesOkError consultesWeb;
	private ConsultesOkError consultesRecobriment;
	private ConsultesOkError consultesTotal;
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
	public String getEmisor() {
		return emisor;
	}
	public void setEmisor(String emisor) {
		this.emisor = emisor;
	}
	public Integer getUsuarisAmbPermisos() {
		return usuarisAmbPermisos;
	}
	public void setUsuarisAmbPermisos(Integer usuarisAmbPermisos) {
		this.usuarisAmbPermisos = usuarisAmbPermisos;
	}
	public Integer getConsultesOk() {
		return consultesOk;
	}
	public void setConsultesOk(Integer consultesOk) {
		this.consultesOk = consultesOk;
	}
	public Integer getConsultesError() {
		return consultesError;
	}
	public void setConsultesError(Integer consultesError) {
		this.consultesError = consultesError;
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

	public static class ConsultesOkError {
		private Long ok;
		private Long error;
		public ConsultesOkError(Long ok, Long error) {
			super();
			this.ok = ok;
			this.error = error;
		}
		public Long getOk() {
			return ok;
		}
		public void setOk(Long ok) {
			this.ok = ok;
		}
		public Long getError() {
			return error;
		}
		public void setError(Long error) {
			this.error = error;
		}
	}

}
