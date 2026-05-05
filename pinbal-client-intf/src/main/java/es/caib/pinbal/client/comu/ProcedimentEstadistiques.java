/**
 * 
 */
package es.caib.pinbal.client.comu;

import es.caib.pinbal.client.comu.ServeiEstadistiques.ConsultesOkError;
import lombok.Data;

import java.util.List;

/**
 * Informació d'un procediment associat a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ProcedimentEstadistiques {

	private String codi;
	private String nom;
	private boolean actiu;
	private ConsultesOkError consultesWeb;
	private ConsultesOkError consultesRecobriment;
	private ConsultesOkError consultesTotal;
	private TotalAcumulat totalWeb;
	private TotalAcumulat totalRecobriment;
	private List<ServeiEstadistiques> serveis;
	
}
