/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.pinbal.core.dto.ServeiJustificantCampDto;

/**
 * Command per a les traduccions dels camps de dades específiques
 * dels serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiJustificantCampCommand {

	private Long id;

	@NotEmpty @Size(max=64)
	private String servei;
	@NotEmpty @Size(max=255)
	private String xpath;
	@Size(max=255)
	private String traduccio;

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
	public String getXpath() {
		return xpath;
	}
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	public String getTraduccio() {
		return traduccio;
	}
	public void setTraduccio(String traduccio) {
		this.traduccio = traduccio;
	}

	public static List<ServeiJustificantCampCommand> toEntitatCommands(
			List<ServeiJustificantCampDto> dtos) {
		List<ServeiJustificantCampCommand> commands = new ArrayList<ServeiJustificantCampCommand>();
		for (ServeiJustificantCampDto dto: dtos)
			commands.add(asCommand(dto));
		return commands;
	}

	public static ServeiJustificantCampCommand asCommand(ServeiJustificantCampDto dto) {
		ServeiJustificantCampCommand command = CommandMappingHelper.getMapperFacade().map(
				dto,
				ServeiJustificantCampCommand.class);
		return command;
	}
	public static ServeiJustificantCampDto asDto(ServeiJustificantCampCommand command) {
		ServeiJustificantCampDto dto = CommandMappingHelper.getMapperFacade().map(
				command,
				ServeiJustificantCampDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
