/**
 * 
 */
package es.caib.pinbal.client.informe;

import java.util.List;

import es.caib.pinbal.client.comu.ServeiEstadistiques;

/**
 * Estructura de l'informe de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InformeServeisResposta {

	private List<ServeiEstadistiques> serveis;
	
	public List<ServeiEstadistiques> getServeis() {
		return serveis;
	}
	public void setServeis(List<ServeiEstadistiques> serveis) {
		this.serveis = serveis;
	}
	
}
