/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

/**
 * Informació de l'emissor d'una sol·licitud SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspEmisor {

	private String nifEmisor;
	private String nombreEmisor;
	
	public String getNifEmisor() {
		return nifEmisor;
	}
	
	public void setNifEmisor(String nifEmisor) {
		this.nifEmisor = nifEmisor;
	}
	
	public String getNombreEmisor() {
		return nombreEmisor;
	}
	
	public void setNombreEmisor(String nombreEmisor) {
		this.nombreEmisor = nombreEmisor;
	}

}
