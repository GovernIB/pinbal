package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.model.EntitatServeiResource;
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
 * Entitat de base de dades del recurs {@link EntitatServeiResource}.
 * <p>
 * Mapeja la mateixa taula que {@link es.caib.pinbal.persist.entity.EntitatServei}.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "pbl_entitat_servei")
@Getter
@Setter
@NoArgsConstructor
public class EntitatServeiResourceEntity extends BaseResourceEntity<EntitatServeiResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "servei_id", length = 64)
	private String serveiCodi;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	private EntitatResourceEntity entitat;

	@Version
	private long version = 0;

}
