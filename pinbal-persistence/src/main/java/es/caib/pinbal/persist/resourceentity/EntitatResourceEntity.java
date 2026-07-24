package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.model.EntitatResource;
import es.caib.pinbal.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Entitat de base de dades del recurs {@link EntitatResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link es.caib.pinbal.persist.entity.Entitat},
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "pbl_entitat")
@Getter
@Setter
@NoArgsConstructor
public class EntitatResourceEntity extends BaseResourceEntity<EntitatResource, Long> {

	// GenerationType.AUTO resol a HIBERNATE_SEQUENCE, la seqüència compartida per totes les
	// taules pbl_* (vegeu db/changelog/init/04_initial_schema_sequence.yaml), igual que
	// es.caib.pinbal.persist.entity.Entitat (via AbstractAuditable).
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;

	@Column(name = "nom", length = 255, nullable = false)
	private String nom;

	@Column(name = "cif", length = 16, nullable = false)
	private String cif;

	@Column(name = "unitat_arrel", length = 9, nullable = false)
	private String unitatArrel;

	// Sense @Enumerated: mateix mapeig ORDINAL que es.caib.pinbal.persist.entity.Entitat#tipus
	// (l'ordre dels valors de EntitatTipusDto ha de coincidir amb Entitat.EntitatTipus).
	@Column(name = "tipus", nullable = false)
	private EntitatDto.EntitatTipusDto tipus;

	@Column(name = "activa")
	private boolean activa = true;

	@Version
	private long version = 0;

}
