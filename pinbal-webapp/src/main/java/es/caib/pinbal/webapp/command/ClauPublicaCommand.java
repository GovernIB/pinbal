/**
 * 
 */
package es.caib.pinbal.webapp.command;

import es.caib.pinbal.core.dto.ClauPublicaDto;
import es.caib.pinbal.webapp.helper.CommandMappingHelper;
import es.caib.pinbal.webapp.validation.ClauPublicaNoRepetida;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Command per al manteniment de claus públiques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ClauPublicaNoRepetida
public class ClauPublicaCommand {

	private Long id;
	
	@NotEmpty @Size(max=256)
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
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
