/**
 * 
 */
package es.caib.distribucio.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.pinbal.core.dto.AbstractIdentificable;
import es.caib.pinbal.core.dto.AvisNivellEnumDto;

/**
 * Informació d'una avis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AvisDto extends AbstractIdentificable<Long> implements Serializable {


	private Long id;
	private String assumpte;
	private String missatge;
	private Date dataInici;
	private Date dataFinal;
	private Boolean actiu;
	private AvisNivellEnumDto avisNivell;

	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAssumpte() {
		return assumpte;
	}
	public void setAssumpte(String assumpte) {
		this.assumpte = assumpte;
	}
	public String getMissatge() {
		return missatge;
	}
	public void setMissatge(String missatge) {
		this.missatge = missatge;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFinal() {
		return dataFinal;
	}
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	public Boolean getActiu() {
		return actiu;
	}
	public void setActiu(Boolean actiu) {
		this.actiu = actiu;
	}
	public AvisNivellEnumDto getAvisNivell() {
		return avisNivell;
	}
	public void setAvisNivell(AvisNivellEnumDto avisNivell) {
		this.avisNivell = avisNivell;
	}
	public boolean isInfo() {
		return avisNivell == AvisNivellEnumDto.INFO;
	}
	public boolean isWarning() {
		return avisNivell == AvisNivellEnumDto.WARNING;
	}
	public boolean isError() {
		return avisNivell == AvisNivellEnumDto.ERROR;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	
	private static final long serialVersionUID = 1L;
}
