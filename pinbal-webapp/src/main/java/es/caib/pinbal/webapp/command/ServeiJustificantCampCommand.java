/**
 * 
 */
package es.caib.pinbal.webapp.command;

import es.caib.pinbal.core.dto.ServeiJustificantCampDto;
import es.caib.pinbal.webapp.helper.CommandMappingHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Command per a les traduccions dels camps de dades específiques
 * dels serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ServeiJustificantCampCommand {

	private Long id;

	@NotEmpty @Size(max=64)
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
