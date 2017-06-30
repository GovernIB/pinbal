/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una ClauPrivada SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OrganismeCessionariDto implements Serializable {
	
	
	private Long id;
	private String nom;
	private String cif;
	private Date dataBaixa;
	private Date dataAlta;
	private Boolean bloquejat;
	private byte[] logo;
	private List<ClauPrivadaDto> claus;
	
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
	
	public Date getDataAlta() {
		return dataAlta;
	}
	public void setDataAlta(Date dataAlta) {
		this.dataAlta = dataAlta;
	}
	
	public Boolean getBloquejat() {
		return bloquejat;
	}
	public void setBloquejat(Boolean bloquejat) {
		this.bloquejat = bloquejat;
	}
	
	public byte[] getLogo() {
		return logo;
	}
	public void setLogo(byte[] logo) {
		this.logo = logo;
	}
	
	public List<ClauPrivadaDto> getClaus() {
		return claus;
	}
	public void setClaus(List<ClauPrivadaDto> claus) {
		this.claus = claus;
	}
	
	
	public String getCadenaIdentificadora() {
		return nom + "(" + cif + ")";
	}
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = -8620175604318725073L;

}
