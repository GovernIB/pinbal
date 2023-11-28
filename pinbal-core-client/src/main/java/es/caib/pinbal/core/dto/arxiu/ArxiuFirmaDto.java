/**
 * 
 */
package es.caib.pinbal.core.dto.arxiu;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Informació de firma provinent de l'arxiu i detalls provinents
 * del plugin de validació de firma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ArxiuFirmaDto {

	private ArxiuFirmaTipusEnumDto tipus;
	private ArxiuFirmaPerfilEnumDto perfil;
	private String fitxerNom;
	private byte[] contingut;
	private String tipusMime;
	private String csvRegulacio;
	private boolean autofirma;
	private List<ArxiuFirmaDetallDto> detalls;

	public String getContingutComString() {
		if (contingut != null) {
			return new String(contingut);
		}
		return null;
	}
	
	
//	public FitxerDto getFitxer() {
//		return new FitxerDto(
//				fitxerNom,
//				tipusMime,
//				contingut);
//	}

}
