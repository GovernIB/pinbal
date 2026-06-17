/**
 * 
 */
package es.caib.pinbal.client.estadistica;

import java.util.List;

import es.caib.pinbal.client.comu.EntitatEstadistiques;
import es.caib.pinbal.client.comu.TotalAcumulat;

/**
 * Estructura de la resposta de l'estadística de càrrega de peticions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EstadisticaCarregaResposta {

	private List<EntitatEstadistiques> entitats;
	private TotalAcumulat totalWeb;
	private TotalAcumulat totalRecobriment;

	public List<EntitatEstadistiques> getEntitats() {
		return entitats;
	}
	public void setEntitats(List<EntitatEstadistiques> entitats) {
		this.entitats = entitats;
	}
	public TotalAcumulat getTotalWeb() {
		return totalWeb;
	}
	public void setTotalWeb(TotalAcumulat totalWeb) {
		this.totalWeb = totalWeb;
	}
	public TotalAcumulat getTotalRecobriment() {
		return totalRecobriment;
	}
	public void setTotalRecobriment(TotalAcumulat totalRecobriment) {
		this.totalRecobriment = totalRecobriment;
	}
	
}
