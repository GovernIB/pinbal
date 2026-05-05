/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

import lombok.Data;

/**
 * Informació de la transmissió d'una sol·licitud SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ScspTransmision {

	private String codigoCertificado;
	private String idSolicitud;
	private String idTransmision;
	private String fechaGeneracion;
	
}
