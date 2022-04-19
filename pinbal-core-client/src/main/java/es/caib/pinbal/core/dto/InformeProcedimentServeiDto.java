package es.caib.pinbal.core.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Objecte DTO amb informació de l'informe d'usuaris que tenen accés a PINBAL 
 * agrupat per entitat/òrgan gestor del procediment/procediment/servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
public class InformeProcedimentServeiDto extends AbstractIdentificable<Long> implements Serializable {

	private String entitatCodi;
	private String entitatNom;
	private String entitatCif;
	private String organGestorCodi;
	private String organGestorNom;
	private boolean organGestorActiu;
	private String procedimentCodi;
	private String procedimentNom;
	private String serveiCodi;
	private String serveiNom;
	private String usuariCodi;
	private String usuariNom;
	private String usuariNif;

	private static final long serialVersionUID = 3986823331500016935L;

}
