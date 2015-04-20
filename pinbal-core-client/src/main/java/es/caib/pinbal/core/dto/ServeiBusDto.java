/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Dades d'una redirecció d'un servei per al bus.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiBusDto implements Serializable {

	private Long id;
	private String servei;
	private String urlDesti;
	private EntitatDto entitat;

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
	public String getUrlDesti() {
		return urlDesti;
	}
	public void setUrlDesti(String urlDesti) {
		this.urlDesti = urlDesti;
	}
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
