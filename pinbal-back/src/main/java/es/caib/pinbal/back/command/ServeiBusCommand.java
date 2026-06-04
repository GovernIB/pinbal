/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.ServeiBusDto;
import es.caib.pinbal.back.helper.CommandMappingHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Command per a configurar les redireccions de peticions al bus
 * de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServeiBusCommand {

	private Long id;

	@NotEmpty @Size(max=64)
	private String servei;
	@NotEmpty
	private String urlDesti;
	@NotNull
	private Long entitatId;

	public ServeiBusCommand(String servei) {
		this.servei = servei;
	}


	public static ServeiBusCommand asCommand(ServeiBusDto dto) {
		ServeiBusCommand command = CommandMappingHelper.getMapperFacade().map(
				dto,
				ServeiBusCommand.class);
		if (dto.getEntitat() != null)
			command.setEntitatId(dto.getEntitat().getId());
		return command;
	}
	public static ServeiBusDto asDto(ServeiBusCommand command) {
		ServeiBusDto dto = CommandMappingHelper.getMapperFacade().map(
				command,
				ServeiBusDto.class);
		if (command.getEntitatId() != null) {
			EntitatDto entitat = new EntitatDto();
			entitat.setId(command.getEntitatId());
			dto.setEntitat(entitat);
		}
		return dto;
	}

}
