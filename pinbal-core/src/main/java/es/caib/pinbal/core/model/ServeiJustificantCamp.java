/**
 * 
 */
package es.caib.pinbal.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.pinbal.core.audit.PinbalAuditable;

/**
 * Classe de model de dades que conté la traducció d'un camp de
 * les dades específiques d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(
		name = "pbl_servei_justif_camp",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"servei_id", "xpath"})},
		indexes = {
				@Index(name = "pbl_servei_juscam_servei_i", columnList = "servei_id")})
@EntityListeners(AuditingEntityListener.class)
public class ServeiJustificantCamp extends PinbalAuditable<Long> {

	private static final long serialVersionUID = -6657066865382086237L;

	@Column(name = "servei_id", length = 64, nullable = false)
	private String servei;
	@Column(name = "xpath", length = 255, nullable = false)
	private String xpath;
	@Column(name = "locale_idioma", length = 2, nullable = false)
	private String localeIdioma;
	@Column(name = "locale_regio", length = 2, nullable = false)
	private String localeRegio;
	@Column(name = "traduccio", length = 255)
	private String traduccio;

	@Version
	private long version = 0;



	/**
	 * Obté el Builder per a crear objectes de tipus ServeiCamp.
	 * 
	 * @param servei
	 *            El codi del servei.
	 * @param xpath
	 *            El xpath del camp.
	 * @param localeIdioma
	 *            Codi d'idioma del locale segons ISO-639.
	 * @param localeRegio
	 *            Codi de regió del locale segons ISO-3166.
	 * @param traduccio
	 *            La traducció pel camp.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String servei,
			String path,
			String localeIdioma,
			String localeRegio,
			String traduccio) {
		return new Builder(
				servei,
				path,
				localeIdioma,
				localeRegio,
				traduccio);
	}

	public String getServei() {
		return servei;
	}
	public String getXpath() {
		return xpath;
	}
	public String getLocaleIdioma() {
		return localeIdioma;
	}
	public String getLocaleRegio() {
		return localeRegio;
	}
	public String getTraduccio() {
		return traduccio;
	}

	public long getVersion() {
		return version;
	}

	public void update(
			String traduccio) {
		this.traduccio = traduccio;
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus ServeiCamp.
	 */
	public static class Builder {
		ServeiJustificantCamp built;

		/**
		 * Crea una nova instància del Builder.
		 * 
		 * @param servei
		 *            El codi del servei.
		 * @param xpath
		 *            El xpath del camp.
		 * @param localeIdioma
		 *            El codi d'idioma del camp segons ISO-639.
		 * @param localeRegio
		 *            El codi de regió del camp segons ISO-3166.
		 * @param traduccio
		 *            La traducció del camp.
		 */
		Builder(String servei,
				String xpath,
				String localeIdioma,
				String localeRegio,
				String traduccio) {
			built = new ServeiJustificantCamp();
			built.servei = servei;
			built.xpath = xpath;
			built.localeIdioma = localeIdioma;
			built.localeRegio = localeRegio;
			built.traduccio = traduccio;
		}

		/**
		 * Construeix el nou objecte de tipus ServeiCamp.
		 * 
		 * @return The created Person object.
		 */
		public ServeiJustificantCamp build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((localeIdioma == null) ? 0 : localeIdioma.hashCode());
		result = prime * result
				+ ((localeRegio == null) ? 0 : localeRegio.hashCode());
		result = prime * result + ((xpath == null) ? 0 : xpath.hashCode());
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
		ServeiJustificantCamp other = (ServeiJustificantCamp) obj;
		if (localeIdioma == null) {
			if (other.localeIdioma != null)
				return false;
		} else if (!localeIdioma.equals(other.localeIdioma))
			return false;
		if (localeRegio == null) {
			if (other.localeRegio != null)
				return false;
		} else if (!localeRegio.equals(other.localeRegio))
			return false;
		if (xpath == null) {
			if (other.xpath != null)
				return false;
		} else if (!xpath.equals(other.xpath))
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
