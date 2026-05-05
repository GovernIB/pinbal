/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Dades d'una traducció d'un camp de dades específiques d'un
 * servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@ToString
public class ServeiJustificantCampDto implements Serializable {

	private Long id;
	private String servei;
	private String xpath;
	private String traduccio;
	private boolean document;

	private static final long serialVersionUID = -139254994389509932L;

}
