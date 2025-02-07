/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

/**
 * Informació del procediment d'una sol·licitud SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspProcedimiento {

	private String codProcedimiento;
	private String nombreProcedimiento;
	
	public String getCodProcedimiento() {
		return codProcedimiento;
	}
	
	public void setCodProcedimiento(String codProcedimiento) {
		this.codProcedimiento = codProcedimiento;
	}
	
	public String getNombreProcedimiento() {
		return nombreProcedimiento;
	}
	
	public void setNombreProcedimiento(String nombreProcedimiento) {
		this.nombreProcedimiento = nombreProcedimiento;
	}

}
