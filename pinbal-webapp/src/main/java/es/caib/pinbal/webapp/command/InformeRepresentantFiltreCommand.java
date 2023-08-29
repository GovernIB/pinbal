/**
 * 
 */
package es.caib.pinbal.webapp.command;

import javax.validation.constraints.Size;

import es.caib.pinbal.core.dto.InformeRepresentantFiltreDto;
import es.caib.pinbal.webapp.helper.CommandMappingHelper;
import lombok.Data;

/**
 * Command per a filtrar l'informe d'usuaris que tenen accés a PINBAL 
 * agrupat per entitat/òrgan gestor del procediment/procediment/servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class InformeRepresentantFiltreCommand {
	
	private Long organGestorId;
	private Long procedimentId;
	@Size(max=64)
	private String serveiCodi;

	public static InformeRepresentantFiltreDto asDto(InformeRepresentantFiltreCommand command) {
		if (command == null)
			return null;
		InformeRepresentantFiltreDto dto = CommandMappingHelper.getMapperFacade().map(
				command,
				InformeRepresentantFiltreDto.class);
		return dto;
	}
}
