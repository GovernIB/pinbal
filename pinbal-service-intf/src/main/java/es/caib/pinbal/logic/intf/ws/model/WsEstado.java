package es.caib.pinbal.logic.intf.ws.model;

import lombok.Data;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Tipus {@code estado} del contracte SOAP de recobriment de PINBAL 1.4.x.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "estado", propOrder = {"codigoEstado", "codigoEstadoSecundario", "literalError", "literalErrorSec", "tiempoEstimadoRespuesta"})
public class WsEstado {

	private String codigoEstado;
	private String codigoEstadoSecundario;
	private String literalError;
	private String literalErrorSec;
	private Integer tiempoEstimadoRespuesta;

}
