/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.InformeRepresentantFiltreDto;
import es.caib.pinbal.back.helper.CommandMappingHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * Command per a filtrar l'informe d'usuaris que tenen accés a PINBAL 
 * agrupat per entitat/òrgan gestor del procediment/procediment/servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
