/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.pinbal.core.dto.OrganGestorDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.webapp.validation.CodiProcedimentNoRepetit;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Command per al manteniment de procediments
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString
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
	@NotNull
	private Long organGestorId;
	@Size(max = 64)
	private String codiSia;

	public static List<ProcedimentCommand> toProcedimentCommands(List<ProcedimentDto> dtos) {
		List<ProcedimentCommand> commands = new ArrayList<ProcedimentCommand>();
		for (ProcedimentDto dto : dtos) {
			commands.add(asCommand(dto));
		}
		return commands;
	}

	public static ProcedimentCommand asCommand(ProcedimentDto dto) {
		ProcedimentCommand command = CommandMappingHelper.getMapperFacade().map(dto, ProcedimentCommand.class);
		if (dto.getOrganGestor() != null) {
			command.setOrganGestorId(dto.getOrganGestor().getId());
		}
		return command;
	}

	public ProcedimentDto asDto() {
		ProcedimentDto dto = CommandMappingHelper.getMapperFacade().map(this, ProcedimentDto.class);
		if (this.getOrganGestorId() != null) {
			OrganGestorDto organGestor = new OrganGestorDto();
			organGestor.setId(this.getOrganGestorId());
			dto.setOrganGestor(organGestor);
		}
		return dto;
	}

}
