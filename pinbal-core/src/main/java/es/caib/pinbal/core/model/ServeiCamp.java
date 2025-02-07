/**
 * 
 */
package es.caib.pinbal.core.model;

import es.caib.pinbal.core.audit.PinbalAuditable;
import es.caib.pinbal.core.dto.ServeiCampDto.ServeiCampDtoValidacioDataTipus;
import es.caib.pinbal.core.dto.ServeiCampDto.ServeiCampDtoValidacioOperacio;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe de model de dades que conté la informació d'un camp per
 * a les dades específiques d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "pbl_servei_camp",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"servei_id", "path"})},
		indexes = {
				@Index(name = "pbl_servei_camp_servei_i", columnList = "servei_id")})
@EntityListeners(AuditingEntityListener.class)
public class ServeiCamp extends PinbalAuditable<Long> {

	private static final int ENUM_DESC_MAX_LENGTH = 1024;
	private static final String ENUM_DESC_SEPARADOR="#";

	public enum ServeiCampTipus {
		TEXT,
		NUMERIC,
		DATA,
		ENUM,
		PROVINCIA,
		MUNICIPI_5,
		ETIQUETA,
		BOOLEA,
		DOC_IDENT,
		PAIS,
		MUNICIPI_3,
		ADJUNT_BINARI,
		ADJUNT_XML
	}

	private static final long serialVersionUID = -6657066865382086237L;

