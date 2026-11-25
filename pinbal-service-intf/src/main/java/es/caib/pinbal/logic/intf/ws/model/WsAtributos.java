package es.caib.pinbal.logic.intf.ws.model;

import lombok.Data;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Tipus {@code atributos} del contracte SOAP de recobriment de PINBAL 1.4.x.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "atributos", propOrder = {"codigoCertificado", "estado", "idPeticion", "numElementos", "solicitante", "timeStamp"})
public class WsAtributos {

	private String codigoCertificado;
	private WsEstado estado;
	private String idPeticion;
	private String numElementos;
	private WsSolicitante solicitante;
	private String timeStamp;

}
