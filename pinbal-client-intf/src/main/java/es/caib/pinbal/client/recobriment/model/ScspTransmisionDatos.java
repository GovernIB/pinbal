/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

import lombok.Data;

/**
 * Informació d'una transmissió d'una resposta SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ScspTransmisionDatos {

	private String id;
	private ScspDatosGenericos datosGenericos;
	private String datosEspecificos;
	
}
