package es.caib.pinbal.logic.intf.ws.model;

import lombok.Data;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Tipus {@code funcionario} del contracte SOAP de recobriment de PINBAL 1.4.x.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "funcionario", propOrder = {"nifFuncionario", "nombreCompletoFuncionario", "seudonimo"})
public class WsFuncionario {

	private String nifFuncionario;
	private String nombreCompletoFuncionario;
	private String seudonimo;

}
