/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.pinbal.webapp.helper.CommandMappingHelper;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.pinbal.core.dto.ClauPublicaDto;

/**
 * Command per al manteniment de claus públiques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getAlies() {
		return alies;
	}

	public void setAlies(String alies) {
		this.alies = alies;
	}

	public String getNumSerie() {
		return numSerie;
	}

	public void setNumSerie(String numSerie) {
		this.numSerie = numSerie;
	}

	public Date getDataAlta() {
		return dataAlta;
	}

	public void setDataAlta(Date dataAlta) {
		this.dataAlta = dataAlta;
	}

	public Date getDataBaixa() {
		return dataBaixa;
	}

	public void setDataBaixa(Date dataBaixa) {
		this.dataBaixa = dataBaixa;
	}

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
