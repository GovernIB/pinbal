/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.ClauPublicaDto;
import es.caib.pinbal.back.helper.CommandMappingHelper;
import es.caib.pinbal.back.validation.ClauPublicaNoRepetida;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Command per al manteniment de claus públiques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ClauPublicaNoRepetida
public class ClauPublicaCommand {

	private Long id;
	
	@NotEmpty
    @Size(max=256)
	private String nom;
	@NotEmpty @Size(max=256)
	private String alies;
	@NotEmpty @Size(max=256)
	private String numSerie;

	@NotNull
	private Date dataAlta;
	private Date dataBaixa;
	
	public static ClauPublicaCommand asCommand(ClauPublicaDto dto) {
		return CommandMappingHelper.getMapperFacade().map(
				dto,
				ClauPublicaCommand.class);
	}
	
	public static ClauPublicaDto asDto(ClauPublicaCommand command) {
		return CommandMappingHelper.getMapperFacade().map(
				command,
				ClauPublicaDto.class);
	}
	
}
