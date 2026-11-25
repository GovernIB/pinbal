package es.caib.pinbal.logic.intf.ws.model;

import lombok.Data;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Tipus {@code transmisiones} del contracte SOAP de recobriment de PINBAL 1.4.x.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transmisiones", propOrder = {"transmisionDatos"})
public class WsTransmisiones {

	@XmlElement(nillable = true)
	private List<WsTransmisionDatos> transmisionDatos = new ArrayList<>();

}
