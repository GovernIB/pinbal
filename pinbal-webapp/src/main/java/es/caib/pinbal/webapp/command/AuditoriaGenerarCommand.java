/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per a generar les auditories aleatòries.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AuditoriaGenerarCommand {

	@NotNull
	private Date dataInici;
	@NotNull
	private Date dataFi;
	@NotNull
	private Integer numConsultes;
	@NotNull
	private Integer numEntitats;

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
	public Integer getNumConsultes() {
		return numConsultes;
	}
	public void setNumConsultes(Integer numConsultes) {
		this.numConsultes = numConsultes;
	}
	public Integer getNumEntitats() {
		return numEntitats;
	}
	public void setNumEntitats(Integer numEntitats) {
		this.numEntitats = numEntitats;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
