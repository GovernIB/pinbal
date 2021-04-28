/**
 * 
 */
package es.caib.pinbal.client.informe;

import java.util.List;

import es.caib.pinbal.client.comu.Entitat;

/**
 * Estructura de l'informe d'usuaris agrupats per entitat i departament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InformeUsuarisResposta {

	private List<Entitat> entitats;

	public List<Entitat> getEntitats() {
		return entitats;
	}
	public void setEntitats(List<Entitat> entitats) {
		this.entitats = entitats;
	}
	
}
