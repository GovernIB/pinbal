/**
 * 
 */
package es.caib.pinbal.client.estadistica;

import java.util.List;

import es.caib.pinbal.client.comu.Entitat;

/**
 * Estructura de la resposta de l'estadística de consultes realitzades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EstadisticaConsultesResposta {

	private List<Entitat> entitats;

	public List<Entitat> getEntitats() {
		return entitats;
	}
	public void setEntitats(List<Entitat> entitats) {
		this.entitats = entitats;
	}
	
}
