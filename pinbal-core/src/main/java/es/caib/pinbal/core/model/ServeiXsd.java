/**
 * 
 */
package es.caib.pinbal.core.model;

import es.caib.pinbal.core.dto.XsdTipusEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import java.util.Date;

/**
 * Classe de model de dades que conté la informació d'un fitxer xsd d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@NoArgsConstructor @AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "pbl_servei_xsd",
		uniqueConstraints = { @UniqueConstraint(columnNames = {"servei_id", "tipus"}) },
		indexes = { @Index(name = "pbl_servei_xsd_servei_i", columnList = "servei_id") })
public class ServeiXsd extends AbstractPersistable<Long> {

	private static final long serialVersionUID = -4205096087669669861L;

	@Column(name = "servei_id", length = 64, nullable = false)
	private String servei;
	@Enumerated(EnumType.STRING)
	@Column(name = "tipus", length = 32, nullable = false)
	private XsdTipusEnumDto tipus;
	@Setter
	@Column(name = "nomarxiu", length = 255, nullable = false)
	private String nomArxiu;
	@Column(name = "path", length = 255, nullable = false)
	private String path;
	@Column(name = "data", length = 255, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataModificacio;

	@Version
	private long version = 0;

	public void updateServeiXsd() {
		this.dataModificacio = new Date();
		this.version++;
	}

}
