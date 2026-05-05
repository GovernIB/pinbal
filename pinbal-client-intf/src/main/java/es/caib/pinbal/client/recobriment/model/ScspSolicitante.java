/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

import lombok.Data;

/**
 * Informació del sol·licitant d'una sol·licitud SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
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
	
	public enum ScspConsentimiento {
		Si,
		Ley
	}

}
