package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.model.OrganismeCessionariResource;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Entitat de base de dades del recurs {@link OrganismeCessionariResource}.
 * <p>
 * Mapeja la mateixa taula que {@link es.caib.pinbal.persist.entity.OrganismeCessionari}. Només
 * de lectura: es fa servir com a picker de referència des de {@code ClauPrivadaResource}, el
 * manteniment complet no s'ha demanat.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "core_organismo_cesionario")
@Getter
@Setter
@NoArgsConstructor
public class OrganismeCessionariResourceEntity extends BaseResourceEntity<OrganismeCessionariResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "nombre", length = 50)
	private String nom;

	@Column(name = "cif", length = 50)
	private String cif;

	@Column(name = "bloqueado", nullable = false)
	private Boolean bloquejat;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechabaja")
	private Date dataBaixa;

}
