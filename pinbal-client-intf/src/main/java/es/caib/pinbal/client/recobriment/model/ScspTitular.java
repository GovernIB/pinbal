/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

import lombok.Data;

/**
 * Informació del titular d'una sol·licitud SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ScspTitular {

	private ScspTipoDocumentacion tipoDocumentacion;
	private String documentacion;
	private String nombreCompleto;
	private String nombre;
	private String apellido1;
	private String apellido2;
	
	public enum ScspTipoDocumentacion {
		CIF,
		CSV,
		DNI,
		NIE,
		NIF,
		Pasaporte,
		NumeroIdentificacion,
		Otros
	}

}
