package es.caib.pinbal.webapp.command;

import es.caib.pinbal.core.dto.ClauPrivadaDto;
import es.caib.pinbal.core.dto.ClauPublicaDto;
import es.caib.pinbal.core.dto.EmisorDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.dto.ServeiDto.JustificantTipusDto;
import es.caib.pinbal.core.dto.ServeiXsdDto;
import es.caib.pinbal.webapp.helper.CommandMappingHelper;
import es.caib.pinbal.webapp.helper.ConversioTipusHelper;
import es.caib.pinbal.webapp.validation.CodiServeiNoRepetit;
import es.caib.pinbal.webapp.validation.ServeiUrl;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Command que representa la informació d'un servei SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@CodiServeiNoRepetit(campCodi = "codi", campCreacio="creacio")
@ServeiUrl
public class ServeiCommand {

	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=512)
	private String descripcio;
	@NotEmpty @Size(max=16)
	private String scspEmisor;
	@NotNull
	private Date scspFechaAlta;
	private Date scspFechaBaja;
	private int scspCaducidad = 0;
	@Size(max=256)
	private String scspUrlSincrona;
	@Size(max=256)
	private String scspUrlAsincrona;
	@Size(max=256)
	private String scspActionSincrona;
	@Size(max=256)
	private String scspActionAsincrona;
	@Size(max=256)
	private String scspActionSolicitud;
	@NotEmpty @Size(max=32)
	private String scspVersionEsquema;
	@NotEmpty @Size(max=16)
	private String scspTipoSeguridad;
	@NotEmpty @Size(max=256)
	private String scspClaveFirma;
	@Size(max=256)
	private String scspClaveCifrado;
	@Size(max=256)
	private String scspXpathCifradoSincrono;
	@Size(max=256)
	private String scspXpathCifradoAsincrono;
	@Size(max=32)
	private String scspAlgoritmoCifrado;
	@Size(max=32)
	private String scspValidacionFirma;
	@Size(max=8)
	private String scspPrefijoPeticion;
	@Size(max=256)
	private String scspEsquemas;
	@NotNull
	private int scspNumeroMaximoReenvios = 0;
	@NotNull
	private int scspMaxSolicitudesPeticion = 0;
	@Size(max=8)
	private String scspPrefijoIdTransmision;
	@Size(max=256)
	private String scspXpathCodigoError;
	@Size(max=256)
	private String scspXpathLiteralError;
	@NotNull
	private int scspTimeout;

	@Size(max=64)
	private String pinbalCustodiaCodi;
	@Size(max=64)
	private String pinbalRoleName;
	@Size(max=255)
	private String pinbalCondicioBusClass;
	private String pinbalEntitatTipus;
	private JustificantTipusDto pinbalJustificantTipus;
	private String pinbalJustificantXpath;
	private boolean pinbalPermesDocumentTipusDni = true;
	private boolean pinbalPermesDocumentTipusNif = true;
	private boolean pinbalPermesDocumentTipusCif = true;
	private boolean pinbalPermesDocumentTipusNie = true;
	private boolean pinbalPermesDocumentTipusPas = true;
	private boolean pinbalActiuCampNom = true;
	private boolean pinbalActiuCampLlinatge1 = true;
	private boolean pinbalActiuCampLlinatge2 = true;
	private boolean pinbalActiuCampNomComplet = true;
	private boolean pinbalActiuCampDocument = true;
	private boolean pinbalUnitatDir3FromEntitat = false;
	@Size(max=10)
	private String pinbalUnitatDir3;
	private boolean pinbalDocumentObligatori = true;
	private boolean pinbalComprovarDocument = true;
	private boolean activaGestioXsd = false;
	private Integer maxPeticionsMinut;

	private boolean creacio;
	
	private String ajuda;
	private String fitxerAjudaNom;
	private MultipartFile fitxerAjuda;
	
	private List<ServeiXsdDto>  fitxersXsd;
	
	private boolean pinbalIniDadesExpecifiques = false;
	private boolean pinbalAddDadesEspecifiques = true;
	private boolean useAutoClasse = true;
	private boolean enviarSolicitant = false;

	private Date dataDarreraActualitzacio;
	
	public ServeiCommand() {
		scspFechaAlta = new Date();
		creacio = false;
	}
	public ServeiCommand(boolean creacio) {
		scspFechaAlta = new Date();
		this.creacio = creacio;
	}

	public String getDataDarreraActualitzacioString() {
		if (dataDarreraActualitzacio == null) return null;

		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return sdf.format(dataDarreraActualitzacio);
	}
	
	
	public static ServeiCommand asCommand(ServeiDto dto) {
		ServeiCommand command = CommandMappingHelper.getMapperFacade().map(dto, ServeiCommand.class);
		if (dto.getScspEmisor() != null)
			command.setScspEmisor(dto.getScspEmisor().getCif());
		if (dto.getScspClaveFirma() != null)
			command.setScspClaveFirma(dto.getScspClaveFirma().getAlies());
		if (dto.getScspClaveCifrado() != null)
			command.setScspClaveCifrado(dto.getScspClaveCifrado().getAlies());
		if (dto.getFitxersXsd() != null && dto.getFitxersXsd().size() > 0)
			command.setFitxersXsd(dto.getFitxersXsd());
		return command;
	}
	public static ServeiDto asDto(ServeiCommand command) {
		if (command.getPinbalEntitatTipus() != null && command.getPinbalEntitatTipus().isEmpty())
			command.setPinbalEntitatTipus(null);
		ServeiDto dto = CommandMappingHelper.getMapperFacade().map(
				command,
				ServeiDto.class);
		if (command.getScspEmisor() != null && !command.getScspEmisor().isEmpty()) {
			EmisorDto emisor = new EmisorDto();
			emisor.setCif(command.getScspEmisor());
			dto.setScspEmisor(emisor);
		} else {
			dto.setScspEmisor(null);
		}
		if (command.getScspClaveFirma() != null && !command.getScspClaveFirma().isEmpty()) {
			ClauPrivadaDto clauPrivada = new ClauPrivadaDto();
			clauPrivada.setAlies(command.getScspClaveFirma());
			dto.setScspClaveFirma(clauPrivada);
		} else {
			dto.setScspClaveFirma(null);
		}
		if (command.getScspClaveCifrado() != null && !command.getScspClaveCifrado().isEmpty()) {
			ClauPublicaDto clauPublica = new ClauPublicaDto();
			clauPublica.setAlies(command.getScspClaveCifrado());
			dto.setScspClaveCifrado(clauPublica);
		} else {
			dto.setScspClaveCifrado(null);
		}
		if (command.getFitxerAjuda() != null) {
			dto.setFitxerAjudaNom(command.getFitxerAjuda().getOriginalFilename());
			dto.setFitxerAjudaMimeType(command.getFitxerAjuda().getContentType());
			try {
				dto.setFitxerAjudaContingut(command.getFitxerAjuda().getBytes());
			} catch (IOException e) {}
		}
		if (command.getFitxersXsd() != null && command.getFitxersXsd().size() > 0) {
			dto.setFitxersXsd(command.getFitxersXsd());
		}else {
			dto.setFitxersXsd(new ArrayList<ServeiXsdDto>());
		}
		return dto;
	}
	
	public static ServeiXsdDto asXsdDto(ServeiCommand command) throws IOException {
		return ConversioTipusHelper.convertir(
				command,
				ServeiXsdDto.class);
	}

}
