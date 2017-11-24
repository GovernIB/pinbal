/**
 * 
 */
package es.caib.pinbal.core.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.pinbal.core.audit.PinbalAuditable;

/**
 * Classe de model de dades que conté la informació d'un camp per
 * a les dades específiques d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(
		name = "pbl_servei_camp_grup",
		indexes = {
				@Index(name = "pbl_servei_campgr_servei_i", columnList = "servei_id")})
@EntityListeners(AuditingEntityListener.class)
public class ServeiCampGrup extends PinbalAuditable<Long> {

	private static final long serialVersionUID = -6657066865382086237L;

	@Column(name = "servei_id", length = 64, nullable = false)
	private String servei;
	@Column(name = "nom", length = 255, nullable = false)
	private String nom;
	@Column(name = "ordre")
	private int ordre;

	@OneToMany(mappedBy="grup", cascade={CascadeType.ALL})
	@OrderBy("ordre asc")
	private List<ServeiCamp> camps = new ArrayList<ServeiCamp>();

	@Version
	private long version = 0;



	/**
	 * Obté el Builder per a crear objectes de tipus ServeiCampGrup.
	 * 
	 * @param servei
	 *            El codi del servei.
	 * @param nom
	 *            El nom del grup.
	 * @param ordre
	 *            L'ordre del grup.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String servei,
			String nom,
			int ordre) {
		return new Builder(
				servei,
				nom,
				ordre);
	}

	public String getServei() {
		return servei;
	}

	public String getNom() {
		return nom;
	}

	public List<ServeiCamp> getCamps() {
		return camps;
	}

	public int getOrdre() {
		return ordre;
	}

	public long getVersion() {
		return version;
	}

	public void update(
			String nom) {
		this.nom = nom;
	}
	public void updateOrdre(
			int ordre) {
		this.ordre = ordre;
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus ServeiCampGrup.
	 */
	public static class Builder {
		ServeiCampGrup built;
		/**
		 * Crea una nova instància del Builder.
		 * 
		 * @param servei
		 *            El codi del servei.
		 * @param nom
		 *            El nom del grup.
		 * @param ordre
		 *            L'ordre del grup.
		 */
		Builder(
				String servei,
				String nom,
				int ordre) {
			built = new ServeiCampGrup();
			built.servei = servei;
			built.nom = nom;
			built.ordre = ordre;
		}

		/**
		 * Construeix el nou objecte de tipus ServeiCampGrup.
		 * 
		 * @return The created Person object.
		 */
		public ServeiCampGrup build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
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
		ServeiCampGrup other = (ServeiCampGrup) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
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
