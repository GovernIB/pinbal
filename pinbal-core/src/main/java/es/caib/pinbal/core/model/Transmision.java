/**
 * 
 */
package es.caib.pinbal.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Classe de model de dades que conté la informació d'una transmissió SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "core_transmision")
public class Transmision implements Serializable {

	@Id
	@Column(length = 64)
	private String idSolicitud;

	@Id
	private String idPeticion;

	@Column(length = 64)
	private String idTransmision;

	@Column(length = 10, nullable = false)
	private String idSolicitante;

	@Column(length = 256)
	private String nombreSolicitante;

	@Column(length = 30)
	private String docTitular;

	@Column(length = 40)
	private String nombreTitular;

	@Column(length = 40)
	private String apellido1Titular;

	@Column(length = 40)
	private String apellido2Titular;

	@Column(length = 122)
	private String nombreCompletoTitular;

	@Column(length = 16)
	private String docFuncionario;

	@Column(length = 128)
	private String nombreFuncionario;

	@Column(length = 32)
	private String seudonimoFuncionario;

	private Date fechaGeneracion;

	@Column(length = 250)
	private String unidadTramitadora;

	@Column(length = 9)
	private String codigoUnidadTramitadora;

	@Column(length = 256)
	private String codigoProcedimiento;

	@Column(length = 256)
	private String nombreProcedimiento;

	@Column(length = 65)
	private String expediente;

	@Column(length = 256)
	private String finalidad;

	@Column(length = 3)
	private String consentimiento;

	@Column(length = 4000)
	private String error;

	@Column(length = 10)
	private String estado;

	@Column(length = 16)
	private String estadoSecundario;

	
	public String getIdSolicitud() {
		return idSolicitud;
	}

	
	public String getIdPeticion() {
		return idPeticion;
	}

	
	public String getIdTransmision() {
		return idTransmision;
	}

	
	public String getIdSolicitante() {
		return idSolicitante;
	}

	
	public String getNombreSolicitante() {
		return nombreSolicitante;
	}

	
	public String getDocTitular() {
		return docTitular;
	}

	
	public String getNombreTitular() {
		return nombreTitular;
	}

	
	public String getApellido1Titular() {
		return apellido1Titular;
	}

	
	public String getApellido2Titular() {
		return apellido2Titular;
	}

	
	public String getNombreCompletoTitular() {
		return nombreCompletoTitular;
	}

	
	public String getDocFuncionario() {
		return docFuncionario;
	}

	
	public String getNombreFuncionario() {
		return nombreFuncionario;
	}

	
	public String getSeudonimoFuncionario() {
		return seudonimoFuncionario;
	}

	
	public Date getFechaGeneracion() {
		return fechaGeneracion;
	}

	
	public String getUnidadTramitadora() {
		return unidadTramitadora;
	}

	
	public String getCodigoUnidadTramitadora() {
		return codigoUnidadTramitadora;
	}

	
	public String getCodigoProcedimiento() {
		return codigoProcedimiento;
	}

	
	public String getNombreProcedimiento() {
		return nombreProcedimiento;
	}

	
	public String getExpediente() {
		return expediente;
	}

	
	public String getFinalidad() {
		return finalidad;
	}

	
	public String getConsentimiento() {
		return consentimiento;
	}

	
	public String getError() {
		return error;
	}

	
	public String getEstado() {
		return estado;
	}

	
	public String getEstadoSecundario() {
		return estadoSecundario;
	}

	private static final long serialVersionUID = -7232616919018376158L;

}
