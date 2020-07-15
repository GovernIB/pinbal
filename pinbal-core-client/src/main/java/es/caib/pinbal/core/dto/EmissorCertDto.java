package es.caib.pinbal.core.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Objecte DTO amb informació d'un emisor certificat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EmissorCertDto extends AbstractIdentificable<Long> implements Serializable {
	
	private Long id;
	
	private String nom;
	private String cif;
	private Date dataBaixa;

	public EmissorCertDto() {}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public String getCif() {
		return cif;
	}
	public void setCif(String cif) {
		this.cif = cif;
	}
	
	public Date getDataBaixa() {
		return dataBaixa;
	}
	public void setDataBaixa(Date dataBaixa) {
		this.dataBaixa = dataBaixa;
	}
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = -2822106398117415005L;
	
}
