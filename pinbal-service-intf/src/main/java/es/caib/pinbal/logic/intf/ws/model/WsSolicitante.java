package es.caib.pinbal.logic.intf.ws.model;

import lombok.Data;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Tipus {@code solicitante} del contracte SOAP de recobriment de PINBAL 1.4.x.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "solicitante", propOrder = {"codigoUnidadTramitadora", "consentimiento", "finalidad", "funcionario", "idExpediente", "identificadorSolicitante", "nombreSolicitante", "procedimiento", "unidadTramitadora"})
public class WsSolicitante {

	private String codigoUnidadTramitadora;
	private WsConsentimiento consentimiento;
	private String finalidad;
	private WsFuncionario funcionario;
	private String idExpediente;
	private String identificadorSolicitante;
	private String nombreSolicitante;
	private WsProcedimiento procedimiento;
	private String unidadTramitadora;

}
