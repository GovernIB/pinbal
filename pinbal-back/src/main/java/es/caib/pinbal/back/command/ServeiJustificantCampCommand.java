/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.ServeiJustificantCampDto;
import es.caib.pinbal.back.helper.CommandMappingHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Command per a les traduccions dels camps de dades específiques
 * dels serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServeiJustificantCampCommand {

	private Long id;

	@NotEmpty
    @Size(max=64)
	private String servei;
	@NotEmpty @Size(max=255)
	private String xpath;
	@Size(max=255)
	private String traduccio;
	private boolean document;


	public static List<ServeiJustificantCampCommand> toEntitatCommands(List<ServeiJustificantCampDto> dtos) {
		List<ServeiJustificantCampCommand> commands = new ArrayList<>();
		for (ServeiJustificantCampDto dto: dtos)
			commands.add(asCommand(dto));
		return commands;
	}

	public static ServeiJustificantCampCommand asCommand(ServeiJustificantCampDto dto) {
		ServeiJustificantCampCommand command = CommandMappingHelper.getMapperFacade().map(
				dto,
				ServeiJustificantCampCommand.class);
		return command;
	}
	public static ServeiJustificantCampDto asDto(ServeiJustificantCampCommand command) {
		ServeiJustificantCampDto dto = CommandMappingHelper.getMapperFacade().map(
				command,
				ServeiJustificantCampDto.class);
		return dto;
	}

}
