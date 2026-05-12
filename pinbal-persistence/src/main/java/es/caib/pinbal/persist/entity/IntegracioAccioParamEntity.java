/**
 * 
 */
package es.caib.pinbal.persist.entity;

import es.caib.pinbal.persist.audit.PinbalAuditable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * Classe del model de dades que representa un IntegracioAccioParam.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "pbl_mon_int_param")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class IntegracioAccioParamEntity extends PinbalAuditable<Long> {

	@ManyToOne(optional = false)
	@JoinColumn(
			name = "mon_int_id",
			foreignKey = @ForeignKey(name = "pbl_mon_int_param_monint_fk"))
	private IntegracioAccioEntity integracioAccio;
	
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	
	@Column(name = "descripcio", length = 1024)
	private String descripcio;

	public IntegracioAccioEntity getIntegracioAccio() {
		return integracioAccio;
	}

	public void setIntegracioAccio(IntegracioAccioEntity integracioAccio) {
		this.integracioAccio = integracioAccio;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public static Builder getBuilder(
			IntegracioAccioEntity integracioAccio,
			String nom,
			String descripcio) {
		return new Builder(
				integracioAccio, 
				nom, 
				descripcio);
	}
	public static class Builder {
		IntegracioAccioParamEntity built;
		Builder(IntegracioAccioEntity integracioAccio,
				String nom,
				String descripcio) {
			built = new IntegracioAccioParamEntity();
			built.integracioAccio = integracioAccio;
			built.nom = StringUtils.abbreviate(nom, 256);
			built.descripcio = StringUtils.abbreviate(descripcio, 1024);
		}
		public IntegracioAccioParamEntity build() {
			return built;
		}
	}

}
