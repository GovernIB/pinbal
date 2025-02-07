/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

/**
 * Informació genèrica d'una sol·licitud SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspDatosGenericos {

	private ScspEmisor emisor;
	private ScspSolicitante solicitante;
	private ScspTitular titular;
	private ScspTransmision transmision;
	
	public ScspEmisor getEmisor() {
		return emisor;
	}
	
	public void setEmisor(ScspEmisor emisor) {
		this.emisor = emisor;
	}
	
	public ScspSolicitante getSolicitante() {
		return solicitante;
	}
	
	public void setSolicitante(ScspSolicitante solicitante) {
		this.solicitante = solicitante;
	}
	
	public ScspTitular getTitular() {
		return titular;
	}
	
	public void setTitular(ScspTitular titular) {
		this.titular = titular;
	}
	
	public ScspTransmision getTransmision() {
		return transmision;
	}
	
	public void setTransmision(ScspTransmision transmision) {
		this.transmision = transmision;
	}

}
