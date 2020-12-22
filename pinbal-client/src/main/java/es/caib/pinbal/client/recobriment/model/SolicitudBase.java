/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

import es.caib.pinbal.client.recobriment.model.ScspSolicitante.ScspConsentimiento;

/**
 * Paràmetres resumits per a una sol·licitud SCSP genrèrica base.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class SolicitudBase {

	private String identificadorSolicitante;
	private String nombreSolicitante;
	private String codigoProcedimiento;
	private String unidadTramitadora;
	private String codigoUnidadTramitadora;
	private String finalidad;
	private ScspConsentimiento consentimiento;
	private String idExpediente;
	private ScspFuncionario funcionario;
	private ScspTitular titular;

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
	public String getCodigoProcedimiento() {
		return codigoProcedimiento;
	}
	public void setCodigoProcedimiento(String codigoProcedimiento) {
		this.codigoProcedimiento = codigoProcedimiento;
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
	public String getIdExpediente() {
		return idExpediente;
	}
	public void setIdExpediente(String idExpediente) {
		this.idExpediente = idExpediente;
	}
	public ScspFuncionario getFuncionario() {
		return funcionario;
	}
	public void setFuncionario(ScspFuncionario funcionario) {
		this.funcionario = funcionario;
	}
	public ScspTitular getTitular() {
		return titular;
	}
	public void setTitular(ScspTitular titular) {
		this.titular = titular;
	}

	public abstract String getDatosEspecificos();

}
