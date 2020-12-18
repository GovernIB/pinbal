/**
 * 
 */
package es.caib.pinbal.webapp.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.RolEnumDto;

/**
 * Command per a filtrar els usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	
	
	public RolEnumDto getRol() {
		return rol;
	}
	public void setRol(RolEnumDto rol) {
		this.rol = rol;
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
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDepartament() {
		return departament;
	}
	public void setDepartament(String departament) {
		this.departament = departament;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}

}
