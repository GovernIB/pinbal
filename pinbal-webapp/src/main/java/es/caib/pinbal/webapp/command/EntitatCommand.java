/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.webapp.validation.CodiEntitatNoRepetit;
import es.caib.pinbal.webapp.validation.DocumentIdentitat;

/**
 * Command per al manteniment d'entitats
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@CodiEntitatNoRepetit(campId = "id", campCodi = "codi")
public class EntitatCommand {

	private Long id;

	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=255)
	private String nom;
	@NotEmpty @Size(max=16)
	@DocumentIdentitat(documentTipus=DocumentTipus.CIF)
	private String cif;
	@NotNull
	private String tipus;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCif() {
		return cif;
	}
	public void setCif(String cif) {
		this.cif = cif;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}

	public static List<EntitatCommand> toEntitatCommands(
			List<EntitatDto> dtos) {
		List<EntitatCommand> commands = new ArrayList<EntitatCommand>();
		for (EntitatDto dto: dtos) {
			commands.add(
					CommandMappingHelper.getMapperFacade().map(
							dto,
							EntitatCommand.class));
		}
		return commands;
	}

	public static EntitatCommand asCommand(EntitatDto dto) {
		return CommandMappingHelper.getMapperFacade().map(
				dto,
				EntitatCommand.class);
	}
	public static EntitatDto asDto(EntitatCommand command) {
		return CommandMappingHelper.getMapperFacade().map(
				command,
				EntitatDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
