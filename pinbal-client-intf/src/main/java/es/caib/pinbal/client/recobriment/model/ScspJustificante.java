/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

import lombok.Data;

/**
 * Informació d'un fitxer.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ScspJustificante {

	private String nom;
	private String contentType;
	private byte[] contingut;

	public long getLongitud() {
		return (contingut != null) ? contingut.length : 0;
	}

	public String getExtensio() {
		int indexPunt = nom.lastIndexOf(".");
		if (indexPunt != -1 && indexPunt < nom.length() - 1) {
			return nom.substring(indexPunt + 1);
		} else {
			return null;
		}
	}

}
