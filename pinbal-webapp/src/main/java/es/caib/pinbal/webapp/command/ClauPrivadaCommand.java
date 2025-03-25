/**
 * 
 */
package es.caib.pinbal.webapp.command;

import es.caib.pinbal.core.dto.ClauPrivadaDto;
import es.caib.pinbal.webapp.helper.CommandMappingHelper;
import es.caib.pinbal.webapp.validation.ClauPrivadaNoRepetida;
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
 * Command per al manteniment d'entitats
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
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
	@NotEmpty @Size(max=256)
	private String numSerie;
	
	private Date dataBaixa;
	@NotNull
	private Date dataAlta;
	
	private boolean interoperabilitat;
	@NotNull
	private Long organisme;
	
	
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
