/**
 * 
 */
package es.caib.pinbal.webapp.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.ServeiBusDto;

/**
 * Command per a configurar les redireccions de peticions al bus
 * de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiBusCommand {

	private Long id;

	@NotEmpty @Size(max=64)
	private String servei;
	@NotEmpty
	private String urlDesti;
	@NotNull
	private Long entitatId;

	public ServeiBusCommand() {
	}
	public ServeiBusCommand(String servei) {
		this.servei = servei;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getServei() {
		return servei;
	}
	public void setServei(String servei) {
		this.servei = servei;
	}
	public String getUrlDesti() {
		return urlDesti;
	}
	public void setUrlDesti(String urlDesti) {
		this.urlDesti = urlDesti;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}

	public static ServeiBusCommand asCommand(ServeiBusDto dto) {
		ServeiBusCommand command = CommandMappingHelper.getMapperFacade().map(
				dto,
				ServeiBusCommand.class);
		if (dto.getEntitat() != null)
			command.setEntitatId(dto.getEntitat().getId());
		return command;
	}
	public static ServeiBusDto asDto(ServeiBusCommand command) {
		ServeiBusDto dto = CommandMappingHelper.getMapperFacade().map(
				command,
				ServeiBusDto.class);
		if (command.getEntitatId() != null) {
			EntitatDto entitat = new EntitatDto();
			entitat.setId(command.getEntitatId());
			dto.setEntitat(entitat);
		}
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
