/**
 * 
 */
package es.caib.pinbal.core.model;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
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
 * Classe de model de dades que conté la informació d'una parella procediment-servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "pbl_procediment_servei",
		uniqueConstraints = {
				@UniqueConstraint(columnNames={"procediment_id", "servei_id"})},
		indexes = {
				@Index(name = "pbl_procserv_proced_i", columnList = "procediment_id"),
				@Index(name = "pbl_procserv_servei_i", columnList = "servei_id")})
@EntityListeners(AuditingEntityListener.class)
public class ProcedimentServei extends PinbalAuditable<Long> {

	private static final long serialVersionUID = -6657066865382086237L;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "procediment_id",
			foreignKey = @ForeignKey(name = "pbl_proced_procserv_fk"))
	private Procediment procediment;

	@Column(name = "servei_id", length = 64)
	private String servei;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "servei_id",
			referencedColumnName = "CODCERTIFICADO",
			insertable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Servei serveiScsp;

	@Column(name = "actiu")
	private boolean actiu;
	
	@Column(name = "PROCEDIMENT_CODI")
	private String procedimentCodi;

	@Version
	private long version = 0;



	/**
	 * Obté el Builder per a crear objectes de tipus ProcedimentServei.
	 * 
	 * @param procediment
	 *            Procediment del registre.
	 * @param servei
	 *            Servei del registre.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(Procediment procediment, String servei) {
		return new Builder(procediment, servei);
	}

	public Procediment getProcediment() {
		return procediment;
	}

	public String getServei() {
		return servei;
	}

	public boolean isActiu() {
		return actiu;
	}
	
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	
	public void updateProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}

	public long getVersion() {
		return version;
	}

	public void updateActiu(boolean actiu) {
		this.actiu = actiu;
	}
	
	public Servei getServeiScsp() {
		return serveiScsp;
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus ProcedimentServei.
	 */
	public static class Builder {
		ProcedimentServei built;

		/**
		 * Crea una nova instància del ProcedimentServei.
		 * 
		 * @param procediment
		 *            Procediment del registre.
		 * @param servei
		 *            Servei del registre.
		 */
		Builder(Procediment procediment, String servei) {
			built = new ProcedimentServei();
			built.procediment = procediment;
			built.servei = servei;
			built.actiu = true;
		}

		/**
		 * Construeix el nou objecte de tipus ProcedimentServei.
		 * 
		 * @return L'objecte de tipus ProcedimentServei creat.
		 */
		public ProcedimentServei build() {
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
				+ ((procediment == null) ? 0 : procediment.hashCode());
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
		ProcedimentServei other = (ProcedimentServei) obj;
		if (procediment == null) {
			if (other.procediment != null)
				return false;
		} else if (!procediment.equals(other.procediment))
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
