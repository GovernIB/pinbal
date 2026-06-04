/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.ClauPrivadaDto;
import es.caib.pinbal.back.helper.CommandMappingHelper;
import es.caib.pinbal.back.validation.ClauPrivadaNoRepetida;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Command per al manteniment d'entitats
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ClauPrivadaNoRepetida
public class ClauPrivadaCommand {
	
	private Long id;
	@NotEmpty @Size(max=256)
	private String alies;
	@NotEmpty @Size(max=256)
	private String nom;
	@NotEmpty @Size(max=256)
	private String password;
	@NotEmpty
    @Size(max=256)
	private String numSerie;
	
	private Date dataBaixa;
	@NotNull
	private Date dataAlta;
	
	private boolean interoperabilitat;
	@NotNull
	private Long organismeId;

    private boolean perEntitat;
	
	
	public static ClauPrivadaCommand asCommand(ClauPrivadaDto dto) {
		return CommandMappingHelper.getMapperFacade().map(
				dto,
				ClauPrivadaCommand.class);
	}

	public static ClauPrivadaDto asDto(ClauPrivadaCommand command) {
		return CommandMappingHelper.getMapperFacade().map(
				command,
				ClauPrivadaDto.class);
	}

}
