/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.pinbal.core.dto.ClauPrivadaDto;

/**
 * Command per al manteniment d'entitats
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getAlies() {
		return alies;
	}
	public void setAlies(String alies) {
		this.alies = alies;
	}
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getNumSerie() {
		return numSerie;
	}
	public void setNumSerie(String numSerie) {
		this.numSerie = numSerie;
	}
	
	public Date getDataBaixa() {
		return dataBaixa;
	}
	public void setDataBaixa(Date dataBaixa) {
		this.dataBaixa = dataBaixa;
	}
	
	public Date getDataAlta() {
		return dataAlta;
	}
	public void setDataAlta(Date dataAlta) {
		this.dataAlta = dataAlta;
	}
	
	public boolean getInteroperabilitat() {
		return interoperabilitat;
	}
	public void setInteroperabilitat(boolean interoperabilitat) {
		this.interoperabilitat = interoperabilitat;
	}
		
	public Long getOrganisme() {
		return organisme;
	}
	public void setOrganisme(Long organisme) {
		this.organisme = organisme;
	}
	
	
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