	@Column(name = "servei_id", length = 64, nullable = false)
	private String servei;
	@Column(name = "path", length = 255, nullable = false)
	private String path;
	@Column(name = "tipus", nullable = false)
	private ServeiCampTipus tipus = ServeiCampTipus.TEXT;
	@Column(name = "etiqueta", length = 64)
	private String etiqueta;
	@Column(name = "valor_defecte", length = 64)
	private String valorPerDefecte;
	@Column(name = "comentari", length = 255)
	private String comentari;
	@Column(name = "enum_descs", length = ENUM_DESC_MAX_LENGTH)
	private String enumDescripcions;
	@Column(name = "data_format", length = 32)
	private String dataFormat;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "camp_pare_id",
			foreignKey = @ForeignKey(name = "pbl_pare_servcamp_fk"))
	private ServeiCamp campPare;
	@Column(name = "valor_pare", length = 64)
	private String valorPare;

	@Column(name = "inicialitzar")
	private boolean inicialitzar = false;
	@Column(name = "obligatori")
	private boolean obligatori = false;
	@Column(name = "modificable")
	private boolean modificable = true;
	@Column(name = "visible")
	private boolean visible = true;
	@Column(name = "ordre")
	private int ordre;
	@Column(name = "mida")
	private int mida;
	@Column(name = "val_regexp", length = 100)
	private String validacioRegexp;
	@Column(name = "val_min")
	private Integer validacioMin;
	@Column(name = "val_max")
	private Integer validacioMax;
	@Column(name = "val_data_cmp_op")
	private ServeiCampDtoValidacioOperacio validacioDataCmpOperacio;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "val_data_cmp_camp2_id",
			foreignKey = @ForeignKey(name = "pbl_pare_servcamp2_fk"))
	private ServeiCamp validacioDataCmpCamp2;
	@Column(name = "val_data_cmp_num")
	private Integer validacioDataCmpNombre;
	@Column(name = "val_data_cmp_tipus")
	private ServeiCampDtoValidacioDataTipus validacioDataCmpTipus;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "grup_id",
			foreignKey = @ForeignKey(name = "pbl_srcgrup_srvcamp_fk"))
	private ServeiCampGrup grup;

	@OneToMany(mappedBy="campPare")
	private Set<ServeiCamp> campsFills = new HashSet<ServeiCamp>();

	@Version
	private long version = 0;



	/**
	 * Obté el Builder per a crear objectes de tipus ServeiCamp.
	 * 
	 * @param servei
	 *            El codi del servei.
	 * @param path
	 *            El path del camp.
	 * @param ordre
	 *            L'ordre del camp.
	 * @param mida
	 *            La mida del camp.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String servei,
			String path,
			int ordre,
			int mida) {
		return new Builder(
				servei,
				path,
				ordre,
				mida);
	}
	/**
	 * Obté el Builder per a crear objectes de tipus ServeiCamp.
	 * 
	 * @param servei
	 *            El codi del servei.
	 * @param path
	 *            El path del camp.
	 * @param tipus
	 *            El tipus de dades suportades del camp.
	 * @param etiqueta
	 *            L'etiqueta del camp.
	 * @param valorPerDefecte
	 *            El valorPerDefecte del camp.
	 * @param ordre
	 *            L'ordre del camp.
	 * @param mida
	 *            La mida del camp.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String servei,
			String path,
			ServeiCampTipus tipus,
			String etiqueta,
			String valorPerDefecte,
			int ordre,
			int mida) {
		return new Builder(
				servei,
				path,
				tipus,
				etiqueta,
				valorPerDefecte,
				ordre,
				mida);
	}

	public String getServei() {
		return servei;
	}
	public String getPath() {
		return path;
	}
	public ServeiCampTipus getTipus() {
		return tipus;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public String getValorPerDefecte() {
		return valorPerDefecte;
	}
	public String getComentari() {
		return comentari;
	}
	public String[] getEnumDescripcions() {
		if (enumDescripcions != null)
			return enumDescripcions.split(ENUM_DESC_SEPARADOR);
		else
			return null;
	}
	public String getDataFormat() {
		return dataFormat;
	}
	public ServeiCamp getCampPare() {
		return campPare;
	}
	public String getValorPare() {
		return valorPare;
	}

	public boolean isInicialitzar() {
		return inicialitzar;
	}

	public boolean isObligatori() {
		return obligatori;
	}
	public void setObligatori(boolean obligatori) {
		this.obligatori = obligatori;
	}
	public boolean isModificable() {
		return modificable;
	}
	public void setModificable(boolean modificable) {
		this.modificable = modificable;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public int getOrdre() {
		return ordre;
	}
	public int getMida() {
		return mida;
	}
	public String getValidacioRegexp() {
		return validacioRegexp;
	}
	public Integer getValidacioMin() {
		return validacioMin;
	}
	public Integer getValidacioMax() {
		return validacioMax;
	}
	public ServeiCampDtoValidacioOperacio getValidacioDataCmpOperacio() {
		return validacioDataCmpOperacio;
	}
	public ServeiCamp getValidacioDataCmpCamp2() {
		return validacioDataCmpCamp2;
	}
	public Integer getValidacioDataCmpNombre() {
		return validacioDataCmpNombre;
	}
	public ServeiCampDtoValidacioDataTipus getValidacioDataCmpTipus() {
		return validacioDataCmpTipus;
	}
	public ServeiCampGrup getGrup() {
		return grup;
	}
	public Set<ServeiCamp> getCampsFills() {
		return campsFills;
	}
	public long getVersion() {
		return version;
	}

	public void update(
			ServeiCampTipus tipus,
			String etiqueta,
			int mida,
			String valorPerDefecte,
			String comentari,
			String[] enumDescripcions,
			String dataFormat,
			ServeiCamp campPare,
			String valorPare,
			ServeiCampGrup grup,
			boolean inicialitzar,
			boolean obligatori,
			boolean modificable,
			boolean visible,
			String validacioRegexp,
			Integer validacioMin,
			Integer validacioMax,
			ServeiCampDtoValidacioOperacio validacioDataCmpOperacio,
			ServeiCamp validacioDataCmpCamp2,
			Integer validacioDataCmpNombre,
			ServeiCampDtoValidacioDataTipus validacioDataCmpTipus) {
		this.tipus = tipus;
		this.etiqueta = etiqueta;
		this.mida = mida;
		this.valorPerDefecte = valorPerDefecte;
		this.comentari = comentari;
		updateEnumDescripcions(enumDescripcions);
		this.dataFormat = dataFormat;
		this.campPare = campPare;
		this.valorPare = valorPare;
		this.grup = grup;
		this.inicialitzar = inicialitzar;
		this.obligatori = obligatori;
		this.modificable = modificable;
		this.visible = visible;
		this.validacioRegexp = validacioRegexp;
		this.validacioMin = validacioMin;
		this.validacioMax = validacioMax;
		this.validacioDataCmpOperacio = validacioDataCmpOperacio;
		this.validacioDataCmpCamp2 = validacioDataCmpCamp2;
		this.validacioDataCmpNombre = validacioDataCmpNombre;
		this.validacioDataCmpTipus = validacioDataCmpTipus;
	}
	public void updateEnumDescripcions(String[] enumDescripcions) {
		if (enumDescripcions != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < enumDescripcions.length; i++) {
				sb.append(enumDescripcions[i]);
				if (i < enumDescripcions.length - 1)
					sb.append(ENUM_DESC_SEPARADOR);
			}
			if (sb.length() <= ENUM_DESC_MAX_LENGTH)
				this.enumDescripcions = sb.toString();
			else
				this.enumDescripcions = sb.substring(0, 1024);
		} else {
			this.enumDescripcions = null;
		}
	}
	public void updateTipus(
			ServeiCampTipus tipus) {
		this.tipus = tipus;
	}
	public void updateOrdre(
			int ordre) {
		this.ordre = ordre;
	}
	public void updateMida(
			int mida) {
		this.mida = mida;
	}
	public void updateGrup(
			ServeiCampGrup grup) {
		this.grup = grup;
	}
	public void deleteCampPare() {
		this.campPare = null;
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus ServeiCamp.
	 */
	public static class Builder {
		ServeiCamp built;

		/**
		 * Crea una nova instància del Builder.
		 * 
		 * @param servei
		 *            El codi del servei.
		 * @param path
		 *            El path del camp.
		 * @param ordre
		 *            L'ordre del camp.
		 * @param mida
		 *            La mida del camp.
		 */
		Builder(String servei, String path, int ordre, int mida) {
			built = new ServeiCamp();
			built.servei = servei;
			built.path = path;
			built.ordre = ordre;
			built.mida = mida;
		}

		/**
		 * Crea una nova instància del Builder.
		 * 
		 * @param servei
		 *            El codi del servei.
		 * @param path
		 *            El path del camp.
		 * @param tipus
		 *            El tipus de dades suportades del camp.
		 * @param etiqueta
		 *            L'etiqueta del camp.
		 * @param valorPerDefecte
		 *            El valorPerDefecte del camp.
		 * @param ordre
		 *            L'ordre del camp.
		 * @param mida
		 *            La mida del camp.
		 */
		Builder(
				String servei,
				String path,
				ServeiCampTipus tipus,
				String etiqueta,
				String valorPerDefecte,
				int ordre,
				int mida) {
			built = new ServeiCamp();
			built.servei = servei;
			built.path = path;
			built.tipus = tipus;
			built.etiqueta = etiqueta;
			built.valorPerDefecte = valorPerDefecte;
			built.ordre = ordre;
			built.mida = mida;
		}

		/**
		 * Construeix el nou objecte de tipus ServeiCamp.
		 * 
		 * @return The created Person object.
		 */
		public ServeiCamp build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		ServeiCamp other = (ServeiCamp) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
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
