/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.Date;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.pinbal.core.dto.ConsultaDto.EstatTipus;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto.EstadistiquesAgrupacioDto;

/**
 * Command per a filtrar les estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EstadistiquesFiltreCommand {

	private Long entitatId;
	private Long procediment;
	@Size(max=64)
	private String servei;
	private EstatTipus estat;
	private Date dataInici;
	private Date dataFi;
	@Size(max=64)
	private String usuariCodi;
	private EstadistiquesAgrupacioDto agrupacio = EstadistiquesAgrupacioDto.PROCEDIMENT_SERVEI;

	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public Long getProcediment() {
		return procediment;
	}
	public void setProcediment(Long procediment) {
		this.procediment = procediment;
	}
	public String getServei() {
		return servei;
	}
	public void setServei(String servei) {
		this.servei = servei;
	}
	public EstatTipus getEstat() {
		return estat;
	}
	public void setEstat(EstatTipus estat) {
		this.estat = estat;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	public EstadistiquesAgrupacioDto getAgrupacio() {
		return agrupacio;
	}
	public void setAgrupacio(EstadistiquesAgrupacioDto agrupacio) {
		this.agrupacio = agrupacio;
	}

	public static EstadistiquesFiltreDto asDto(EstadistiquesFiltreCommand command) {
		if (command == null)
			return null;
		EstadistiquesFiltreDto dto = CommandMappingHelper.getMapperFacade().map(
				command,
				EstadistiquesFiltreDto.class);
		dto.setProcedimentId(command.getProcediment());
		if (command.getServei() != null && !command.getServei().isEmpty())
			dto.setServeiCodi(command.getServei());
		if (dto.getUsuariCodi() != null && dto.getUsuariCodi().isEmpty())
			dto.setUsuariCodi(null);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
