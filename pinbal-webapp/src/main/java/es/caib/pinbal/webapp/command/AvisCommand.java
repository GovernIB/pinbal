/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.pinbal.core.dto.AvisDto;
import es.caib.pinbal.core.dto.AvisNivellEnumDto;
import es.caib.pinbal.webapp.helper.ConversioTipusHelper;

/**
 * Command per al manteniment d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class AvisCommand {

	private Long id;
	@NotEmpty
	private String assumpte;
	@NotEmpty
	private String missatge;
	@NotNull
	private Date dataInici;
	@NotNull
	private Date dataFinal;
	private Boolean actiu;
	@NotNull
	private AvisNivellEnumDto avisNivell;
	

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAssumpte() {
		return assumpte;
	}
	public void setAssumpte(String assumpte) {
		this.assumpte = assumpte;
	}
	public String getMissatge() {
		return missatge;
	}
	public void setMissatge(String missatge) {
		this.missatge = missatge;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFinal() {
		return dataFinal;
	}
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	public Boolean getActiu() {
		return actiu;
	}
	public void setActiu(Boolean actiu) {
		this.actiu = actiu;
	}
	public AvisNivellEnumDto getAvisNivell() {
		return avisNivell;
	}
	public void setAvisNivell(AvisNivellEnumDto avisNivell) {
		this.avisNivell = avisNivell;
	}
	public static AvisCommand asCommand(AvisDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				AvisCommand.class);
	}
	public static AvisDto asDto(AvisCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				AvisDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
