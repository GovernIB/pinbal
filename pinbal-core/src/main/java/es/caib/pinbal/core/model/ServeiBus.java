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
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.pinbal.core.audit.PinbalAuditable;

/**
 * Configuració per al bus de serveis per a redireccionar
 * les peticions als serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(
		name = "pbl_servei_bus",
		indexes = {
				@Index(name = "pbl_servei_bus_servei_i", columnList = "servei_id")})
@EntityListeners(AuditingEntityListener.class)
public class ServeiBus extends PinbalAuditable<Long> {

	private static final long serialVersionUID = -348876812591122748L;

	@Column(name = "servei_id", length = 64, nullable = false)
	private String servei;

	@Column(name = "url_desti", length = 255, nullable = false)
	private String urlDesti;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "entitat_id",
			foreignKey = @ForeignKey(name = "pbl_entitat_servbus_fk"))
	private Entitat entitat;

	@Version
	private long version = 0;



	/**
	 * Obté el Builder per a crear objectes de tipus ServeiBus.
	 * 
	 * @param servei
	 *            Codi del servei al que fa referència aquest redirecció.
	 * @param urlDesti
	 *            Url destí de la redirecció.
	 * @param entitat
	 *            Indica l'entitat a la qual està associada aquesta redirecció.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String servei,
			String urlDesti,
			Entitat entitat) {
		return new Builder(servei, urlDesti, entitat);
	}

	public String getServei() {
		return servei;
	}

	public String getUrlDesti() {
		return urlDesti;
	}

	public Entitat getEntitat() {
		return entitat;
	}

	public void update(String urlDesti, Entitat entitat) {
		this.urlDesti = urlDesti;
		this.entitat = entitat;
	}

	public long getVersion() {
		return version;
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus ServeiBus.
	 */
	public static class Builder {
		ServeiBus built;
		/**
		 * Crea una nova instància del ServeiBus.
		 * 
		 * @param servei
		 *            Codi del servei al que fa referència aquest redirecció.
		 * @param urlDesti
		 *            Url destí de la redirecció.
		 * @param entitat
		 *            Indica l'entitat a la qual està associada aquesta redirecció.
		 */
		Builder(String servei,
				String urlDesti,
				Entitat entitat) {
			built = new ServeiBus();
			built.servei = servei;
			built.urlDesti = urlDesti;
			built.entitat = entitat;
		}
		/**
		 * Construeix el nou objecte de tipus ServeiBus.
		 * 
		 * @return L'objecte de tipus ServeiBus creat.
		 */
		public ServeiBus build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((entitat == null) ? 0 : entitat.hashCode());
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
		ServeiBus other = (ServeiBus) obj;
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
