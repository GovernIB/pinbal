/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.IntegracioAccioEstatEnumDto;
import es.caib.pinbal.core.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.core.dto.IntegracioFiltreDto;
import es.caib.pinbal.webapp.helper.ConversioTipusHelper;

/**
 * Command per a filtrar les estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class IntegracioFiltreCommand {
	
	private String codi;
	private Date data;
	private String descripcio;	
	private String idPeticio;	
	private IntegracioAccioTipusEnumDto tipus;
	private IntegracioAccioEstatEnumDto estat;
	private EntitatDto entitat;	
	
	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public String getIdPeticio() {
		return idPeticio;
	}

	public void setIdPeticio(String idPeticio) {
		this.idPeticio = idPeticio;
	}

	public IntegracioAccioTipusEnumDto getTipus() {
		return tipus;
	}

	public void setTipus(IntegracioAccioTipusEnumDto tipus) {
		this.tipus = tipus;
	}

	public IntegracioAccioEstatEnumDto getEstat() {
		return estat;
	}

	public void setEstat(IntegracioAccioEstatEnumDto estat) {
		this.estat = estat;
	}

	public EntitatDto getEntitat() {
		return entitat;
	}

	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}
	
	public static IntegracioFiltreCommand asCommand(IntegracioFiltreDto dto) {
		IntegracioFiltreCommand command = ConversioTipusHelper.convertir(
				dto, 
				IntegracioFiltreCommand.class);
		return command;
	}
	
	public static IntegracioFiltreDto asDto(IntegracioFiltreCommand command) {
		IntegracioFiltreDto dto = ConversioTipusHelper.convertir(
				command, 
				IntegracioFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
