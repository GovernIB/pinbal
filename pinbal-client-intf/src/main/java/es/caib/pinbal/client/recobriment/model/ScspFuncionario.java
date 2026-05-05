/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

import lombok.Data;

/**
 * Informació del funcionari d'una sol·licitud SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ScspFuncionario {

	private String nombreCompletoFuncionario;
	private String nifFuncionario;
	private String seudonimo;
	
}
