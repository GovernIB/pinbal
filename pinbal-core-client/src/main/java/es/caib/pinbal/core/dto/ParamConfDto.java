package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Objecte DTO amb informació d'un servei SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ParamConfDto extends AbstractIdentificable<Long> implements Serializable {

	private String nom;
	private String valor;
	private String descripcio;

	public ParamConfDto() {}

	public ParamConfDto(
			String nom,
			String valor,
			String descripcio) {
		this.nom = nom;
		this.valor = valor;
		this.descripcio = descripcio;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -5757581909233321212L;

}
