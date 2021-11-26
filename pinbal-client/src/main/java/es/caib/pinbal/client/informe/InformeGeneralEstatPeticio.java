/**
 * 
 */
package es.caib.pinbal.client.informe;

import java.util.Date;

/**
 * Estructura de la petició de l'informe general d'estat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InformeGeneralEstatPeticio {

	private Date dataInici;
	private Date dataFi;

	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	
}