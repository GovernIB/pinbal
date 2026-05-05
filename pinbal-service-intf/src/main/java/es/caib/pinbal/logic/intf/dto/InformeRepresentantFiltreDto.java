package es.caib.pinbal.logic.intf.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Objecte DTO amb informació per filtrar l'informe d'usuaris que tenen accés a PINBAL 
 * agrupat per entitat/òrgan gestor del procediment/procediment/servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@ToString
public class InformeRepresentantFiltreDto implements Serializable{

	private Long organGestorId;
	private Long procedimentId;
	private String serveiCodi;

	private static final long serialVersionUID = -2983291959461478570L;
}
