/**
 * 
 */
package es.caib.pinbal.client.informe;

import java.util.List;

import es.caib.pinbal.client.comu.Servei;

/**
 * Estructura de l'informe de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InformeServeisResposta {

	private List<Servei> serveis;
	
	public List<Servei> getServeis() {
		return serveis;
	}
	public void setServeis(List<Servei> serveis) {
		this.serveis = serveis;
	}
	
}
