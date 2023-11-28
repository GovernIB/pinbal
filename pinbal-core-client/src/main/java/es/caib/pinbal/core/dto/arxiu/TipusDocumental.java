/**
 * 
 */
package es.caib.pinbal.core.dto.arxiu;

import es.caib.pinbal.core.dto.EntitatDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter @Setter
public class TipusDocumental implements Serializable {
	
	private Long id;
	private String codi;
	private String codiEspecific;
	private String nomEspanyol;
	private String nomCatala;
	private String nom;
	private EntitatDto entitat;
	
	public String getCodiNom() {
		return codi + " - " + nom;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
