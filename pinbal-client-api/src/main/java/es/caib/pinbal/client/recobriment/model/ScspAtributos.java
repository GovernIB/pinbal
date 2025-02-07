/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

/**
 * Informació dels atributs d'una petició SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspAtributos {

	private String idPeticion;
	private String numElementos;
	private String timeStamp;
	private String codigoCertificado;
	private ScspEstado estado;
	
	public String getIdPeticion() {
		return idPeticion;
	}
	
	public void setIdPeticion(String idPeticion) {
		this.idPeticion = idPeticion;
	}
	
	public String getNumElementos() {
		return numElementos;
	}
	
	public void setNumElementos(String numElementos) {
		this.numElementos = numElementos;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public String getCodigoCertificado() {
		return codigoCertificado;
	}
	
	public void setCodigoCertificado(String codigoCertificado) {
		this.codigoCertificado = codigoCertificado;
	}
	
	public ScspEstado getEstado() {
		return estado;
	}
	
	public void setEstado(ScspEstado estado) {
		this.estado = estado;
	}

}
