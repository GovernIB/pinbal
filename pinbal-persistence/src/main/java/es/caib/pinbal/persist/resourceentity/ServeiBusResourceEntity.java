package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.model.ServeiBusResource;
import es.caib.pinbal.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Entitat de base de dades del recurs {@link ServeiBusResource}.
 * <p>
 * Mapeja la mateixa taula que {@link es.caib.pinbal.persist.entity.ServeiBus}.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "pbl_servei_bus")
@Getter
@Setter
@NoArgsConstructor
public class ServeiBusResourceEntity extends BaseResourceEntity<ServeiBusResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "servei_id", length = 64, nullable = false)
	private String serveiCodi;

	@Column(name = "url_desti", length = 255, nullable = false)
	private String urlDesti;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	private EntitatResourceEntity entitat;

	@Version
	private long version = 0;

}
