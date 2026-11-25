package es.caib.pinbal.logic.intf.ws.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Tipus {@code tipoDocumentacion} del contracte SOAP de recobriment de PINBAL 1.4.x. Els noms de les
 * constants són els valors XML.
 */
@XmlType(name = "tipoDocumentacion")
@XmlEnum
public enum WsTipoDocumentacion {
	CIF,
	NIF,
	DNI,
	Pasaporte,
	NIE,
	Otros,
	NumeroIdentificacion,
	CSV
}
