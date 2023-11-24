/**
 * 
 */
package es.caib.pinbal.core.dto;

import es.caib.pinbal.core.dto.ConsultaDto.JustificantEstat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Informació d'un fitxer.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JustificantDto extends FitxerDto {

	private JustificantEstat estat;
	private boolean error;
	private String errorDescripcio;

	private String arxiuUuid;
	private String arxiuCsv;

}
