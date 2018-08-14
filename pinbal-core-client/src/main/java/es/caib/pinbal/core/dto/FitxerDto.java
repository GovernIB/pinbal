/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un fitxer.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FitxerDto implements Serializable {

	private String nom;
	private String contentType;
	private byte[] contingut;
	private long tamany;



	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public byte[] getContingut() {
		return contingut;
	}
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
		if (contingut != null)
			this.tamany = contingut.length;
		else
			this.tamany = 0;
	}
	public long getTamany() {
		return tamany;
	}
	public void setTamany(long tamany) {
		this.tamany = tamany;
	}

	public String getExtensio() {
		int indexPunt = nom.lastIndexOf(".");
		if (indexPunt != -1 && indexPunt < nom.length() - 1) {
			return nom.substring(indexPunt + 1);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
