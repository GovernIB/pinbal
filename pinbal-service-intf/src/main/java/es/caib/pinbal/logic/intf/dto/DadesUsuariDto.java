/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Dades d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString
public class DadesUsuariDto implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String email;

	private static final long serialVersionUID = -139254994389509932L;

}
