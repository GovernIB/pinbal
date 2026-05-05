/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * Dades d'una integració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class IntegracioDto implements Serializable {

	private String codi;
	private String nom;
	private int numErrors;

	private static final long serialVersionUID = -139254994389509932L;

}
