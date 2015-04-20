package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Objecte DTO amb informació d'un servei SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatUsuariDto implements Serializable {

	private UsuariDto usuari;
	private String departament;
	private boolean principal;
	private boolean representant; 
	private boolean delegat;
	private boolean auditor;
	private boolean aplicacio;

	public EntitatUsuariDto(
			UsuariDto usuari,
			String departament,
			boolean principal,
			boolean representant,
			boolean delegat,
			boolean auditor,
			boolean aplicacio) {
		this.usuari = usuari;
		this.departament = departament;
		this.principal = principal;
		this.representant = representant;
		this.delegat = delegat;
		this.auditor = auditor;
		this.aplicacio = aplicacio;
	}

	public UsuariDto getUsuari() {
		return usuari;
	}
	public void setUsuari(UsuariDto usuari) {
		this.usuari = usuari;
	}
	public String getDepartament() {
		return departament;
	}
	public void setDepartament(String departament) {
		this.departament = departament;
	}
	public boolean isPrincipal() {
		return principal;
	}
	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}
	public boolean isRepresentant() {
		return representant;
	}
	public void setRepresentant(boolean representant) {
		this.representant = representant;
	}
	public boolean isDelegat() {
		return delegat;
	}
	public void setDelegat(boolean delegat) {
		this.delegat = delegat;
	}
	public boolean isAuditor() {
		return auditor;
	}
	public void setAuditor(boolean auditor) {
		this.auditor = auditor;
	}
	public boolean isAplicacio() {
		return aplicacio;
	}
	public void setAplicacio(boolean aplicacio) {
		this.aplicacio = aplicacio;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -5757581909233321212L;

}
