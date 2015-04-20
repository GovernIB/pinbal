package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Objecte DTO amb informació d'un servei SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariDto implements Serializable {

	private String codi;
	private String nif;
	private String nom;
	private boolean inicialitzat;
	private boolean noInicialitzatNif;
	private boolean noInicialitzatCodi;

	public UsuariDto() {
	}
	public UsuariDto(
			String codi,
			String nif,
			String nom,
			boolean inicialitzat,
			boolean noInicialitzatNif,
			boolean noInicialitzatCodi) {
		this.codi = codi;
		this.nif = nif;
		this.nom = nom;
		this.inicialitzat = inicialitzat;
		this.noInicialitzatNif = noInicialitzatNif;
		this.noInicialitzatCodi = noInicialitzatCodi;
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
	public boolean isInicialitzat() {
		return inicialitzat;
	}
	public void setInicialitzat(boolean inicialitzat) {
		this.inicialitzat = inicialitzat;
	}
	public boolean isNoInicialitzatNif() {
		return noInicialitzatNif;
	}
	public void setNoInicialitzatNif(boolean noInicialitzatNif) {
		this.noInicialitzatNif = noInicialitzatNif;
	}
	public boolean isNoInicialitzatCodi() {
		return noInicialitzatCodi;
	}
	public void setNoInicialitzatCodi(boolean noInicialitzatCodi) {
		this.noInicialitzatCodi = noInicialitzatCodi;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -5757581909233321212L;

}
