/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

import lombok.Data;

/**
 * Informació sobre l'estat d'una petició SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ScspEstado {

	private String codigoEstado;
	private String codigoEstadoSecundario;
	private String literalError;
	private String literalErrorSec;
	private Integer tiempoEstimadoRespuesta;
	
}
