/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació per a ordenar una consulta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OrdreDto implements Serializable {

	public enum OrdreDireccio {
		ASCENDENT,
		DESCENDENT
	}

	private String camp;
	private OrdreDireccio direccio;

	public OrdreDto(
			String camp,
			OrdreDireccio direccio) {
		this.camp = camp;
		this.direccio = direccio;
	}

	public String getCamp() {
		return camp;
	}
	public void setCamp(String camp) {
		this.camp = camp;
	}
	public OrdreDireccio getDireccio() {
		return direccio;
	}
	public void setDireccio(OrdreDireccio direccio) {
		this.direccio = direccio;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
