/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Dades d'una traducció d'un camp de dades específiques d'un
 * servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiJustificantCampDto implements Serializable {

	private Long id;
	private String servei;
	private String xpath;
	private String traduccio;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getServei() {
		return servei;
	}
	public void setServei(String servei) {
		this.servei = servei;
	}
	public String getXpath() {
		return xpath;
	}
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	public String getTraduccio() {
		return traduccio;
	}
	public void setTraduccio(String traduccio) {
		this.traduccio = traduccio;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
