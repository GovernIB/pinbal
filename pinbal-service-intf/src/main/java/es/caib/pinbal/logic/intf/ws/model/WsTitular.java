package es.caib.pinbal.logic.intf.ws.model;

import lombok.Data;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Tipus {@code titular} del contracte SOAP de recobriment de PINBAL 1.4.x.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "titular", propOrder = {"apellido1", "apellido2", "documentacion", "nombre", "nombreCompleto", "tipoDocumentacion"})
public class WsTitular {

	private String apellido1;
	private String apellido2;
	private String documentacion;
	private String nombre;
	private String nombreCompleto;
	private WsTipoDocumentacion tipoDocumentacion;

}
