/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.ConsultaDto.EstatTipus;
import es.caib.pinbal.logic.intf.dto.EstadistiquesFiltreDto;
import es.caib.pinbal.logic.intf.dto.EstadistiquesFiltreDto.EstadistiquesAgrupacioDto;
import es.caib.pinbal.back.helper.CommandMappingHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Command per a filtrar les estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadistiquesFiltreCommand {

	private Long entitatId;
	private Long procediment;
	@Size(max=64)
	private String servei;
	private EstatTipus estat;
	private Date dataInici;
	private Date dataFi;
	@Size(max=64)
	private String usuariCodi;
	private EstadistiquesAgrupacioDto agrupacio = EstadistiquesAgrupacioDto.PROCEDIMENT_SERVEI;

	public static EstadistiquesFiltreDto asDto(EstadistiquesFiltreCommand command) {
		if (command == null)
			return null;
		EstadistiquesFiltreDto dto = CommandMappingHelper.getMapperFacade().map(
				command,
				EstadistiquesFiltreDto.class);
		dto.setProcedimentId(command.getProcediment());
		if (command.getServei() != null && !command.getServei().isEmpty())
			dto.setServeiCodi(command.getServei());
		if (dto.getUsuariCodi() != null && dto.getUsuariCodi().isEmpty())
			dto.setUsuariCodi(null);
		return dto;
	}
}
