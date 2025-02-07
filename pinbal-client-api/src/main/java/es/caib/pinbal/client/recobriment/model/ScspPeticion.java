/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

import java.util.List;

/**
 * Informació d'una petició SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspPeticion {

	private ScspAtributos atributos;
	private List<ScspSolicitud> solicitudes;
	
	public ScspAtributos getAtributos() {
		return atributos;
	}
	
	public void setAtributos(ScspAtributos atributos) {
		this.atributos = atributos;
	}
	
	public List<ScspSolicitud> getSolicitudes() {
		return solicitudes;
	}
	
	public void setSolicitudes(List<ScspSolicitud> solicitudes) {
		this.solicitudes = solicitudes;
	}

}
