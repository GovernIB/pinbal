/**
 * 
 */
package es.caib.pinbal.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.pinbal.core.audit.PinbalAuditingEntityListener;

/**
 * Classe de model de dades que conté la informació d'un
 * organisme cesionari
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "organismo_cesionario")
@EntityListeners(PinbalAuditingEntityListener.class)
public class OrganismeCessionari implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "nombre", length = 50)
	private String nom;
	@Column(name = "cif", length = 50)
	private String cif;
	@Column(name = "fechabaja")
	@Temporal(TemporalType.DATE)
	private Date dataBaixa;
	@Column(name = "fechaalta")
	@Temporal(TemporalType.DATE)
	private Date dataAlta;
	@Column(name = "bloqueado", nullable = false)
	private Boolean bloquejat;
	@Column(name = "logo")
	private byte[] logo;
	
	@OneToMany(
			mappedBy = "organisme",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<ClauPrivada> claus = new ArrayList<ClauPrivada>();
	
	
	/**
	 * Obté el Builder per a crear objectes de tipus OrganismeCessionari.
	 * 
	 * @param nom
	 *            El nom del organisme cessionari.
	 * @param cif
	 *            El cif del organisme cessionari.
	 * @param dataBaixa
	 *            La data de baixa del organisme cessionari.
	 * @param dataAlta
	 *            La data de alta del organisme cessionari.
	 * @param bloquejat
	 *            La propietat bloquejat del organisme cessionari.
	 * @param logo
	 *            El logo del organisme cessionari.
	 * @param claus
	 *            Les claus privades que hi estan relacionades.
	 * 
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String nom,
			String cif,
			Date dataBaixa,
			Date dataAlta,
			Boolean bloquejat,
			byte[] logo,
			List<ClauPrivada> claus) {
		return new Builder(
				nom,
				cif,
				dataBaixa,
				dataAlta,
				bloquejat,
				logo,
				claus);
	}
	
	
	public Long getId() {
		return id;
	}
	
	public String getNom() {
		return nom;
	}
	
	public String getCif() {
		return cif;
	}
	
	public Date getDataBaixa() {
		return dataBaixa;
	}
	
	public Date getDataAlta() {
		return dataAlta;
	}
	
	public Boolean getBloquejat() {
		return bloquejat;
	}
	
	public byte[] getLogo() {
		return logo;
	}
	
	public List<ClauPrivada> getClaus() {
		return claus;
	}
	
	
	public void update(
			String nom,
			String cif,
			Date dataBaixa,
			Date dataAlta,
			Boolean bloquejat,
			byte[] logo,
			List<ClauPrivada> claus) {
		this.nom = nom;
		this.cif = cif;
		this.dataBaixa = dataBaixa;
		this.dataAlta = dataAlta;
		this.bloquejat = bloquejat;
		this.logo = logo;
		this.claus = claus;
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus OrganismeCessionari.
	 */
	public static class Builder {
		OrganismeCessionari built;

		/**
		 * Crea una nova instància del Builder.
		 * 
		 * @param nom
		 *            El nom del organisme cessionari.
		 * @param cif
		 *            El cif del organisme cessionari.
		 * @param dataBaixa
		 *            La data de baixa del organisme cessionari.
		 * @param dataAlta
		 *            La data de alta del organisme cessionari.
		 * @param bloquejat
		 *            La propietat bloquejat del organisme cessionari.
		 * @param logo
		 *            El logo del organisme cessionari.
		 * @param claus
		 *            Les claus privades que hi estan relacionades.
		 */
		Builder(
				String nom,
				String cif,
				Date dataBaixa,
				Date dataAlta,
				Boolean bloquejat,
				byte[] logo,
				List<ClauPrivada> claus) {
			built = new OrganismeCessionari();
			built.nom = nom;
			built.cif = cif;
			built.dataBaixa = dataBaixa;
			built.dataAlta = dataAlta;
			built.bloquejat = bloquejat;
			built.logo = logo;
			built.claus = claus;
		}

		/**
		 * Construeix el nou objecte de tipus OrganismeCessionari.
		 * 
		 * @return L'objecte de tipus OrganismeCessionari creat.
		 */
		public OrganismeCessionari build() {
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
		OrganismeCessionari other = (OrganismeCessionari) obj;
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
