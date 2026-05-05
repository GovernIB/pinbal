/**
 * 
 */
package es.caib.pinbal.client.dadesobertes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Estructura de la petició d'informació per a les dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect
public class DadesObertesPeticio {

	private String entitatCodi;
	private String procedimentCodi;
	private String serveiCodi;
	private Date dataInici;
	private Date dataFi;
	private boolean historic = false;

}
