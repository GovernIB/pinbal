/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

/**
 * Informació del titular d'una sol·licitud SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspTitular {

	private ScspTipoDocumentacion tipoDocumentacion;
	private String documentacion;
	private String nombreCompleto;
	private String nombre;
	private String apellido1;
	private String apellido2;
	
	public ScspTipoDocumentacion getTipoDocumentacion() {
		return tipoDocumentacion;
	}
	
	public void setTipoDocumentacion(ScspTipoDocumentacion tipoDocumentacion) {
		this.tipoDocumentacion = tipoDocumentacion;
	}
	
	public String getDocumentacion() {
		return documentacion;
	}
	
	public void setDocumentacion(String documentacion) {
		this.documentacion = documentacion;
	}
	
	public String getNombreCompleto() {
		return nombreCompleto;
	}
	
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getApellido1() {
		return apellido1;
	}
	
	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}
	
	public String getApellido2() {
		return apellido2;
	}
	
	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}

	public enum ScspTipoDocumentacion {
		CIF,
		CSV,
		DNI,
		NIE,
		NIF,
		Pasaporte,
		NumeroIdentificacion,
		Otros
	}

}
