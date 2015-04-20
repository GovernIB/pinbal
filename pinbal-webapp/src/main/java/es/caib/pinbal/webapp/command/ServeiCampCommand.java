/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.pinbal.core.dto.ServeiCampDto;

/**
 * Command per als camps dels serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiCampCommand {

	private Long id;

	@NotEmpty @Size(max=64)
	private String servei;
	@NotEmpty @Size(max=255)
	private String path;
	private String tipus;
	@Size(max=64)
	private String etiqueta;
	@Size(max=64)
	private String valorPerDefecte;
	@Size(max=255)
	private String comentari;
	private String[] enumDescripcions;
	@Size(max=32)
	private String dataFormat;
	private Long campPareId;
	@Size(max=64)
	private String valorPare;
	private boolean obligatori;
	private boolean modificable;
	private boolean visible;
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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public String getValorPerDefecte() {
		return valorPerDefecte;
	}
	public void setValorPerDefecte(String valorPerDefecte) {
		this.valorPerDefecte = valorPerDefecte;
	}
	public String getComentari() {
		return comentari;
	}
	public void setComentari(String comentari) {
		this.comentari = comentari;
	}
	public String[] getEnumDescripcions() {
		return enumDescripcions;
	}
	public void setEnumDescripcions(String[] enumDescripcions) {
		this.enumDescripcions = enumDescripcions;
	}
	public String getDataFormat() {
		return dataFormat;
	}
	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}
	public Long getCampPareId() {
		return campPareId;
	}
	public void setCampPareId(Long campPareId) {
		this.campPareId = campPareId;
	}
	public String getValorPare() {
		return valorPare;
	}
	public void setValorPare(String valorPare) {
		this.valorPare = valorPare;
	}
	public boolean isObligatori() {
		return obligatori;
	}
	public void setObligatori(boolean obligatori) {
		this.obligatori = obligatori;
	}
	public boolean isModificable() {
		return modificable;
	}
	public void setModificable(boolean modificable) {
		this.modificable = modificable;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	public static List<ServeiCampCommand> toEntitatCommands(
			List<ServeiCampDto> dtos) {
		List<ServeiCampCommand> commands = new ArrayList<ServeiCampCommand>();
		for (ServeiCampDto dto: dtos)
			commands.add(asCommand(dto));
		return commands;
	}

	public static ServeiCampCommand asCommand(ServeiCampDto dto) {
		ServeiCampCommand command = CommandMappingHelper.getMapperFacade().map(
				dto,
				ServeiCampCommand.class);
		if (dto.getCampPare() != null)
			command.setCampPareId(dto.getCampPare().getId());
		return command;
	}
	public static ServeiCampDto asDto(ServeiCampCommand command) {
		ServeiCampDto dto = CommandMappingHelper.getMapperFacade().map(
				command,
				ServeiCampDto.class);
		if (command.getCampPareId() != null) {
			ServeiCampDto sc = new ServeiCampDto();
			sc.setId(command.getCampPareId());
			dto.setCampPare(sc);
		}
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
