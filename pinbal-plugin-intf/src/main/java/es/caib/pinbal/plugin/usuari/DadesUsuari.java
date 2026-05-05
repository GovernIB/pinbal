/**
 * 
 */
package es.caib.pinbal.plugin.usuari;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Dades d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class DadesUsuari implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String email;
	private String[] rols;

	private static final long serialVersionUID = -139254994389509932L;

}
