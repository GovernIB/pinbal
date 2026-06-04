/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.ServeiCampGrupDto;
import es.caib.pinbal.back.helper.CommandMappingHelper;
import es.caib.pinbal.back.validation.ServeiGrup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Command per als camps dels serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ServeiGrup
public class ServeiCampGrupCommand {

	private Long id;

	@NotEmpty
    @Size(max=64)
	private String servei;
	@NotEmpty @Size(max=255)
	private String nom;
	private Long pareId;
	private String ajuda;
	private int ordre;


	public static ServeiCampGrupCommand asCommand(ServeiCampGrupDto dto) {
		ServeiCampGrupCommand command = CommandMappingHelper.getMapperFacade().map(
				dto,
				ServeiCampGrupCommand.class);
		return command;
	}
	public static ServeiCampGrupDto asDto(ServeiCampGrupCommand command) {
		ServeiCampGrupDto dto = CommandMappingHelper.getMapperFacade().map(
				command,
				ServeiCampGrupDto.class);
		if ("<br>".equals(command.getAjuda())) {
			dto.setAjuda(null);
		}
		return dto;
	}

}
