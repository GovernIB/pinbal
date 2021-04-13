/**
 * 
 */
package es.caib.pinbal.client.dadesobertes;

import java.time.LocalDate;

/**
 * Estructura de la petició d'informació per a les dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesObertesPeticio {

	private String entitatCodi;
	private String procedimentCodi;
	private String serveiCodi;
	private LocalDate dataInici;
	private LocalDate dataFi;

	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public String getServeiCodi() {
		return serveiCodi;
	}
	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
	}
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
