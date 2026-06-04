/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.ParamConfDto;
import es.caib.pinbal.back.helper.CommandMappingHelper;
import es.caib.pinbal.back.validation.NomParamConfNoRepetit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Command per al manteniment de paràmetres de configuració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NomParamConfNoRepetit(campNom = "nom")
public class ParamConfCommand {

	@NotEmpty
	@Size(max=64)
	private String nom;
	@NotEmpty
	@Size(max=512)
	private String valor;
	@Size(max=512)
	private String descripcio;
	private boolean forcreate;
	

	public static ParamConfCommand commandForCreate() {
		
		ParamConfCommand command = new ParamConfCommand();
		command.setForcreate(true);
		
		return command;
	}
	
	public static ParamConfCommand asCommandForUpdate(ParamConfDto dto) {
		
		ParamConfCommand command = CommandMappingHelper.getMapperFacade().map(
				dto,
				ParamConfCommand.class);
		command.setForcreate(false);
		
		return command;
	}
	
	public static ParamConfCommand asCommand(ParamConfDto dto) {
		
		return CommandMappingHelper.getMapperFacade().map(
				dto,
				ParamConfCommand.class);
	}
	
	public static ParamConfDto asDto(ParamConfCommand command) {
		
		return CommandMappingHelper.getMapperFacade().map(
				command,
				ParamConfDto.class);
	}
	
}
