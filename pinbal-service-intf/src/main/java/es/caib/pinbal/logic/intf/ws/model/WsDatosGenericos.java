package es.caib.pinbal.logic.intf.ws.model;

import lombok.Data;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Tipus {@code datosGenericos} del contracte SOAP de recobriment de PINBAL 1.4.x.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "datosGenericos", propOrder = {"emisor", "solicitante", "titular", "transmision"})
public class WsDatosGenericos {

	private WsEmisor emisor;
	private WsSolicitante solicitante;
	private WsTitular titular;
	private WsTransmision transmision;

}
