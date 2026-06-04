/**
 * 
 */
package es.caib.pinbal.back.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Command per a generar les auditories aleatòries.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaGenerarCommand {

	@NotNull
	private Date dataInici;
	@NotNull
	private Date dataFi;
	@NotNull
	private Integer numConsultes;
	@NotNull
	private Integer numEntitats;

}
