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
 * Classe de model de dades que conté la informació d'una parella entitat-usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "pbl_entitat_usuari",
		uniqueConstraints = {
			@UniqueConstraint(columnNames={"entitat_id", "usuari_id"})},
		indexes = {
				@Index(name = "pbl_entiusr_entitat_i", columnList = "entitat_id"),
				@Index(name = "pbl_entiusr_usuari_i", columnList = "usuari_id")})
@EntityListeners(AuditingEntityListener.class)
public class EntitatUsuari extends PinbalAuditable<Long> {

	private static final long serialVersionUID = -6657066865382086237L;

	@Column(name = "representant")
	private boolean representant;

	@Column(name = "delegat")
	private boolean delegat;

	@Column(name = "auditor")
	private boolean auditor;

	@Column(name = "aplicacio")
	private boolean aplicacio;

	@Column(name = "departament", length = 64)
	private String departament;

	@Column(name = "principal")
	private boolean principal = false;

	@ManyToOne(optional=false, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "entitat_id",
			foreignKey = @ForeignKey(name="pbl_entitat_entiusr_fk"))
	private Entitat entitat;

	@ManyToOne(optional=false, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "usuari_id",
			foreignKey = @ForeignKey(name = "pbl_usuari_entiusr_fk"))
	private Usuari usuari;

	@Version
	private long version = 0;



	/**
	 * Obté el Builder per a crear objectes de tipus Entitat.
	 *            
	 * @param entitat
	 *            Entitat del registre.
	 * @param usuari
	 *            Usuari del registre.
	 * @param departament
	 *            Departament del registre.
	 * @param representant
	 *            Indica si l'usuari d'una entitat actua com a representant.
	 * @param delegat
	 *            Indica si l'usuari d'una entitat actua com a delegat.
	 * @param auditor
	 *            Indica si l'usuari d'una entitat actua com a auditor.
	 * @param aplicacio
	 *            Indica si l'usuari d'una entitat actua com a aplicacio.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			Entitat entitat,
			Usuari usuari,
			String departament,
			boolean representant,
			boolean delegat,
			boolean auditor,
			boolean aplicacio) {
		return new Builder(
				entitat,
				usuari,
				departament,
				representant,
				delegat,
				auditor,
				aplicacio);
	}

	public boolean isRepresentant() {
		return representant;
	}

	public boolean isDelegat() {
		return delegat;
	}

	public boolean isAuditor() {
		return auditor;
	}

	public boolean isAplicacio() {
		return aplicacio;
	}

	public String getDepartament() {
		return departament;
	}

	public boolean isPrincipal() {
		return principal;
	}

	public Entitat getEntitat() {
		return entitat;
	}

	public Usuari getUsuari() {
		return usuari;
	}

	public long getVersion() {
		return version;
	}

	public void update(
			String departament,
			boolean representant,
			boolean delegat,
			boolean auditor,
			boolean aplicacio) {
		this.departament = departament;
		this.representant = representant;
		this.delegat = delegat;
		this.auditor = auditor;
		this.aplicacio = aplicacio;
	}
	public void updatePrincipal(
			boolean principal) {
		this.principal = principal;
	}
	public boolean canviPrincipal() {
		principal = !principal;
		return principal;
	}

	public void updateUsuari(Usuari usuariNou) {
		this.usuari = usuariNou;
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus EntitatUsuari.
	 */
	public static class Builder {
		EntitatUsuari built;

		/**
		 * Crea una nova instància del Builder.
		 * 
		 *@param entitat
		 *            Entitat del registre.
		 * @param usuari
		 *            Usuari del registre.
		 * @param departament
		 *            Departament del registre.
		 * @param representant
		 *            Indica si l'usuari d'una entitat actua com a representant.
		 * @param delegat
		 *            Indica si l'usuari d'una entitat actua com a delegat.
		 * @param auditor
		 *            Indica si l'usuari d'una entitat actua com a auditor.
		 * @param aplicacio
		 *            Indica si l'usuari d'una entitat actua com a aplicacio.
		 */
		Builder(
				Entitat entitat,
				Usuari usuari,
				String departament,
				boolean representant,
				boolean delegat,
				boolean auditor,
				boolean aplicacio) {
			built = new EntitatUsuari();
			built.entitat = entitat;
			built.usuari = usuari;
			built.departament = departament;
			built.representant = representant;
			built.delegat = delegat;
			built.auditor = auditor;
			built.aplicacio = aplicacio;
		}

		/**
		 * Construeix el nou objecte de tipus EntitatUsuari.
		 * 
		 * @return L'objecte de tipus EntitatUsuari creat.
		 */
		public EntitatUsuari build() {
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
		result = prime * result + ((entitat == null) ? 0 : entitat.hashCode());
		result = prime * result + ((usuari == null) ? 0 : usuari.hashCode());
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
		EntitatUsuari other = (EntitatUsuari) obj;
		if (entitat == null) {
			if (other.entitat != null)
				return false;
		} else if (!entitat.equals(other.entitat))
			return false;
		if (usuari == null) {
			if (other.usuari != null)
				return false;
		} else if (!usuari.equals(other.usuari))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
