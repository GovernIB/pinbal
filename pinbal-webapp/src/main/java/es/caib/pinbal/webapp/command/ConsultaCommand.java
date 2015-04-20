/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.webapp.validation.ArxiuNoBuit;
import es.caib.pinbal.webapp.validation.DocumentIdentitatCif;
import es.caib.pinbal.webapp.validation.DocumentIdentitatDni;
import es.caib.pinbal.webapp.validation.DocumentIdentitatNie;
import es.caib.pinbal.webapp.validation.DocumentIdentitatNif;
import es.caib.pinbal.webapp.validation.DocumentIdentitatPass;

/**
 * Command per a realitzar consultes SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConsultaCommand {

	@NotNull
	private Long procedimentId;
	@NotEmpty @Size(max=64)
	private String serveiCodi;

	@Size(max=122)
	private String funcionariNom;
	@Size(max=10)
	private String funcionariNif;
	@Size(max=255)
	private String entitatNom;
	@Size(max=10)
	private String entitatCif;
	@NotEmpty @Size(max=64)
	private String departamentNom;
	@NotNull
	private Consentiment consentiment;
	@NotEmpty @Size(max=250)
	private String finalitat;

	@NotNull(groups=ConsultaCommandAmbDocumentObligatori.class)
	private DocumentTipus titularDocumentTipus;
	@NotEmpty(groups=ConsultaCommandAmbDocumentObligatori.class)
	@Size(max=14)
	@DocumentIdentitatNif(groups=ConsultaCommandAmbDocumentTipusNif.class)
	@DocumentIdentitatDni(groups=ConsultaCommandAmbDocumentTipusDni.class)
	@DocumentIdentitatCif(groups=ConsultaCommandAmbDocumentTipusCif.class)
	@DocumentIdentitatNie(groups=ConsultaCommandAmbDocumentTipusNie.class)
	@DocumentIdentitatPass(groups=ConsultaCommandAmbDocumentTipusPass.class)
	private String titularDocumentNum;
	@Size(max=40)
	private String titularNom;
	@Size(max=40)
	private String titularLlinatge1;
	@Size(max=40)
	private String titularLlinatge2;
	@Size(max=122)
	private String titularNomComplet;

	@Size(max=25)
	private String expedientId;

	private Map<String, String> dadesEspecifiques;

	private boolean multiple = false;
	@ArxiuNoBuit(groups=ConsultaCommandMultiple.class)
	private MultipartFile multipleFitxer;
	private List<String> multipleErrorsValidacio;



	public ConsultaCommand() {
	}
	public ConsultaCommand(String serveiCodi) {
		this.serveiCodi = serveiCodi;
	}

	public Long getProcedimentId() {
		return procedimentId;
	}
	public void setProcedimentId(Long procedimentId) {
		this.procedimentId = procedimentId;
	}
	public String getServeiCodi() {
		return serveiCodi;
	}
	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
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
	public String getEntitatNom() {
		return entitatNom;
	}
	public void setEntitatNom(String entitatNom) {
		this.entitatNom = entitatNom;
	}
	public String getEntitatCif() {
		return entitatCif;
	}
	public void setEntitatCif(String entitatCif) {
		this.entitatCif = entitatCif;
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
	public String getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(String expedientId) {
		this.expedientId = expedientId;
	}
	public Map<String, String> getDadesEspecifiques() {
		return dadesEspecifiques;
	}
	public void setDadesEspecifiques(Map<String, String> dadesEspecifiques) {
		this.dadesEspecifiques = dadesEspecifiques;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
	public MultipartFile getMultipleFitxer() {
		return multipleFitxer;
	}
	public void setMultipleFitxer(MultipartFile multipleFitxer) {
		this.multipleFitxer = multipleFitxer;
	}
	public List<String> getMultipleErrorsValidacio() {
		return multipleErrorsValidacio;
	}
	public void setMultipleErrorsValidacio(List<String> multipleErrorsValidacio) {
		this.multipleErrorsValidacio = multipleErrorsValidacio;
	}

	public static ConsultaDto asDto(ConsultaCommand command) {
		return CommandMappingHelper.getMapperFacade().map(
				command,
				ConsultaDto.class);
	}

	public interface ConsultaCommandAmbDocumentObligatori {}
	public interface ConsultaCommandAmbDocumentTipusNif {}
	public interface ConsultaCommandAmbDocumentTipusDni {}
	public interface ConsultaCommandAmbDocumentTipusCif {}
	public interface ConsultaCommandAmbDocumentTipusNie {}
	public interface ConsultaCommandAmbDocumentTipusPass {}
	public interface ConsultaCommandMultiple {}

}
