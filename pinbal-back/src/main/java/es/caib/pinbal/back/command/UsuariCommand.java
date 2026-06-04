/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.NumElementsPaginaEnum;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.back.helper.ConversioTipusHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.io.Serializable;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuariCommand implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String email;
	private String[] rols;
	private String idioma;
	private Long procedimentId;
	private String serveiCodi;
	private Long entitatId;
	@Size(max = 250)
	private String departament;
	@Size(max = 250)
	private String finalitat;
	private boolean hasMultiplesEntitats;
	private NumElementsPaginaEnum numElementsPagina;


	public static UsuariCommand asCommand(UsuariDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				UsuariCommand.class);
	}
	public static UsuariDto asDto(UsuariCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				UsuariDto.class);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
