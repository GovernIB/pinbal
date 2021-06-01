/**
 * 
 */
package es.caib.pinbal.client.dadesobertes;

import java.util.Date;

/**
 * Estructura d'un element de la resposta per a les dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesObertesRespostaConsulta {

	private String entitatCodi;
	private String entitatNom;
	private String entitatNif;
	private String entitatTipus;
	private String departamentCodi;
	private String departamentNom;
	private String procedimentCodi;
	private String procedimentNom;
	private String serveiCodi;
	private String serveiNom;
	private String emissor;
	private String emissorNif;
	private String consentiment;
	private String finalitat;
	private String titularTipusDoc;
	private String solicitudId;
	private Date data;
	private DadesObertesConsultaTipus tipus;
	private DadesObertesConsultaResultat resultat;

	public DadesObertesRespostaConsulta(
			String entitatCodi,
			String entitatNom,
			String entitatNif,
			String entitatTipus,
			String departamentCodi,
			String departamentNom,
			String procedimentCodi,
			String procedimentNom,
			String serveiCodi,
			String serveiNom,
			String emissor,
			String emissorNif,
			String consentiment,
			String finalitat,
			String titularTipusDoc,
			String solicitudId,
			Date data,
			boolean recobriment,
			String estat) {
		super();
		this.entitatCodi = entitatCodi;
		this.entitatNom = entitatNom;
		this.entitatNif = entitatNif;
		this.entitatTipus = entitatTipus;
		this.departamentCodi = departamentCodi;
		this.departamentNom = departamentNom;
		this.procedimentCodi = procedimentCodi;
		this.procedimentNom = procedimentNom;
		this.serveiCodi = serveiCodi;
		this.serveiNom = serveiNom;
		this.emissor = emissor;
		this.emissorNif = emissorNif;
		this.consentiment = consentiment;
		if (finalitat != null) {
			int index = finalitat.lastIndexOf("#");
			if (index != -1) {
				this.finalitat = finalitat.substring(index + 1);
			} else {
				this.finalitat = finalitat;
			}
		}
		this.titularTipusDoc = titularTipusDoc;
		this.solicitudId = solicitudId;
		this.data = data;
		this.tipus = recobriment ? DadesObertesConsultaTipus.RECOBRIMENT : DadesObertesConsultaTipus.WEB;
		if ("0".equals(estat)) {
			this.resultat = DadesObertesConsultaResultat.PROCES;
		} else if ("1".equals(estat)) {
			this.resultat = DadesObertesConsultaResultat.PROCES;
		} else if ("2".equals(estat)) {
			this.resultat = DadesObertesConsultaResultat.OK;
		} else if ("3".equals(estat)) {
			this.resultat = DadesObertesConsultaResultat.ERROR;
		}
	}

	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getEntitatNom() {
		return entitatNom;
	}
	public void setEntitatNom(String entitatNom) {
		this.entitatNom = entitatNom;
	}
	public String getEntitatNif() {
		return entitatNif;
	}
	public void setEntitatNif(String entitatNif) {
		this.entitatNif = entitatNif;
	}
	public String getEntitatTipus() {
		return entitatTipus;
	}
	public void setEntitatTipus(String entitatTipus) {
		this.entitatTipus = entitatTipus;
	}
	public String getDepartamentCodi() {
		return departamentCodi;
	}
	public void setDepartamentCodi(String departamentCodi) {
		this.departamentCodi = departamentCodi;
	}
	public String getDepartamentNom() {
		return departamentNom;
	}
	public void setDepartamentNom(String departamentNom) {
		this.departamentNom = departamentNom;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public String getProcedimentNom() {
		return procedimentNom;
	}
	public void setProcedimentNom(String procedimentNom) {
		this.procedimentNom = procedimentNom;
	}
	public String getServeiCodi() {
		return serveiCodi;
	}
	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
	}
	public String getServeiNom() {
		return serveiNom;
	}
	public void setServeiNom(String serveiNom) {
		this.serveiNom = serveiNom;
	}
	public String getEmissor() {
		return emissor;
	}
	public void setEmissor(String emissor) {
		this.emissor = emissor;
	}
	public String getEmissorNif() {
		return emissorNif;
	}
	public void setEmissorNif(String emissorNif) {
		this.emissorNif = emissorNif;
	}
	public String getConsentiment() {
		return consentiment;
	}
	public void setConsentiment(String consentiment) {
		this.consentiment = consentiment;
	}
	public String getFinalitat() {
		return finalitat;
	}
	public void setFinalitat(String finalitat) {
		this.finalitat = finalitat;
	}
	public String getTitularTipusDoc() {
		return titularTipusDoc;
	}
	public void setTitularTipusDoc(String titularTipusDoc) {
		this.titularTipusDoc = titularTipusDoc;
	}
	public String getSolicitudId() {
		return solicitudId;
	}
	public void setSolicitudId(String solicitudId) {
		this.solicitudId = solicitudId;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public DadesObertesConsultaTipus getTipus() {
		return tipus;
	}
	public void setTipus(DadesObertesConsultaTipus tipus) {
		this.tipus = tipus;
	}
	public DadesObertesConsultaResultat getResultat() {
		return resultat;
	}
	public void setResultat(DadesObertesConsultaResultat resultat) {
		this.resultat = resultat;
	}

	public enum DadesObertesConsultaTipus {
		WEB,
		RECOBRIMENT
	}

	public enum DadesObertesConsultaResultat {
		OK,
		PROCES,
		ERROR
	}

}
