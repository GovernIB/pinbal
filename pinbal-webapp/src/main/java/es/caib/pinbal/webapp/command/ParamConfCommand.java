/**
 * 
 */
package es.caib.pinbal.webapp.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.pinbal.core.dto.ParamConfDto;
import es.caib.pinbal.webapp.validation.NomParamConfNoRepetit;

/**
 * Command per al manteniment de paràmetres de configuració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@NomParamConfNoRepetit(campNom = "nom")
public class ParamConfCommand {

	@NotEmpty @Size(max=64)
	private String nom;
	@NotEmpty @Size(max=512)
	private String valor;
	@Size(max=512)
	private String descripcio;
	private boolean forcreate;
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
		
	public boolean isForcreate() {
		return forcreate;
	}
	public void setForcreate(boolean forcreate) {
		this.forcreate = forcreate;
	}
	
	
	public static ParamConfCommand commandForCreate() {
		
		ParamConfCommand command = new ParamConfCommand();
		command.setForcreate(true);
		
		return command;
	}
	
	public static ParamConfCommand asCommandForUpdate(ParamConfDto dto) {
		
		ParamConfCommand command = CommandMappingHelper.getMapperFacade().map(
				dto,
				ParamConfCommand.class);
		command.setForcreate(false);
		
		return command;
	}
	
	public static ParamConfCommand asCommand(ParamConfDto dto) {
		
		return CommandMappingHelper.getMapperFacade().map(
				dto,
				ParamConfCommand.class);
	}
	
	public static ParamConfDto asDto(ParamConfCommand command) {
		
		return CommandMappingHelper.getMapperFacade().map(
				command,
				ParamConfDto.class);
	}
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
