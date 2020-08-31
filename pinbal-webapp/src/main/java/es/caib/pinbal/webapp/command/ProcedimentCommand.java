/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.webapp.validation.CodiProcedimentNoRepetit;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment de procediments
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@CodiProcedimentNoRepetit(campId = "id", campEntitatId = "entitatId", campCodi = "codi")
public class ProcedimentCommand {

	private Long id;

	private Long entitatId;

	@NotEmpty
	@Size(max = 20)
	private String codi;
	@NotEmpty
	@Size(max = 100)
	private String nom;
	@Size(max = 64)
	private String departament;
	@NotEmpty
	@Size(max = 64)
	private String organGestor;
	@Size(max = 64)
	private String codiSia;

	public static List<ProcedimentCommand> toProcedimentCommands(List<ProcedimentDto> dtos) {
		List<ProcedimentCommand> commands = new ArrayList<ProcedimentCommand>();
		for (ProcedimentDto dto : dtos) {
			commands.add(CommandMappingHelper.getMapperFacade().map(dto, ProcedimentCommand.class));
		}
		return commands;
	}

	public static ProcedimentCommand asCommand(ProcedimentDto dto) {
		return CommandMappingHelper.getMapperFacade().map(dto, ProcedimentCommand.class);
	}

	public static ProcedimentDto asDto(ProcedimentCommand command) {
		return CommandMappingHelper.getMapperFacade().map(command, ProcedimentDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
