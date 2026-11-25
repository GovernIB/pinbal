package es.caib.pinbal.logic.intf.ws.model;

import lombok.Data;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Tipus {@code solicitudes} del contracte SOAP de recobriment de PINBAL 1.4.x.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "solicitudes", propOrder = {"solicitudTransmision"})
public class WsSolicitudes {

	@XmlElement(nillable = true)
	private List<WsSolicitudTransmision> solicitudTransmision = new ArrayList<>();

}
