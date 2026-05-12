/**
 * 
 */
package es.caib.pinbal.webapp.command;

import es.caib.pinbal.core.dto.ServeiCampGrupDto;
import es.caib.pinbal.webapp.helper.CommandMappingHelper;
import es.caib.pinbal.webapp.validation.ServeiGrup;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Command per als camps dels serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@ServeiGrup
public class ServeiCampGrupCommand {

	private Long id;

	@NotEmpty @Size(max=64)
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
