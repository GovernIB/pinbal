/**
 * 
 */
package es.caib.pinbal.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.pinbal.core.audit.PinbalAuditable;
import es.caib.pinbal.core.dto.AvisNivellEnumDto;

/**
 * Classe del model de dades que representa una avis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "pbl_avis")
@EntityListeners(AuditingEntityListener.class)
public class Avis extends PinbalAuditable<Long> {
	
	@Column(name = "assumpte", length = 256, nullable = false)
	private String assumpte;
	@Column(name = "missatge", length = 2048, nullable = false)
	private String missatge;
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inici", nullable = false)
	private Date dataInici;
	@Temporal(TemporalType.DATE)
	@Column(name = "data_final", nullable = false)
	private Date dataFinal;
	@Column(name = "actiu", nullable = false)
	private Boolean actiu;
	@Column(name = "avis_nivell", length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	private AvisNivellEnumDto avisNivell;
	
	
	public void update(
			String assumpte,
			String missatge,
			Date dataInici,
			Date dataFinal,
			AvisNivellEnumDto avisNivell) {
		this.assumpte = assumpte;
		this.missatge = missatge;
		this.dataInici = dataInici;
		this.dataFinal = dataFinal;
		this.avisNivell = avisNivell;
	}
	
	public void updateActiva(
			Boolean actiu) {
		this.actiu = actiu;
	}
	

	public static Builder getBuilder(
			String assumpte,
			String missatge,
			Date dataInici,
			Date dataFinal,
			AvisNivellEnumDto avisNivell) {
		return new Builder(
				assumpte,
				missatge,
				dataInici,
				dataFinal,
				avisNivell);
	}


	public static class Builder {
		Avis built;
		Builder(
				String assumpte,
				String missatge,
				Date dataInici,
				Date dataFinal,
				AvisNivellEnumDto avisNivell) {
			built = new Avis();
			built.assumpte = assumpte;
			built.missatge = missatge;
			built.dataInici = dataInici;
			built.dataFinal = dataFinal;
			built.actiu = true;
			built.avisNivell = avisNivell;
		}
		public Avis build() {
			return built;
		}
	}
	

	public String getAssumpte() {
		return assumpte;
	}

	public String getMissatge() {
		return missatge;
	}

	public Date getDataInici() {
		return dataInici;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public Boolean getActiu() {
		return actiu;
	}

	public AvisNivellEnumDto getAvisNivell() {
		return avisNivell;
	}

	private static final long serialVersionUID = -2299453443943600172L;
	
}
