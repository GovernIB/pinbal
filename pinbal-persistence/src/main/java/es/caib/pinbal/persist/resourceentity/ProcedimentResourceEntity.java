package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.dto.ProcedimentClaseTramiteEnumDto;
import es.caib.pinbal.logic.intf.model.ProcedimentResource;
import es.caib.pinbal.persist.base.entity.BaseResourceEntity;
import es.caib.pinbal.persist.entity.ProcedimentClasseTramiteConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
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
 * Entitat de base de dades del recurs {@link ProcedimentResource}.
 * <p>
 * Mapeja la mateixa taula que {@link es.caib.pinbal.persist.entity.Procediment}.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "pbl_procediment")
@Getter
@Setter
@NoArgsConstructor
public class ProcedimentResourceEntity extends BaseResourceEntity<ProcedimentResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "codi", length = 20, nullable = false)
	private String codi;

	@Column(name = "nom", length = 255, nullable = false)
	private String nom;

	@Column(name = "departament", length = 64)
	private String departament;

	@Column(name = "actiu")
	private boolean actiu = true;

	@Column(name = "codi_sia")
	private String codiSia;

	@Column(name = "valor_camp_automatizado")
	private Boolean valorCampAutomatizado;

	@Column(name = "valor_camp_clasetramite")
	@Convert(converter = ProcedimentClasseTramiteConverter.class)
	private ProcedimentClaseTramiteEnumDto valorCampClaseTramite;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	private EntitatResourceEntity entitat;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "organ_gestor_id")
	private OrganGestorResourceEntity organGestor;

	@Version
	private long version = 0;

}
