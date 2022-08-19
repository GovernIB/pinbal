/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

/**
 * Informació d'una dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@EqualsAndHashCode
public class OrganGestorDto extends AbstractIdentificable<Long> implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private String entitatId;
	private String pareCodi;
	private String pareNom;
	private String entitatNom;
	private boolean actiu;
	private OrganGestorEstatEnum estat;

	public String getCodiINom() {
		return codi + " - " + nom;
	}

	public String getPareCodiINom() {
		return StringUtils.isBlank(pareCodi) ? "" : pareCodi + " - " + pareNom;
	}

	private static final long serialVersionUID = -6776483458084380673L;

}
