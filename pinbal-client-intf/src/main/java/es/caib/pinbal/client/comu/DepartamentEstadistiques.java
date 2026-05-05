/**
 * 
 */
package es.caib.pinbal.client.comu;

import lombok.Data;

import java.util.List;

/**
 * Informació d'un departament associat a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class DepartamentEstadistiques {

	private String codi;
	private String nom;
	private List<ProcedimentEstadistiques> procediments;
	private List<Usuari> usuaris;
	private TotalAcumulat totalWeb;
	private TotalAcumulat totalRecobriment;
	
}
