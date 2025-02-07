/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

/**
 * Informació sobre l'estat d'una petició SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspEstado {

	private String codigoEstado;
	private String codigoEstadoSecundario;
	private String literalError;
	private String literalErrorSec;
	private Integer tiempoEstimadoRespuesta;
	
	public String getCodigoEstado() {
		return codigoEstado;
	}
	
	public void setCodigoEstado(String codigoEstado) {
		this.codigoEstado = codigoEstado;
	}
	
	public String getCodigoEstadoSecundario() {
		return codigoEstadoSecundario;
	}
	
	public void setCodigoEstadoSecundario(String codigoEstadoSecundario) {
		this.codigoEstadoSecundario = codigoEstadoSecundario;
	}
	
	public String getLiteralError() {
		return literalError;
	}
	
	public void setLiteralError(String literalError) {
		this.literalError = literalError;
	}
	
	public String getLiteralErrorSec() {
		return literalErrorSec;
	}
	
	public void setLiteralErrorSec(String literalErrorSec) {
		this.literalErrorSec = literalErrorSec;
	}
	
	public Integer getTiempoEstimadoRespuesta() {
		return tiempoEstimadoRespuesta;
	}
	
	public void setTiempoEstimadoRespuesta(Integer tiempoEstimadoRespuesta) {
		this.tiempoEstimadoRespuesta = tiempoEstimadoRespuesta;
	}

}
