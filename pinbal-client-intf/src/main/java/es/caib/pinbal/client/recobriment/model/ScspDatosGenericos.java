/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

import lombok.Data;

/**
 * Informació genèrica d'una sol·licitud SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ScspDatosGenericos {

	private ScspEmisor emisor;
	private ScspSolicitante solicitante;
	private ScspTitular titular;
	private ScspTransmision transmision;
	
}
