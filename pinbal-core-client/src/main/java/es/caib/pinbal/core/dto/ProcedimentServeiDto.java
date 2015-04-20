package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Objecte DTO amb informació d'una parella procediment-servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentServeiDto implements Serializable {

	private ProcedimentDto procediment;
	private ServeiDto servei;

	public ProcedimentServeiDto() {
	}

	public ProcedimentDto getProcediment() {
		return procediment;
	}
	public void setProcediment(ProcedimentDto procediment) {
		this.procediment = procediment;
	}
	public ServeiDto getServei() {
		return servei;
	}
	public void setServei(ServeiDto servei) {
		this.servei = servei;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = 3986823331500016935L;

}
