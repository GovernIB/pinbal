/**
 * 
 */
package es.caib.pinbal.webapp.command;

import es.caib.pinbal.core.dto.OrganGestorEstatEnumDto;
import lombok.Data;

/**
 * Command per a filtrar les organs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class OrganGestorFiltreCommand {

	private String codi;
	private String nom;
	private OrganGestorEstatEnumDto estat;
	private Long entitatId;

	public OrganGestorFiltreCommand() {
		super();
	}

	public OrganGestorFiltreCommand(Long entitatId) {
		this.entitatId = entitatId;
	}

}
