package es.caib.pinbal.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Classe de model de dades que conté la informació d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
// TODO: averiguar si a table s'han de posar les constraints i els indexs
@Entity
@Table(name = "core_servicio")
@EntityListeners(AuditingEntityListener.class)
public class Servei implements Serializable {
	
	private static final long serialVersionUID = 4672258012588241720L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "CODCERTIFICADO", length = 64, nullable = false, unique = true)
	private String codi;

	@Column(name = "descripcion", length = 512, nullable = false)
	private String descripcio;

	@ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "emisor",
			foreignKey = @ForeignKey(name = "serv_emisor"))
	private EmissorCert scspEmisor;
	
	@Column(name = "fechaalta")
	@Temporal(TemporalType.DATE)
	private Date scspFechaAlta;
	
	@Column(name = "fechabaja")
	@Temporal(TemporalType.DATE)
	private Date scspFechaBaja;
	
	@Column(name = "caducidad")
	private int caducitat;

	
	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public EmissorCert getScspEmisor() {
		return scspEmisor;
	}

	public void setScspEmisor(EmissorCert scspEmisor) {
		this.scspEmisor = scspEmisor;
	}


	public Date getScspFechaAlta() {
		return scspFechaAlta;
	}

	public void setScspFechaAlta(Date scspFechaAlta) {
		this.scspFechaAlta = scspFechaAlta;
	}

	public Date getScspFechaBaja() {
		return scspFechaBaja;
	}

	public void setScspFechaBaja(Date scspFechaBaja) {
		this.scspFechaBaja = scspFechaBaja;
	}

	public int getCaducitat() {
		return caducitat;
	}

	public void setCaducitat(int caducitat) {
		this.caducitat = caducitat;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
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
		Servei other = (Servei) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
