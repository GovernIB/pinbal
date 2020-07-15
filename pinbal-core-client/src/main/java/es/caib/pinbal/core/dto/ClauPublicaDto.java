/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una ClavePublica SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClauPublicaDto extends AbstractIdentificable<Long> implements Serializable {

	private Long id;

	private String alies;
	private String nom;
	private String numSerie;
	private Date dataAlta;
	private Date dataBaixa;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getAlies() {
		return alies;
	}
	public void setAlies(String alies) {
		this.alies = alies;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getNumSerie() {
		return numSerie;
	}
	public void setNumSerie(String numSerie) {
		this.numSerie = numSerie;
	}
	public Date getDataAlta() {
		return dataAlta;
	}
	public void setDataAlta(Date dataAlta) {
		this.dataAlta = dataAlta;
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

	private static final long serialVersionUID = -8620175604318725073L;

}
