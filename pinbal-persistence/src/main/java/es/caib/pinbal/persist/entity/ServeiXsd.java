/**
 * 
 */
package es.caib.pinbal.persist.entity;

import es.caib.pinbal.logic.intf.dto.XsdTipusEnumDto;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;

/**
 * Classe de model de dades que conté la informació d'un fitxer xsd d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@NoArgsConstructor @AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
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
	@Builder.Default
	private long version = 0;

	public void updateServeiXsd() {
		this.dataModificacio = new Date();
		this.version++;
	}

}
