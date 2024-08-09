/**
 * 
 */
package es.caib.pinbal.core.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Dades d'una traducció d'un camp de dades específiques d'un
 * servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ServeiJustificantCampDto implements Serializable {

	private Long id;
	private String servei;
	private String xpath;
	private String traduccio;
	private boolean document;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
