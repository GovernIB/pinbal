/**
 * 
 */
package es.caib.pinbal.client.informe;

import java.time.LocalDate;

/**
 * Estructura de la petició de l'informe general d'estat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InformeGeneralEstatPeticio {

	private LocalDate dataInici;
	private LocalDate dataFi;

	public LocalDate getDataInici() {
		return dataInici;
	}
	public void setDataInici(LocalDate dataInici) {
		this.dataInici = dataInici;
	}
	public LocalDate getDataFi() {
		return dataFi;
	}
	public void setDataFi(LocalDate dataFi) {
		this.dataFi = dataFi;
	}
	
}
