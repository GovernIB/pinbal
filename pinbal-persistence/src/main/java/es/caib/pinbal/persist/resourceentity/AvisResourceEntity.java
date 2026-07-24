package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.dto.AvisNivellEnumDto;
import es.caib.pinbal.logic.intf.model.AvisResource;
import es.caib.pinbal.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Entitat de base de dades del recurs {@link AvisResource}.
 * <p>
 * Mapeja la mateixa taula que {@link es.caib.pinbal.persist.entity.Avis}.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "pbl_avis")
@Getter
@Setter
@NoArgsConstructor
public class AvisResourceEntity extends BaseResourceEntity<AvisResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "assumpte", length = 256, nullable = false)
	private String assumpte;

	@Column(name = "missatge", length = 2048, nullable = false)
	private String missatge;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inici", nullable = false)
	private Date dataInici;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_final")
	private Date dataFinal;

	@Column(name = "actiu", nullable = false)
	private boolean actiu;

	@Enumerated(EnumType.STRING)
	@Column(name = "avis_nivell", length = 10, nullable = false)
	private AvisNivellEnumDto avisNivell;

}
