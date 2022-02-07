/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'una dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class OrganGestorDto extends AbstractIdentificable<Long> implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private String entitatId;
	private String pareCodi;
	private String entitatNom;
	private boolean actiu;

	public String getCodiINom() {
		return codi + " - " + nom;
	}

	public String getNomICodi() {
		return nom + " (" + codi + ")";
	}

	private static final long serialVersionUID = -6776483458084380673L;

}
