/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Dades d'un arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuDto implements Serializable {

	private String nom;
	private byte[] contingut;

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public byte[] getContingut() {
		return contingut;
	}
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
	}

	public String getExtensio() {
		if (nom.indexOf(".") == -1)
			return null;
		return nom.substring(nom.lastIndexOf(".") + 1);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
