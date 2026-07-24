package es.caib.pinbal.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Objecte DTO amb informació d'un servei SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@ToString
public class EntitatUsuariDto extends AbstractIdentificable<Long> implements Serializable, Comparable<EntitatUsuariDto> {

	private Long entitatId;
	private UsuariDto usuari;
	private String departament;
	private boolean principal;
	private boolean representant; 
	private boolean delegat;
	private boolean auditor;
	private boolean aplicacio;
	private boolean actiu;

	private Boolean acces = false; // Propietat per a mostrar el check amb accés permés als llistats
	private Integer permisosCount;

	public EntitatUsuariDto(
			UsuariDto usuari,
			String departament,
			boolean principal,
			boolean representant,
			boolean delegat,
			boolean auditor,
			boolean aplicacio,
			boolean actiu) {
		this.usuari = usuari;
		this.departament = departament;
		this.principal = principal;
		this.representant = representant;
		this.delegat = delegat;
		this.auditor = auditor;
		this.aplicacio = aplicacio;
		this.actiu = actiu;
	}



	@Override
	public int compareTo(EntitatUsuariDto o) {
		return this.getUsuari().compareTo(o.getUsuari());
	}

	private static final long serialVersionUID = -5757581909233321212L;

}
