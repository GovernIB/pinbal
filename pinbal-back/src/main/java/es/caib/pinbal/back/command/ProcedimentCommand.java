/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.OrganGestorDto;
import es.caib.pinbal.logic.intf.dto.ProcedimentClaseTramiteEnumDto;
import es.caib.pinbal.logic.intf.dto.ProcedimentDto;
import es.caib.pinbal.back.helper.CommandMappingHelper;
import es.caib.pinbal.back.validation.CodiProcedimentNoRepetit;
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
 * Command per al manteniment de procediments
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
	private Boolean valorCampAutomatizado;
	private ProcedimentClaseTramiteEnumDto valorCampClaseTramite;

	private String codiSiaOrigen;
	private List<String> codiSiaFills;
	private boolean clonarPermisosOrigen;

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
