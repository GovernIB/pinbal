/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.back.helper.CommandMappingHelper;
import es.caib.pinbal.back.validation.CifEntitatNoRepetit;
import es.caib.pinbal.back.validation.CodiEntitatNoRepetit;
import es.caib.pinbal.back.validation.DocumentIdentitat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Command per al manteniment d'entitats
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CodiEntitatNoRepetit(campId = "id", campCodi = "codi")
@CifEntitatNoRepetit(campId = "id", campCif = "cif")
public class EntitatCommand {

	private Long id;

	@NotEmpty
	@Size(max = 64)
	private String codi;
	@NotEmpty
	@Size(max = 255)
	private String nom;
	@NotEmpty
	@Size(max = 16)
	@DocumentIdentitat(documentTipus = DocumentTipus.CIF)
	private String cif;
	@NotEmpty
	@Size(max = 9)
	private String unitatArrel;
	@NotNull
	private String tipus;

	public static List<EntitatCommand> toEntitatCommands(List<EntitatDto> dtos) {
		List<EntitatCommand> commands = new ArrayList<EntitatCommand>();
		for (EntitatDto dto : dtos) {
			commands.add(CommandMappingHelper.getMapperFacade().map(dto, EntitatCommand.class));
		}
		return commands;
	}

	public static EntitatCommand asCommand(EntitatDto dto) {
		return CommandMappingHelper.getMapperFacade().map(dto, EntitatCommand.class);
	}

	public static EntitatDto asDto(EntitatCommand command) {
		return CommandMappingHelper.getMapperFacade().map(command, EntitatDto.class);
	}

}
