/**
 * 
 */
package es.caib.pinbal.core.dto.arxiu;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Informació d'un contingut emmagatzemada a l'arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ArxiuContingutDto implements Serializable {

	protected String identificador;
	protected String nom;
	protected ArxiuContingutTipusEnumDto tipus;

	private static final long serialVersionUID = -2124829280908976623L;

}
