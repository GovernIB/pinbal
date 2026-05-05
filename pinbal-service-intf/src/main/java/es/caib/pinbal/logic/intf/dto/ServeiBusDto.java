/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Dades d'una redirecció d'un servei per al bus.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString
public class ServeiBusDto implements Serializable {

	private Long id;
	private String servei;
	private String urlDesti;
	private EntitatDto entitat;

	private static final long serialVersionUID = -139254994389509932L;

}
