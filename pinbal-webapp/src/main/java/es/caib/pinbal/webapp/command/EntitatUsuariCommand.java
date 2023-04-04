/**
 * 
 */
package es.caib.pinbal.webapp.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.pinbal.webapp.validation.DocumentIdentitatNie;
import es.caib.pinbal.webapp.validation.DocumentIdentitatNif;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.webapp.validation.DocumentIdentitat;

/**
 * Command per als usuaris de les entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatUsuariCommand {

	public static final String CARACTER_NIF = "N";
	public static final String CARACTER_CODI = "C";

	public static final String CARACTER_REPRESENTANT = "R";
	public static final String CARACTER_DELEGAT = "D";
	public static final String CARACTER_AUDITOR = "A";
	public static final String CARACTER_APLICACIO = "P";

	@NotNull
	private Long id;
	@Size(max = 64)
	@NotEmpty(groups = {TipusCodi.class, Existent.class})
	private String codi;
	private String tipus;
	@Size(max = 64)
	@NotEmpty(groups = {TipusNif.class, Existent.class})
	@DocumentIdentitatNif(groups = {TipusNif.class})
	@DocumentIdentitatNie(groups = {TipusNie.class})
	private String nif;
	@Size(max = 64)
	private String departament;
	private boolean rolRepresentant;
	private boolean rolDelegat;
	private boolean rolAuditor;
	private boolean rolAplicacio;
	private boolean afegir = false;
	private boolean actiu;

	public EntitatUsuariCommand() {}
	public EntitatUsuariCommand(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getDepartament() {
		return departament;
	}
	public void setDepartament(String departament) {
		this.departament = departament;
	}
	public boolean isRolRepresentant() {
		return rolRepresentant;
	}
	public void setRolRepresentant(boolean rolRepresentant) {
		this.rolRepresentant = rolRepresentant;
	}
	public boolean isRolDelegat() {
		return rolDelegat;
	}
	public void setRolDelegat(boolean rolDelegat) {
		this.rolDelegat = rolDelegat;
	}
	public boolean isRolAuditor() {
		return rolAuditor;
	}
	public void setRolAuditor(boolean rolAuditor) {
		this.rolAuditor = rolAuditor;
	}
	public boolean isRolAplicacio() {
		return rolAplicacio;
	}
	public void setRolAplicacio(boolean rolAplicacio) {
		this.rolAplicacio = rolAplicacio;
	}
	public boolean isAfegir() {
		return afegir;
	}
	public void setAfegir(boolean afegir) {
		this.afegir = afegir;
	}
	public boolean isActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	
	public String getRols() {
		StringBuilder rols = new StringBuilder();
		if (isRolRepresentant())
			rols.append(CARACTER_REPRESENTANT);
		if (isRolDelegat())
			rols.append(CARACTER_DELEGAT);
		if (isRolAuditor())
			rols.append(CARACTER_AUDITOR);
		if (isRolAplicacio())
			rols.append(CARACTER_APLICACIO);
		return rols.toString();
	}
	public void setRols(String rols) {
		setRolRepresentant(rols.contains(CARACTER_REPRESENTANT));
		setRolDelegat(rols.contains(CARACTER_DELEGAT));
		setRolAuditor(rols.contains(CARACTER_AUDITOR));
		setRolAplicacio(rols.contains(CARACTER_APLICACIO));
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public interface TipusNif {}
	public interface TipusNie {}
	public interface TipusCodi {}
	public interface Existent {}

}
