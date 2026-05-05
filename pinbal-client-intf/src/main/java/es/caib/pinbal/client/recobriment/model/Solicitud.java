package es.caib.pinbal.client.recobriment.model;

import lombok.Data;

/**
 * Paràmetres resumits per a una sol·licitud SCSP genèrica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class Solicitud extends SolicitudBase {

	private String datosEspecificos;

}
