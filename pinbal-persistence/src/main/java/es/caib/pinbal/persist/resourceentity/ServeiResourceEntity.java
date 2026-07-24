package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.logic.intf.model.ServeiResource;
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
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 * Entitat de base de dades del recurs {@link ServeiResource}.
 * <p>
 * Mapeja la mateixa taula que {@link es.caib.pinbal.persist.entity.ServeiConfig} (configuració
 * pròpia de PINBAL), <strong>no</strong> la del servei SCSP en si (taula {@code core_servicio},
 * gestionada per la llibreria SCSP i no accessible com a entitat JPA local). El camp
 * {@code descripcio} (que sí que viu a {@code core_servicio}) es marca {@link Transient} i es
 * gestiona als hooks d'{@code ServeiResourceServiceImpl} via {@code ScspHelper}.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "pbl_servei_config")
@Getter
@Setter
@NoArgsConstructor
public class ServeiResourceEntity extends BaseResourceEntity<ServeiResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "servei_id", length = 64, nullable = false, unique = true)
	private String codi;

	@Transient
	private String descripcio;

	@Column(name = "actiu", nullable = false)
	private boolean actiu;

	// Sense @Enumerated: mateix mapeig ORDINAL que ServeiConfig#entitatTipus.
	@Column(name = "entitat_tipus")
	private ServeiDto.EntitatTipusDto pinbalEntitatTipus;

	@Column(name = "role_name", length = 64)
	private String pinbalRoleName;

	@Column(name = "permes_doctip_dni")
	private boolean pinbalPermesDocumentTipusDni;

	@Column(name = "permes_doctip_nif")
	private boolean pinbalPermesDocumentTipusNif;

	@Column(name = "permes_doctip_cif")
	private boolean pinbalPermesDocumentTipusCif;

	@Column(name = "permes_doctip_nie")
	private boolean pinbalPermesDocumentTipusNie;

	@Column(name = "permes_doctip_pas")
	private boolean pinbalPermesDocumentTipusPas;

	@Column(name = "document_obligatori")
	private boolean pinbalDocumentObligatori;

	@Column(name = "max_peticions_min")
	private Integer maxPeticionsMinut;

	@Version
	private long version = 0;

}
