/**
 * 
 */
package es.caib.pinbal.persist.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Classe de model de dades que conté la informació d'un
 * emisor certificat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "core_emisor_certificado")
@EntityListeners(AuditingEntityListener.class)
public class EmissorCert implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "nombre", length = 50, nullable = false)
	private String nom;
	@Column(name = "cif", length = 16, nullable = false)
	private String cif;
	@Column(name = "fechabaja")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataBaixa;
	
	
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
			String nom,
			String cif,
			Date dataBaixa) {
		return new Builder(
				nom,
				cif,
				dataBaixa);
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
	
	
	public void update(
			String nom,
			String cif,
			Date dataBaixa) {
		this.nom = nom;
		this.cif = cif;
		this.dataBaixa = dataBaixa;
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus EmisorCert.
	 */
	public static class Builder {
		EmissorCert built;

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
				String nom,
				String cif,
				Date dataBaixa) {
			built = new EmissorCert();
			built.nom = nom;
			built.cif = cif;
			built.dataBaixa = dataBaixa;
		}

		/**
		 * Construeix el nou objecte de tipus Entitat.
		 * 
		 * @return L'objecte de tipus Entitat creat.
		 */
		public EmissorCert build() {
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
		EmissorCert other = (EmissorCert) obj;
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
