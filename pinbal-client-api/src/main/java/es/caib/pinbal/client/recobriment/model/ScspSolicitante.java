/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

/**
 * Informació del sol·licitant d'una sol·licitud SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspSolicitante {

	private ScspProcedimiento procedimiento;
	private ScspFuncionario funcionario;
	private String unidadTramitadora;
	private String codigoUnidadTramitadora;
	private String identificadorSolicitante;
	private String nombreSolicitante;
	private String idExpediente;
	private String finalidad;
	private ScspConsentimiento consentimiento;
	
	public ScspProcedimiento getProcedimiento() {
		return procedimiento;
	}
	
	public void setProcedimiento(ScspProcedimiento procedimiento) {
		this.procedimiento = procedimiento;
	}
	
	public ScspFuncionario getFuncionario() {
		return funcionario;
	}
	
	public void setFuncionario(ScspFuncionario funcionario) {
		this.funcionario = funcionario;
	}
	
	public String getUnidadTramitadora() {
		return unidadTramitadora;
	}
	
	public void setUnidadTramitadora(String unidadTramitadora) {
		this.unidadTramitadora = unidadTramitadora;
	}
	
	public String getCodigoUnidadTramitadora() {
		return codigoUnidadTramitadora;
	}
	
	public void setCodigoUnidadTramitadora(String codigoUnidadTramitadora) {
		this.codigoUnidadTramitadora = codigoUnidadTramitadora;
	}
	
	public String getIdentificadorSolicitante() {
		return identificadorSolicitante;
	}
	
	public void setIdentificadorSolicitante(String identificadorSolicitante) {
		this.identificadorSolicitante = identificadorSolicitante;
	}
	
	public String getNombreSolicitante() {
		return nombreSolicitante;
	}
	
	public void setNombreSolicitante(String nombreSolicitante) {
		this.nombreSolicitante = nombreSolicitante;
	}
	
	public String getIdExpediente() {
		return idExpediente;
	}
	
	public void setIdExpediente(String idExpediente) {
		this.idExpediente = idExpediente;
	}
	
	public String getFinalidad() {
		return finalidad;
	}
	
	public void setFinalidad(String finalidad) {
		this.finalidad = finalidad;
	}
	
	public ScspConsentimiento getConsentimiento() {
		return consentimiento;
	}
	
	public void setConsentimiento(ScspConsentimiento consentimiento) {
		this.consentimiento = consentimiento;
	}

	public enum ScspConsentimiento {
		Si,
		Ley
	}

}
