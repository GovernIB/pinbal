/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

import lombok.Data;

/**
 * Informació dels atributs d'una petició SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ScspAtributos {

	private String idPeticion;
	private String numElementos;
	private String timeStamp;
	private String codigoCertificado;
	private ScspEstado estado;
	
}
