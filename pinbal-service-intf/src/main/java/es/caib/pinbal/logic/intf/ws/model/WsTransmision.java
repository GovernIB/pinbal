package es.caib.pinbal.logic.intf.ws.model;

import lombok.Data;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Tipus {@code transmision} del contracte SOAP de recobriment de PINBAL 1.4.x.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transmision", propOrder = {"codigoCertificado", "fechaGeneracion", "idSolicitud", "idTransmision"})
public class WsTransmision {

	private String codigoCertificado;
	private String fechaGeneracion;
	private String idSolicitud;
	private String idTransmision;

}
