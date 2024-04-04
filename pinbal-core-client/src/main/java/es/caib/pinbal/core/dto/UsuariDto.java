package es.caib.pinbal.core.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Objecte DTO amb informació d'un servei SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class UsuariDto extends AbstractIdentificable<Long> implements Serializable, Comparable<UsuariDto> {

	private String codi;
	private String nif;
	private String nom;
	private boolean inicialitzat;
	private boolean noInicialitzatNif;
	private boolean noInicialitzatCodi;
	private String[] rols;
	private String email;
	private String idioma;
	// Valors per defecte
	private Long procedimentId;
	private String serveiCodi;
	private Long entitatId;
	private String departament;
	private String finalitat;
	private boolean hasMultiplesEntitats;

	public UsuariDto() {
	}
	public UsuariDto(
			String codi,
			String nif,
			String nom,
			boolean inicialitzat,
			boolean noInicialitzatNif,
			boolean noInicialitzatCodi) {
//			Long procedimentId,
//			String serveiCodi,
//			Long entitatId,
//			String departament,
//			String finalitat) {
		this.codi = codi;
		this.nif = nif;
		this.nom = nom;
		this.inicialitzat = inicialitzat;
		this.noInicialitzatNif = noInicialitzatNif;
		this.noInicialitzatCodi = noInicialitzatCodi;
//		this.procedimentId = procedimentId;
//		this.serveiCodi = serveiCodi;
//		this.entitatId = entitatId;
//		this.departament = departament;
//		this.finalitat = finalitat;
	}

	public String getDescripcio() {
		if (inicialitzat) {
			if (nif == null || nif.isEmpty())
				return nom + " (" + codi + ")";
			else
				return nom + " (NIF: " + nif + ")";
		} else {
			if (noInicialitzatNif) {
				return "NIF: " + nif;
			} else if (noInicialitzatCodi) {
				return codi;
			} else {
				// En teoria no pot passar
				return "¿" + codi + "?";
			}
		}
	}

	public String getNomCodi() {
		return nom + " (" + codi + ")";
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -5757581909233321212L;

	@Override
	public int compareTo(UsuariDto o) {
		return this.getCodi().compareTo(o.getCodi());
	}

}
