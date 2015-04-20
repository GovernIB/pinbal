/**
 * 
 */
package es.caib.pinbal.scsp;

import java.util.Map;

import org.w3c.dom.Element;


/**
 * Dades per a una sol·licitud cap a SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Solicitud {

	private String serveiCodi;
	private String procedimentCodi;
	private String procedimentNom;
	private String solicitantIdentificacio;
	private String solicitantNom;
	private String funcionariNom;
	private String funcionariNif;
	private DocumentTipus titularDocumentTipus;
	private String titularDocument;
	private String titularNom;
	private String titularLlinatge1;
	private String titularLlinatge2;
	private String titularNomComplet;
	private String finalitat;
	private Consentiment consentiment;
	private String unitatTramitadora;
	private String expedientId;

	private Element dadesEspecifiquesElement;
	private Map<String, String> dadesEspecifiquesMap;

	public String getServeiCodi() {
		return serveiCodi;
	}
	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
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
	public String getSolicitantIdentificacio() {
		return solicitantIdentificacio;
	}
	public void setSolicitantIdentificacio(String solicitantIdentificacio) {
		this.solicitantIdentificacio = solicitantIdentificacio;
	}
	public String getSolicitantNom() {
		return solicitantNom;
	}
	public void setSolicitantNom(String solicitantNom) {
		this.solicitantNom = solicitantNom;
	}
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
	public DocumentTipus getTitularDocumentTipus() {
		return titularDocumentTipus;
	}
	public void setTitularDocumentTipus(DocumentTipus titularDocumentTipus) {
		this.titularDocumentTipus = titularDocumentTipus;
	}
	public String getTitularDocument() {
		return titularDocument;
	}
	public void setTitularDocument(String titularDocument) {
		this.titularDocument = titularDocument;
	}
	public String getTitularNom() {
		return titularNom;
	}
	public void setTitularNom(String titularNom) {
		this.titularNom = titularNom;
	}
	public String getTitularLlinatge1() {
		return titularLlinatge1;
	}
	public void setTitularLlinatge1(String titularLlinatge1) {
		this.titularLlinatge1 = titularLlinatge1;
	}
	public String getTitularLlinatge2() {
		return titularLlinatge2;
	}
	public void setTitularLlinatge2(String titularLlinatge2) {
		this.titularLlinatge2 = titularLlinatge2;
	}
	public String getTitularNomComplet() {
		return titularNomComplet;
	}
	public void setTitularNomComplet(String titularNomComplet) {
		this.titularNomComplet = titularNomComplet;
	}
	public String getFinalitat() {
		return finalitat;
	}
	public void setFinalitat(String finalitat) {
		this.finalitat = finalitat;
	}
	public Consentiment getConsentiment() {
		return consentiment;
	}
	public void setConsentiment(Consentiment consentiment) {
		this.consentiment = consentiment;
	}
	public String getUnitatTramitadora() {
		return unitatTramitadora;
	}
	public void setUnitatTramitadora(String unitatTramitadora) {
		this.unitatTramitadora = unitatTramitadora;
	}
	public String getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(String expedientId) {
		this.expedientId = expedientId;
	}

	public Element getDadesEspecifiquesElement() {
		return dadesEspecifiquesElement;
	}
	public void setDadesEspecifiquesElement(Element dadesEspecifiquesElement) {
		this.dadesEspecifiquesElement = dadesEspecifiquesElement;
	}
	public Map<String, String> getDadesEspecifiquesMap() {
		return dadesEspecifiquesMap;
	}
	public void setDadesEspecifiquesMap(Map<String, String> dadesEspecifiquesMap) {
		this.dadesEspecifiquesMap = dadesEspecifiquesMap;
	}

}
