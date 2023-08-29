/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.Date;

import javax.validation.constraints.Size;

import es.caib.pinbal.webapp.helper.CommandMappingHelper;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.core.dto.EmissorCertDto;
import es.caib.pinbal.webapp.validation.DocumentIdentitat;

/**
 * Command per al manteniment d'emisors certificats
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EmissorCertCommand {

	private Long id;
	
	@NotEmpty @Size(max=50)
	private String nom;
	@NotEmpty @Size(max=16)
	@DocumentIdentitat(documentTipus=DocumentTipus.CIF)
	private String cif;
	
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
	
	public String getCif() {
		return cif;
	}
	public void setCif(String cif) {
		this.cif = cif;
	}
	
	public Date getDataBaixa() {
		return dataBaixa;
	}
	public void setDataBaixa(Date dataBaixa) {
		this.dataBaixa = dataBaixa;
	}
	

	public static EmissorCertCommand asCommand(EmissorCertDto dto) {
		return CommandMappingHelper.getMapperFacade().map(
				dto,
				EmissorCertCommand.class);
	}
	
	public static EmissorCertDto asDto(EmissorCertCommand command) {
		return CommandMappingHelper.getMapperFacade().map(
				command,
				EmissorCertDto.class);
	}
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
