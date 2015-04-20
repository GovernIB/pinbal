/**
 * 
 */
package es.caib.pinbal.webapp.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.pinbal.core.dto.ServeiCampGrupDto;

/**
 * Command per als camps dels serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiCampGrupCommand {

	private Long id;

	@NotEmpty @Size(max=64)
	private String servei;
	@NotEmpty @Size(max=255)
	private String nom;
	private int ordre;

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
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

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
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
