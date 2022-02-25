/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.Date;

import javax.validation.constraints.Size;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import es.caib.pinbal.core.dto.ConsultaDto.EstatTipus;
import es.caib.pinbal.core.dto.ConsultaFiltreDto;
import lombok.Data;

/**
 * Command per a filtrar les consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ConsultaFiltreCommand {

	private String scspPeticionId;
	private Long procediment;
	@Size(max=64)
	private String servei;
	private EstatTipus estat;
	private Date dataInici;
	private Date dataFi;
	private String titularNom;
	private String titularDocument;
	private String funcionari;
	private String usuari;
	private Long entitatId;

	public ConsultaFiltreCommand() {
		super();
	}

	// Filtra per defecte els darrers 3 mesos i l'entitat govern (per a consultes de l'administrador)
	public ConsultaFiltreCommand(Long entitatId) {
		DateTime dateStartToday = (new LocalDate()).toDateTimeAtStartOfDay();
		this.dataInici = dateStartToday.minusMonths(3).toDate();
		this.entitatId = entitatId;
	}

	// Filtra per defecte els darrers 3 mesos (per a consultes simples i multiples del delegat)
	public void filtrarDarrers3MesosPerDefecte() {
		DateTime dateStartToday = (new LocalDate()).toDateTimeAtStartOfDay();
		this.dataInici = dateStartToday.minusMonths(3).toDate();
	}

	// Elimina els espais en els camps de cerca
	public void eliminarEspaisCampsCerca() {
		this.scspPeticionId = eliminarEspais(this.scspPeticionId);
		this.titularNom = eliminarEspais(this.titularNom);
		this.titularDocument = eliminarEspais(this.titularDocument);
		this.funcionari = eliminarEspais(this.funcionari);
		this.usuari = eliminarEspais(this.usuari);
	}

	public static ConsultaFiltreDto asDto(ConsultaFiltreCommand command) {
		if (command == null)
			return null;
		ConsultaFiltreDto dto = CommandMappingHelper.getMapperFacade().map(
				command,
				ConsultaFiltreDto.class);
		dto.setProcedimentId(command.getProcediment());
		dto.setServeiCodi(command.getServei());
		return dto;
	}

	private String eliminarEspais(String str) {
		return (str != null) ? str.trim() : null;
	}

}
