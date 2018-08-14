package es.caib.pinbal.core.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un fitxer XSD d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiXsdDto {

	private XsdTipusEnumDto tipus;
	private String nomArxiu;

	public XsdTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(XsdTipusEnumDto tipus) {
		this.tipus = tipus;
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
}
