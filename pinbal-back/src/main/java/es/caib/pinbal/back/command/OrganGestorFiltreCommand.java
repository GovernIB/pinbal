/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.OrganGestorEstatEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command per a filtrar els organs gestors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganGestorFiltreCommand {

	private String codi;
	private String nom;
	private OrganGestorEstatEnum estat;
	private Long entitatId;
	private String pareCodi;

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
