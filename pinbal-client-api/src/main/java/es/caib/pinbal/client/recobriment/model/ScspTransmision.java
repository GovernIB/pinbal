/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

/**
 * Informació de la transmissió d'una sol·licitud SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspTransmision {

	private String codigoCertificado;
	private String idSolicitud;
	private String idTransmision;
	private String fechaGeneracion;
	
	public String getCodigoCertificado() {
		return codigoCertificado;
	}
	
	public void setCodigoCertificado(String codigoCertificado) {
		this.codigoCertificado = codigoCertificado;
	}
	
	public String getIdSolicitud() {
		return idSolicitud;
	}
	
	public void setIdSolicitud(String idSolicitud) {
		this.idSolicitud = idSolicitud;
	}
	
	public String getIdTransmision() {
		return idTransmision;
	}
	
	public void setIdTransmision(String idTransmision) {
		this.idTransmision = idTransmision;
	}
	
	public String getFechaGeneracion() {
		return fechaGeneracion;
	}
	
	public void setFechaGeneracion(String fechaGeneracion) {
		this.fechaGeneracion = fechaGeneracion;
	}

}
