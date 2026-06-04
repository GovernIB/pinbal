/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.pinbal.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.logic.intf.dto.IntegracioFiltreDto;
import es.caib.pinbal.back.helper.ConversioTipusHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Command per a filtrar les estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegracioFiltreCommand {
	
	private String codi;
	private Date data;
	private String descripcio;	
	private String idPeticio;	
	private IntegracioAccioTipusEnumDto tipus;
	private IntegracioAccioEstatEnumDto estat;
	private EntitatDto entitat;	
	
	public static IntegracioFiltreCommand asCommand(IntegracioFiltreDto dto) {
		IntegracioFiltreCommand command = ConversioTipusHelper.convertir(
				dto, 
				IntegracioFiltreCommand.class);
		return command;
	}
	
	public static IntegracioFiltreDto asDto(IntegracioFiltreCommand command) {
		IntegracioFiltreDto dto = ConversioTipusHelper.convertir(
				command, 
				IntegracioFiltreDto.class);
		return dto;
	}

}
