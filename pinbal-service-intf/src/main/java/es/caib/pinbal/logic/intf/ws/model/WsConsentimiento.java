package es.caib.pinbal.logic.intf.ws.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Tipus {@code consentimiento} del contracte SOAP de recobriment de PINBAL 1.4.x. Els noms de les
 * constants són els valors XML.
 */
@XmlType(name = "consentimiento")
@XmlEnum
public enum WsConsentimiento {
	Si,
	Ley
}
