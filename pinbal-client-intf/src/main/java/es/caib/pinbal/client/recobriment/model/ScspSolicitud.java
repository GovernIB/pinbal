/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

import lombok.Data;

/**
 * Informació d'una sol·licitud d'una petició SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ScspSolicitud {

	private String id;
	private ScspDatosGenericos datosGenericos;
	private String datosEspecificos;
	
}
