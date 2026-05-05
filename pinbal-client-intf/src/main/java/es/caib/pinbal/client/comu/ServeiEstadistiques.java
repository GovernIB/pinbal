/**
 * 
 */
package es.caib.pinbal.client.comu;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Informació d'un servei associat a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ServeiEstadistiques {

	private String codi;
	private String nom;
	private String emisor;
	private Integer usuarisAmbPermisos;
	private Integer consultesOk;
	private Integer consultesError;
	private ConsultesOkError consultesWeb;
	private ConsultesOkError consultesRecobriment;
	private ConsultesOkError consultesTotal;
	private TotalAcumulat totalWeb;
	private TotalAcumulat totalRecobriment;
	

	@Data
	@AllArgsConstructor
	public static class ConsultesOkError {
		private Long ok;
		private Long error;
	}

}
