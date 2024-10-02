/**
 * 
 */
package es.caib.pinbal.webapp.command;

import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.webapp.validation.DocumentIdentitatNie;
import es.caib.pinbal.webapp.validation.DocumentIdentitatNif;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Command per als usuaris de les entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
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
	private String nom;
	private String email;
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


	public static EntitatUsuariCommand asCommand(EntitatUsuariDto dto, Long entitatId) {
		EntitatUsuariCommand command = new EntitatUsuariCommand(entitatId);
		command.setCodi(dto.getUsuari().getCodi());
		command.setNom(dto.getUsuari().getNom());
		command.setNif(dto.getUsuari().getNif());
		command.setEmail(dto.getUsuari().getEmail());
		command.setDepartament(dto.getDepartament());
		command.setActiu(dto.isActiu());
		command.setAfegir(false);
		command.setRolRepresentant(dto.isRepresentant());
		command.setRolDelegat(dto.isDelegat());
		command.setRolAuditor(dto.isAuditor());
		command.setRolAplicacio(dto.isAplicacio());
		return command;
	}

//	EntitatUsuariDto asDto(EntitatUsuariCommand command) {
//		EntitatUsuariDto dto = CommandMappingHelper.getMapperFacade().map(command, EntitatUsuariDto.class);
//		return dto;
//	}
}
