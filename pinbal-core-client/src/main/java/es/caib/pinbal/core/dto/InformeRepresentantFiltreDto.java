package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Data;

/**
 * Objecte DTO amb informació per filtrar l'informe d'usuaris que tenen accés a PINBAL 
 * agrupat per entitat/òrgan gestor del procediment/procediment/servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class InformeRepresentantFiltreDto implements Serializable{

	private Long organGestorId;
	private Long procedimentId;
	private String serveiCodi;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = -2983291959461478570L;
}
