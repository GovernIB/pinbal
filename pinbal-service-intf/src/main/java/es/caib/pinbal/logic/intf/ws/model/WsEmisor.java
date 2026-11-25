package es.caib.pinbal.logic.intf.ws.model;

import lombok.Data;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Tipus {@code emisor} del contracte SOAP de recobriment de PINBAL 1.4.x.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "emisor", propOrder = {"nifEmisor", "nombreEmisor"})
public class WsEmisor {

	private String nifEmisor;
	private String nombreEmisor;

}
