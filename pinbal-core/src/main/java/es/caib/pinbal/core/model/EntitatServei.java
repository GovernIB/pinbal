/**
 * 
 */
package es.caib.pinbal.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.pinbal.core.audit.PinbalAuditable;

/**
 * Classe de model de dades que conté la informació d'una parella entitat-servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "pbl_entitat_servei",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"entitat_id", "servei_id"})},
		indexes = {
				@Index(name = "pbl_entiserv_entitat_i", columnList = "entitat_id"),
				@Index(name = "pbl_entiserv_servei_i", columnList = "servei_id")
		})
@EntityListeners(AuditingEntityListener.class)
public class EntitatServei extends PinbalAuditable<Long> {

	private static final long serialVersionUID = -6657066865382086237L;

	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(
			name = "entitat_id",
			foreignKey = @ForeignKey(name="pbl_entitat_entiserv_fk"))
	private Entitat entitat;

	@Column(name = "servei_id", length = 64)
	private String servei;

	@Version
	private long version = 0;



	/**
	 * Obté el Builder per a crear objectes de tipus ProcedimentServei.
	 * 
	 * @param entitat
	 *            Entitat del registre.
	 * @param servei
	 *            Servei del registre.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(Entitat entitat, String servei) {
		return new Builder(entitat, servei);
	}

	public Entitat getEntitat() {
		return entitat;
	}

	public String getServei() {
		return servei;
	}

	public long getVersion() {
		return version;
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus ProcedimentServei.
	 */
	public static class Builder {
		EntitatServei built;

		/**
		 * Crea una nova instància del ProcedimentServei.
		 * 
		 * @param entitat
		 *            Entitat del registre.
		 * @param servei
		 *            Servei del registre.
		 */
		Builder(Entitat entitat, String servei) {
			built = new EntitatServei();
			built.entitat = entitat;
			built.servei = servei;
		}

		/**
		 * Construeix el nou objecte de tipus ProcedimentServei.
		 * 
		 * @return L'objecte de tipus ProcedimentServei creat.
		 */
		public EntitatServei build() {
			return built;
		}
	}

	public void configurarIdPerTest(Long id) {
		this.setId(id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((entitat == null) ? 0 : entitat.hashCode());
		result = prime * result + ((servei == null) ? 0 : servei.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntitatServei other = (EntitatServei) obj;
		if (entitat == null) {
			if (other.entitat != null)
				return false;
		} else if (!entitat.equals(other.entitat))
			return false;
		if (servei == null) {
			if (other.servei != null)
				return false;
		} else if (!servei.equals(other.servei))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
