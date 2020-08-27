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
import lombok.Getter;
import lombok.Setter;

/**
 * Command per a realitzar consultes SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ConsultaCommand {

	@NotNull
	private Long procedimentId;
	@NotEmpty @Size(max=64)
	private String serveiCodi;

	@NotEmpty @Size(max=122)
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
	private DocumentTipus titularDocumentTipus = DocumentTipus.NIF;
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

	private Map<String, Object> dadesEspecifiques;

	private boolean multiple = false;
	@ArxiuNoBuit(groups=ConsultaCommandMultiple.class)
	private MultipartFile multipleFitxer;
	private List<String> multipleErrorsValidacio;

	public ConsultaCommand() {
	}
	public ConsultaCommand(String serveiCodi) {
		this.serveiCodi = serveiCodi;
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
