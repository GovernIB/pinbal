package es.caib.pinbal.logic.intf.ws.model;

import lombok.Data;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Tipus {@code procedimiento} del contracte SOAP de recobriment de PINBAL 1.4.x.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "procedimiento", propOrder = {"codProcedimiento", "nombreProcedimiento"})
public class WsProcedimiento {

	private String codProcedimiento;
	private String nombreProcedimiento;

}
