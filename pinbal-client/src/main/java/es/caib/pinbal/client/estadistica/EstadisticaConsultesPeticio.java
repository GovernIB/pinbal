/**
 * 
 */
package es.caib.pinbal.client.estadistica;

import java.time.LocalDate;

/**
 * Estructura de la petició de l'estadística de consultes realitzades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EstadisticaConsultesPeticio {

	private String entitatCodi;
	private String procedimentCodi;
	private String serveiCodi;
	private String estatCodi;
	private String agrupacioTipus;
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
	public String getEstatCodi() {
		return estatCodi;
	}
	public void setEstatCodi(String estatCodi) {
		this.estatCodi = estatCodi;
	}
	public String getAgrupacioTipus() {
		return agrupacioTipus;
	}
	public void setAgrupacioTipus(String agrupacioTipus) {
		this.agrupacioTipus = agrupacioTipus;
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
