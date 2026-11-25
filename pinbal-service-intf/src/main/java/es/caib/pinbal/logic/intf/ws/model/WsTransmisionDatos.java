package es.caib.pinbal.logic.intf.ws.model;

import lombok.Data;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Tipus {@code transmisionDatos} del contracte SOAP de recobriment de PINBAL 1.4.x.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transmisionDatos", propOrder = {"datosEspecificos", "datosGenericos", "id"})
public class WsTransmisionDatos {

	/** xs:anyType: es rep/envia com a element DOM ({@code org.w3c.dom.Element}). */
	private Object datosEspecificos;
	private WsDatosGenericos datosGenericos;
	private String id;

}
