/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

import lombok.Data;

import java.util.List;

/**
 * Informació d'una petició SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ScspPeticion {

	private ScspAtributos atributos;
	private List<ScspSolicitud> solicitudes;
	
}
