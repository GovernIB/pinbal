/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

/**
 * Informació d'un fitxer.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspJustificante {

	private String nom;
	private String contentType;
	private byte[] contingut;

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
	}

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
