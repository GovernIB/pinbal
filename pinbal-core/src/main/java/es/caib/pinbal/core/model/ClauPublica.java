/**
 * 
 */
package es.caib.pinbal.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.pinbal.core.audit.PinbalAuditingEntityListener;

/**
 * Classe de model de dades que conté la informació d'una
 * clau pública. 
 * La taula de claus públiques també es consulten a partir del ScspHelper i la llibreria
 * scsp-core.jar però tenim les nostres pròpies entitats per fer-ne el manteniment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "core_clave_publica")
@EntityListeners(PinbalAuditingEntityListener.class)
public class ClauPublica implements Serializable {
	

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "nombre", length = 256, nullable = false)
	private String nom;
	@Column(name = "alias", length = 256, nullable = false)
	private String alies;
	@Column(name = "numeroserie", length = 256, nullable = false)
	private String numSerie;
	@Column(name = "fechaalta", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataAlta;
	@Column(name = "fechabaja")
	@Temporal(TemporalType.DATE)
	private Date dataBaixa;
	

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getAlies() {
		return alies;
	}
	public void setAlies(String alies) {
		this.alies = alies;
	}

	public String getNumSerie() {
		return numSerie;
	}
	public void setNumSerie(String numSerie) {
		this.numSerie = numSerie;
	}

	public Date getDataAlta() {
		return dataAlta;
	}
	public void setDataAlta(Date dataAlta) {
		this.dataAlta = dataAlta;
	}

	public Date getDataBaixa() {
		return dataBaixa;
	}
	public void setDataBaixa(Date dataBaixa) {
		this.dataBaixa = dataBaixa;
	}


	/**
	 * Obté el Builder per a crear objectes de tipus EmisorCert.
	 * 
	 * @param nom
	 *            El nom del emisor cert.
	 * @param cif
	 *            El cif del emisor cert.
	 * @param dataBaixa
	 *            La data de baixa.
	 * 
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String alies,
			String nom,
			String numSerie,
			Date dataAlta,
			Date dataBaixa) {
		return new Builder(
				alies,
				nom,
				numSerie,
				dataAlta,
				dataBaixa);
	}
	
	
	public void update(
			String alies,
			String nom,
			String numSerie,
			Date dataAlta,
			Date dataBaixa) {
		this.alies = alies;
		this.nom = nom;
		this.numSerie = numSerie;
		this.dataAlta = dataAlta;
		this.dataBaixa = dataBaixa;
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus ClauPublica.
	 */
	public static class Builder {
		ClauPublica built;

		/**
		 * Crea una nova instància del Builder.
		 * 
		 * @param nom
		 *            El nom del emisor cert.
		 * @param cif
		 *            El cif del emisor cert.
		 * @param dataBaixa
		 *            La data de baixa.
		 */
		Builder(
				String alies,
				String nom,
				String numSerie,
				Date dataAlta,
				Date dataBaixa) {
			built = new ClauPublica();
			built.alies = alies;
			built.nom = nom;
			built.numSerie = numSerie;
			built.dataAlta = dataAlta;
			built.dataBaixa = dataBaixa;
		}

		/**
		 * Construeix el nou objecte de tipus Entitat.
		 * 
		 * @return L'objecte de tipus Entitat creat.
		 */
		public ClauPublica build() {
			return built;
		}
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
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
		ClauPublica other = (ClauPublica) obj;
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
	
	
	private static final long serialVersionUID = -7232616919018376158L;	
}
