/**
 * 
 */
package es.caib.pinbal.scsp;

import java.util.Date;



/**
 * Informació sobre una resposta SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Resposta {

	private String funcionariNom;
	private String funcionariNif;
	private Consentiment consentiment;
	private String expedientId;
	private String finalitat;
	private String unitatTramitadora;
	private String unitatTramitadoraCodi;

	private Date respostaData;
	private String peticioXml;
	private String respostaXml;

	private ResultatEnviamentPeticio resultatEnviament;



	public String getFuncionariNom() {
		return funcionariNom;
	}
	public void setFuncionariNom(String funcionariNom) {
		this.funcionariNom = funcionariNom;
	}
	public String getFuncionariNif() {
		return funcionariNif;
	}
	public void setFuncionariNif(String funcionariNif) {
		this.funcionariNif = funcionariNif;
	}
	public Consentiment getConsentiment() {
		return consentiment;
	}
	public void setConsentiment(Consentiment consentiment) {
		this.consentiment = consentiment;
	}
	public String getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(String expedientId) {
		this.expedientId = expedientId;
	}
	public String getFinalitat() {
		return finalitat;
	}
	public void setFinalitat(String finalitat) {
		this.finalitat = finalitat;
	}
	public String getUnitatTramitadora() {
		return unitatTramitadora;
	}
	public void setUnitatTramitadora(String unitatTramitadora) {
		this.unitatTramitadora = unitatTramitadora;
	}
	public String getUnitatTramitadoraCodi() {
		return unitatTramitadoraCodi;
	}
	public void setUnitatTramitadoraCodi(String unitatTramitadoraCodi) {
		this.unitatTramitadoraCodi = unitatTramitadoraCodi;
	}
	public Date getRespostaData() {
		return respostaData;
	}
	public void setRespostaData(Date respostaData) {
		this.respostaData = respostaData;
	}
	public String getPeticioXml() {
		return peticioXml;
	}
	public void setPeticioXml(String peticioXml) {
		this.peticioXml = peticioXml;
	}
	public String getRespostaXml() {
		return respostaXml;
	}
	public void setRespostaXml(String respostaXml) {
		this.respostaXml = respostaXml;
	}
	public ResultatEnviamentPeticio getResultatEnviament() {
		return resultatEnviament;
	}
	public void setResultatEnviament(ResultatEnviamentPeticio resultatEnviament) {
		this.resultatEnviament = resultatEnviament;
	}

}
