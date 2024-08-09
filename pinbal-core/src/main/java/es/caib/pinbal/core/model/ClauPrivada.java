/**
 * 
 */
package es.caib.pinbal.core.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Classe de model de dades que conté la informació d'una
 * clau privada
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
//@SuppressWarnings("deprecation")
@Entity
@Table(name = "core_clave_privada")
@EntityListeners(AuditingEntityListener.class)
public class ClauPrivada implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "alias", length = 256, nullable = false)
	private String alies;
	@Column(name = "nombre", length = 256, nullable = false)
	private String nom;
	@Column(name = "password", length = 256, nullable = false)
	private String password;
	@Column(name = "numeroserie", length = 256, nullable = false)
	private String numSerie;
	@Column(name = "fechabaja")
	@Temporal(TemporalType.DATE)
	private Date dataBaixa;
	@Column(name = "fechaalta")
	@Temporal(TemporalType.DATE)
	private Date dataAlta;
	@Column(name = "interoperabilidad")
	private boolean interoperabilitat;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "organismo")
	@ForeignKey(name = "clave_privada_org")
	private OrganismeCessionari organisme;
	
	
	/**
	 * Obté el Builder per a crear objectes de tipus ClauPrivada.
	 * 
	 * @param alies
	 *            El alies de la clau privada.
	 * @param nom
	 *            El nom de la clau privada.
	 * @param password
	 *            El cif de la clau privada.
	 * @param numeroserie
	 *            El cif de la clau privada.
	 * @param dataBaixa
	 *            La data de baixa de la clau privada.
	 * @param dataAlta
	 *            La data de alta de la clau privada.
	 * @param interoperabilitat
	 *            La propietat d'interoperabilitat de la clau privada.
	 * @param organisme
	 *            L'organisme cessionari amb que està relacionat.
	 * 
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String alies,
			String nom,
			String password,
			String numeroserie,
			Date dataBaixa,
			Date dataAlta,
			boolean interoperabilitat,
			OrganismeCessionari organisme) {
		return new Builder(
				alies,
				nom,
				password,
				numeroserie,
				dataBaixa,
				dataAlta,
				interoperabilitat,
				organisme);
	}
	
	
	public Long getId() {
		return id;
	}
	
	public String getAlies() {
		return alies;
	}
	
	public String getNom() {
		return nom;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getNumSerie() {
		return numSerie;
	}
	
	public Date getDataBaixa() {
		return dataBaixa;
	}
	
	public Date getDataAlta() {
		return dataAlta;
	}
	
	public boolean getInteroperabilitat() {
		return interoperabilitat;
	}
	
	public OrganismeCessionari getOrganisme() {
		return organisme;
	}
	
	
	public void update(
			String alies,
			String nom,
			String password,
			String numSerie,
			Date dataBaixa,
			Date dataAlta,
			boolean interoperabilitat,
			OrganismeCessionari organisme) {
		this.alies = alies;
		this.nom = nom;
		this.password = password;
		this.numSerie = numSerie;
		this.dataBaixa = dataBaixa;
		this.dataAlta = dataAlta;
		this.interoperabilitat = interoperabilitat;
		this.organisme = organisme;
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus ClauPrivada.
	 */
	public static class Builder {
		ClauPrivada built;

		/**
		 * Crea una nova instància del Builder.
		 * 
		 * @param alies
		 *            El alies de la clau privada.
		 * @param nom
		 *            El nom de la clau privada.
		 * @param password
		 *            El cif de la clau privada.
		 * @param numSerie
		 *            El cif de la clau privada.
		 * @param dataBaixa
		 *            La data de baixa de la clau privada.
		 * @param dataAlta
		 *            La data de alta de la clau privada.
		 * @param interoperabilitat
		 *            La propietat d'interoperabilitat de la clau privada.
		 * @param organisme
		 *            L'organisme cessionari amb que està relacionat.
		 */
		Builder(
				String alies,
				String nom,
				String password,
				String numSerie,
				Date dataBaixa,
				Date dataAlta,
				boolean interoperabilitat,
				OrganismeCessionari organisme) {
			built = new ClauPrivada();
			built.alies = alies;
			built.nom = nom;
			built.password = password;
			built.numSerie = numSerie;
			built.dataBaixa = dataBaixa;
			built.dataAlta = dataAlta;
			built.interoperabilitat = interoperabilitat;
			built.organisme = organisme;
		}

		/**
		 * Construeix el nou objecte de tipus ClauPrivada.
		 * 
		 * @return L'objecte de tipus ClauPrivada creat.
		 */
		public ClauPrivada build() {
			return built;
		}
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClauPrivada other = (ClauPrivada) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	private static final long serialVersionUID = -6657066865382086237L;
	
}
