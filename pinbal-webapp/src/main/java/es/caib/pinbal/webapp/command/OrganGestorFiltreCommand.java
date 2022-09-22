/**
 * 
 */
package es.caib.pinbal.webapp.command;

import es.caib.pinbal.core.dto.OrganGestorEstatEnum;
import es.caib.pinbal.core.dto.OrganGestorEstatEnumDto;
import lombok.Data;

/**
 * Command per a filtrar els organs gestors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class OrganGestorFiltreCommand {

	private String codi;
	private String nom;
	private OrganGestorEstatEnum estat;
	private Long entitatId;
	private String pareCodi;

	public OrganGestorFiltreCommand() {
		super();
	}

	public OrganGestorFiltreCommand(Long entitatId) {
		this.entitatId = entitatId;
	}

	// Elimina els espais en els camps de cerca
	public void eliminarEspaisCampsCerca() {
		this.codi = eliminarEspais(this.codi);
		this.nom = eliminarEspais(this.nom);
	}

	private String eliminarEspais(String str) {
		return (str != null) ? str.trim() : null;
	}
}
