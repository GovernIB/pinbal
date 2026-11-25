/**
 * Model del servei SOAP de recobriment (http://www.caib.es/pinbal/ws/recobriment).
 * <p>
 * Reprodueix exactament el contracte publicat per PINBAL 1.4.x, que es generava a partir dels
 * beans de SCSP 4 (sense anotacions JAXB): tipus al namespace del servei, elements locals no
 * qualificats amb el nom de la propietat en minúscula i ordre alfabètic. Les aplicacions
 * integrades tenen el client generat a partir d'aquell WSDL, per això no es poden fer servir
 * directament els beans de SCSP 5 (elements qualificats amb els namespaces SCSP i en majúscula).
 */
@XmlSchema(namespace = "http://www.caib.es/pinbal/ws/recobriment", elementFormDefault = XmlNsForm.UNQUALIFIED)
package es.caib.pinbal.logic.intf.ws.model;

import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
