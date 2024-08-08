/**
 * 
 */
package es.caib.pinbal.webapp.command;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.RolEnumDto;
import es.caib.pinbal.core.dto.UsuariEstatEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per a filtrar els usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private String eliminarEspais(String str) {
		return (str != null) ? str.trim() : null;
	}

}
