/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.logic.intf.dto.EmissorCertDto;
import es.caib.pinbal.back.helper.CommandMappingHelper;
import es.caib.pinbal.back.validation.CifEmisorNoRepetit;
import es.caib.pinbal.back.validation.DocumentIdentitat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Command per al manteniment d'emisors certificats
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CifEmisorNoRepetit
public class EmissorCertCommand {

	private Long id;
	
	@NotEmpty
    @Size(max=50)
	private String nom;
	@NotEmpty @Size(max=16)
	@DocumentIdentitat(documentTipus= DocumentTipus.CIF)
	private String cif;
	
	private Date dataBaixa;
	
	
	public static EmissorCertCommand asCommand(EmissorCertDto dto) {
		return CommandMappingHelper.getMapperFacade().map(
				dto,
				EmissorCertCommand.class);
	}
	
	public static EmissorCertDto asDto(EmissorCertCommand command) {
		return CommandMappingHelper.getMapperFacade().map(
				command,
				EmissorCertDto.class);
	}
	
}
