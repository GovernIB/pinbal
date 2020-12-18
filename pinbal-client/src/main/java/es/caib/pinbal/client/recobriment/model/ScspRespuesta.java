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
public class ScspRespuesta {

	private ScspAtributos atributos;
	private List<ScspTransmisionDatos> transmisiones;

	public ScspAtributos getAtributos() {
		return atributos;
	}
	
	public void setAtributos(ScspAtributos atributos) {
		this.atributos = atributos;
	}
	
	public List<ScspTransmisionDatos> getTransmisiones() {
		return transmisiones;
	}
	
	public void setTransmisiones(List<ScspTransmisionDatos> transmisiones) {
		this.transmisiones = transmisiones;
	}

}
