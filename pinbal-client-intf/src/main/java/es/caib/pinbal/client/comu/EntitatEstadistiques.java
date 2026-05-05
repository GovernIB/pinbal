/**
 * 
 */
package es.caib.pinbal.client.comu;

import lombok.Data;

import java.util.List;

/**
 * Informació d'una entitat associada a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class EntitatEstadistiques {

	private String codi;
	private String nom;
	private String nif;
	private List<DepartamentEstadistiques> departaments;
	private TotalAcumulat totalWeb;
	private TotalAcumulat totalRecobriment;
	
}
