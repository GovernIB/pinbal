/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'una avis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AvisDto extends AbstractIdentificable<Long> implements Serializable {

	private Long id;
	private String assumpte;
	private String missatge;
	private Date dataInici;
	private Date dataFinal;
	private Boolean actiu;
	private AvisNivellEnumDto avisNivell;

	public boolean isInfo() {
		return avisNivell == AvisNivellEnumDto.INFO;
	}
	public boolean isWarning() {
		return avisNivell == AvisNivellEnumDto.WARNING;
	}
	public boolean isError() {
		return avisNivell == AvisNivellEnumDto.ERROR;
	}

	private static final long serialVersionUID = 1L;
}
