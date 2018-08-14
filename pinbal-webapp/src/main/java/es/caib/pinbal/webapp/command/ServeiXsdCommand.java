package es.caib.pinbal.webapp.command;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.caib.pinbal.core.dto.ServeiXsdDto;

//import com.fasterxml.jackson.annotation.JsonIgnore;

import es.caib.pinbal.core.dto.XsdTipusEnumDto;
import es.caib.pinbal.webapp.helper.ConversioTipusHelper;


/**
 * Informació d'un fitxer XSD d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiXsdCommand {

	private String codi;
	@NotNull
	private XsdTipusEnumDto tipus;
	@NotNull
	private String nomArxiu;
	@JsonIgnore
	@NotNull
	private MultipartFile contingut;

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public XsdTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(XsdTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public MultipartFile getContingut() {
		return contingut;
	}
	public void setContingut(MultipartFile contingut) {
		this.contingut = contingut;
	}
	public String getNomArxiu() {
		return nomArxiu;
	}
	public void setNomArxiu(String nomArxiu) {
		this.nomArxiu = nomArxiu;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public static ServeiXsdCommand asCommand(ServeiXsdDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				ServeiXsdCommand.class);
	}
	public static ServeiXsdDto asDto(ServeiXsdCommand command) throws IOException {
		return ConversioTipusHelper.convertir(
				command,
				ServeiXsdDto.class);
	}
}
