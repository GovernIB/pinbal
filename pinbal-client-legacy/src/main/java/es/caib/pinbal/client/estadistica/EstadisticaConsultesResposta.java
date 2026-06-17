/**
 * 
 */
package es.caib.pinbal.client.estadistica;

import java.util.List;

import es.caib.pinbal.client.comu.EntitatEstadistiques;

/**
 * Estructura de la resposta de l'estadística de consultes realitzades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EstadisticaConsultesResposta {

	private List<EntitatEstadistiques> entitats;

	public List<EntitatEstadistiques> getEntitats() {
		return entitats;
	}
	public void setEntitats(List<EntitatEstadistiques> entitats) {
		this.entitats = entitats;
	}
	
}
