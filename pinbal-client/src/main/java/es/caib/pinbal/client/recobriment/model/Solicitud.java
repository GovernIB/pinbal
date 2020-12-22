/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

/**
 * Paràmetres resumits per a una sol·licitud SCSP genèrica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Solicitud extends SolicitudBase {

	private String datosEspecificos;

	public String getDatosEspecificos() {
		return datosEspecificos;
	}
	public void setDatosEspecificos(String datosEspecificos) {
		this.datosEspecificos = datosEspecificos;
	}

}
