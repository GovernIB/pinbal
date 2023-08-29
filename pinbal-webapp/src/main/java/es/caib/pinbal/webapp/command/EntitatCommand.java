/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.pinbal.webapp.helper.CommandMappingHelper;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.webapp.validation.CifEntitatNoRepetit;
import es.caib.pinbal.webapp.validation.CodiEntitatNoRepetit;
import es.caib.pinbal.webapp.validation.DocumentIdentitat;
import lombok.Data;

/**
 * Command per al manteniment d'entitats
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
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
