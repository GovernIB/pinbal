/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.ServeiCampDto;
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
 * Command per als camps dels serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServeiCampCommand {

	private Long id;

	@NotEmpty
    @Size(max=64)
	private String servei;
	@NotEmpty @Size(max=255)
	private String path;
	private String tipus;
	@Size(max=64)
	private String etiqueta;
	@Size(max=64)
	private String valorPerDefecte;
	@Size(max=255)
	private String comentari;
	private String[] enumDescripcions;
	@Size(max=32)
	private String dataFormat;
	private Long campPareId;
	@Size(max=64)
	private String valorPare;
	private boolean inicialitzar;
	private boolean obligatori;
	private boolean modificable;
	private boolean visible;
	private int ordre;
	private int mida;
	private String validacioRegexp;
	private Integer validacioMin;
	private Integer validacioMax;
	private ServeiCampDto.ServeiCampDtoValidacioOperacio validacioDataCmpOperacio;
	private Long validacioDataCmpCamp2Id;
	private Integer validacioDataCmpNombre;
	private ServeiCampDto.ServeiCampDtoValidacioDataTipus validacioDataCmpTipus;


	public static List<ServeiCampCommand> toEntitatCommands(List<ServeiCampDto> dtos) {
		List<ServeiCampCommand> commands = new ArrayList<ServeiCampCommand>();
		for (ServeiCampDto dto: dtos)
			commands.add(asCommand(dto));
		return commands;
	}

	public static ServeiCampCommand asCommand(ServeiCampDto dto) {
		ServeiCampCommand command = CommandMappingHelper.getMapperFacade().map(
				dto,
				ServeiCampCommand.class);
		if (dto.getCampPare() != null)
			command.setCampPareId(dto.getCampPare().getId());
		return command;
	}
	public static ServeiCampDto asDto(ServeiCampCommand command) {
		ServeiCampDto dto = CommandMappingHelper.getMapperFacade().map(
				command,
				ServeiCampDto.class);
		if (command.getCampPareId() != null) {
			ServeiCampDto sc = new ServeiCampDto();
			sc.setId(command.getCampPareId());
			dto.setCampPare(sc);
		}
		if (command.getValidacioDataCmpCamp2Id() != null) {
			ServeiCampDto sc = new ServeiCampDto();
			sc.setId(command.getValidacioDataCmpCamp2Id());
			dto.setValidacioDataCmpCamp2(sc);
		}
		return dto;
	}

}
