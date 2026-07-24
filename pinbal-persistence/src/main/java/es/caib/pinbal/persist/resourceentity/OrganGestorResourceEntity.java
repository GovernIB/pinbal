package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.dto.OrganGestorEstatEnum;
import es.caib.pinbal.logic.intf.model.OrganGestorResource;
import es.caib.pinbal.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entitat de base de dades del recurs {@link OrganGestorResource}.
 * <p>
 * Mapeja la mateixa taula que {@link es.caib.pinbal.persist.entity.OrganGestor}.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "pbl_organ_gestor")
@Getter
@Setter
@NoArgsConstructor
public class OrganGestorResourceEntity extends BaseResourceEntity<OrganGestorResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;

	@Column(name = "nom", length = 1000)
	private String nom;

	@Column(name = "actiu")
	private boolean actiu;

	@Column(name = "estat")
	@Enumerated(EnumType.STRING)
	private OrganGestorEstatEnum estat;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	private EntitatResourceEntity entitat;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "pare_id")
	private OrganGestorResourceEntity pare;

}
