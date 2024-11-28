/**
 * 
 */
package es.caib.pinbal.client.informe;

import java.util.List;

import es.caib.pinbal.client.comu.EntitatEstadistiques;

/**
 * Estructura de la resposta de l'informe general d'estat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InformeGeneralEstatResposta {

	private List<EntitatEstadistiques> entitats;

	public List<EntitatEstadistiques> getEntitats() {
		return entitats;
	}
	public void setEntitats(List<EntitatEstadistiques> entitats) {
		this.entitats = entitats;
	}
	
}
