package es.caib.pinbal.core.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.pinbal.core.dto.ConsultaDto.EstatTipus;

/**
 * Objecte DTO amb informació per filtrar les estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EstadistiquesFiltreDto implements Serializable {

	public enum EstadistiquesAgrupacioDto {
		PROCEDIMENT_SERVEI,
		SERVEI_PROCEDIMENT
	}

	private Long entitatId;
	private Long procedimentId;
	private String serveiCodi;
	private EstatTipus estat;
	private Date dataInici;
	private Date dataFi;
	private String usuariCodi;
	private EstadistiquesAgrupacioDto agrupacio;

	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public Long getProcedimentId() {
		return procedimentId;
	}
	public void setProcedimentId(Long procedimentId) {
		this.procedimentId = procedimentId;
	}
	public String getServeiCodi() {
		return serveiCodi;
	}
	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -2822106398117415005L;

}
