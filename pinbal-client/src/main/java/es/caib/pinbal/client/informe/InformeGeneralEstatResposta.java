/**
 * 
 */
package es.caib.pinbal.client.informe;

import java.util.List;

import es.caib.pinbal.client.comu.Entitat;

/**
 * Estructura de la resposta de l'informe general d'estat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InformeGeneralEstatResposta {

	private List<Entitat> entitats;

	public List<Entitat> getEntitats() {
		return entitats;
	}
	public void setEntitats(List<Entitat> entitats) {
		this.entitats = entitats;
	}
	
}
