/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.RolEnumDto;
import es.caib.pinbal.logic.intf.dto.UsuariEstatEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command per a filtrar els usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuariFiltreCommand {

	private String codi;
	private String nif;
	private String nom;
	private String email;
	private String departament;

	private Boolean isRepresentant;
	private Boolean isDelegat;
	private Boolean isAuditor;
	private Boolean isAplicacio;
	
	private RolEnumDto rol;
	
	private EntitatDto entitat = null;

	private UsuariEstatEnum actiu = UsuariEstatEnum.ACTIU;

	// Elimina els espais en els camps de cerca
	public void eliminarEspaisCampsCerca() {
		this.codi = eliminarEspais(this.codi);
		this.nif = eliminarEspais(this.nif);
		this.nom = eliminarEspais(this.nom);
		this.departament = eliminarEspais(this.departament);
	}

	private String eliminarEspais(String str) {
		return (str != null) ? str.trim() : null;
	}

}
