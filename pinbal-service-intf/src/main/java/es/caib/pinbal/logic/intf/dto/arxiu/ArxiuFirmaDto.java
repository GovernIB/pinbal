/**
 * 
 */
package es.caib.pinbal.logic.intf.dto.arxiu;

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
	/**
	 * Perfil tal com l'informa l'arxiu (metadada {@code eni:perfil_firma}). Es fa servir per mostrar
	 * el perfil quan el valor no correspon a cap valor de {@link ArxiuFirmaPerfilEnumDto}.
	 */
	private String perfilDescripcio;
	/** Indica si la firma està integrada dins el contingut del document (PAdES, CAdES attached, XAdES enveloped). */
	private boolean integrada;
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

}
