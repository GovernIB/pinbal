/**
 * 
 */
package es.caib.pinbal.client.comu;

import lombok.Data;

/**
 * Informació d'una entitat associada a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class EntitatInfo {

	private String codi;
	private String nom;
	private String cif;
	private String unitatArrel;

}
