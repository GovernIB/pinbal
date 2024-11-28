/**
 * 
 */
package es.caib.pinbal.client.informe;

import java.util.List;

import es.caib.pinbal.client.comu.EntitatEstadistiques;

/**
 * Estructura de l'informe de procediments agrupats per entitat i departament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InformeProcediments {

	private List<EntitatEstadistiques> entitats;

	public List<EntitatEstadistiques> getEntitats() {
		return entitats;
	}
	public void setEntitats(List<EntitatEstadistiques> entitats) {
		this.entitats = entitats;
	}
	
}
