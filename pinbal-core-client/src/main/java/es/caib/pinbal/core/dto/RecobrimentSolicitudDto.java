/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.w3c.dom.Element;

import es.caib.pinbal.core.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;

/**
 * Dades d'una sol·licitud provinent del recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RecobrimentSolicitudDto implements Serializable {

	private String id;
	private String entitatCif;
	private String procedimentCodi;
	private String funcionariNif;
	private String funcionariNom;
	private String departamentNom;
	private Consentiment consentiment;
	private String finalitat;
	private String expedientId;
	private DocumentTipus titularDocumentTipus;
	private String titularDocumentNum;
	private String titularNom;
	private String titularLlinatge1;
	private String titularLlinatge2;
	private String titularNomComplet;
	private Element dadesEspecifiques;



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEntitatCif() {
		return entitatCif;
	}
	public void setEntitatCif(String entitatCif) {
		this.entitatCif = entitatCif;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public String getFuncionariNif() {
		return funcionariNif;
	}
	public void setFuncionariNif(String funcionariNif) {
		this.funcionariNif = funcionariNif;
	}
	public String getFuncionariNom() {
		return funcionariNom;
	}
	public void setFuncionariNom(String funcionariNom) {
		this.funcionariNom = funcionariNom;
	}
	public String getDepartamentNom() {
		return departamentNom;
	}
	public void setDepartamentNom(String departamentNom) {
		this.departamentNom = departamentNom;
	}
	public Consentiment getConsentiment() {
		return consentiment;
	}
	public void setConsentiment(Consentiment consentiment) {
		this.consentiment = consentiment;
	}
	public String getFinalitat() {
		return finalitat;
	}
	public void setFinalitat(String finalitat) {
		this.finalitat = finalitat;
	}
	public String getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(String expedientId) {
		this.expedientId = expedientId;
	}
	public DocumentTipus getTitularDocumentTipus() {
		return titularDocumentTipus;
	}
	public void setTitularDocumentTipus(DocumentTipus titularDocumentTipus) {
		this.titularDocumentTipus = titularDocumentTipus;
	}
	public String getTitularDocumentNum() {
		return titularDocumentNum;
	}
	public void setTitularDocumentNum(String titularDocumentNum) {
		this.titularDocumentNum = titularDocumentNum;
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
	public Element getDadesEspecifiques() {
		return dadesEspecifiques;
	}
	public void setDadesEspecifiques(Element dadesEspecifiques) {
		this.dadesEspecifiques = dadesEspecifiques;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
