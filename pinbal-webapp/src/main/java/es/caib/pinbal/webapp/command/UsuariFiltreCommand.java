/**
 * 
 */
package es.caib.pinbal.webapp.command;

import es.caib.pinbal.core.dto.UsuariEstatEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.RolEnumDto;

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

	public Boolean getIsRepresentant() {
		return isRepresentant;
	}
	public void setIsRepresentant(Boolean isRepresentant) {
		this.isRepresentant = isRepresentant;
	}
	public Boolean getIsDelegat() {
		return isDelegat;
	}
	public void setIsDelegat(Boolean isDelegat) {
		this.isDelegat = isDelegat;
	}
	public Boolean getIsAuditor() {
		return isAuditor;
	}
	public void setIsAuditor(Boolean isAuditor) {
		this.isAuditor = isAuditor;
	}
	public Boolean getIsAplicacio() {
		return isAplicacio;
	}
	public void setIsAplicacio(Boolean isAplicacio) {
		this.isAplicacio = isAplicacio;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private String eliminarEspais(String str) {
		return (str != null) ? str.trim() : null;
	}

}
