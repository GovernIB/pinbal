package es.caib.pinbal.client.recobriment.model;

import es.caib.pinbal.client.recobriment.model.ScspSolicitante.ScspConsentimiento;

import java.text.SimpleDateFormat;
import java.util.Date;

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

	protected String xmlOptionalStringParameter(String value, String nombre) {
		if (value != null && !value.isEmpty()) {
			String xmlString = "";
			xmlString += "<" + nombre + ">";
			xmlString += value;
			xmlString += "</" + nombre + ">";
			return xmlString;
		} else {
			return "";
		}
	}

	protected String xmlOptionalDateParameter(Date value, String nombre) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (value != null) {
			String xmlString = "";
			xmlString += "<" + nombre + ">";
			xmlString += dateFormat.format(value);
			xmlString += "</" + nombre + ">";
			return xmlString;
		} else {
			return "";
		}
	}

	protected String xmlOptionalBooleanParameter(Boolean value, String nombre) {
		if (value != null) {
			String xmlString = "";
			xmlString += "<" + nombre + ">";
			xmlString += value ? "true" : "false";
			xmlString += "</" + nombre + ">";
			return xmlString;
		} else {
			return "";
		}
	}

	protected void validFormat(String camp, String value, String format) {
		if (value != null && !value.isEmpty()) {
			if (!value.matches(format)) {
				throw new CampFormatException(camp, format);
			}
		}
	}

	protected void validLength(String camp, String value, int minLength, int maxLength) {
		if (value != null && !value.isEmpty()) {
			if (value.length() < minLength) {
				throw new CampLengthException(camp, minLength, CampLengthException.Tipus.MIN);
			}
			if (value.length() > maxLength) {
				throw new CampLengthException(camp, maxLength, CampLengthException.Tipus.MAX);
			}
		}
	}

	public static boolean isEmptyString(String s) {
		return s == null || s.isEmpty();
	}
}
