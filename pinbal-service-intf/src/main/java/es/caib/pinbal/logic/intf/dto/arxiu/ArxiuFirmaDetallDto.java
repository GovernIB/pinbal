/**
 * 
 */
package es.caib.pinbal.logic.intf.dto.arxiu;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Detalls de firma provinents del plugin de validació de firma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ArxiuFirmaDetallDto {

	private Date data;
	private String responsableNif;
	private String responsableNom;
	private String emissorCertificat;

}
