/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.Date;

import javax.validation.constraints.Size;

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
	private String funcionariNom;
	private String funcionariDocument;
	private Long entitatId;


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

}
