package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.model.ClauPrivadaResource;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Entitat de base de dades del recurs {@link ClauPrivadaResource}.
 * <p>
 * Mapeja la mateixa taula que {@link es.caib.pinbal.persist.entity.ClauPrivada}.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "core_clave_privada")
@Getter
@Setter
@NoArgsConstructor
public class ClauPrivadaResourceEntity extends BaseResourceEntity<ClauPrivadaResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "alias", length = 256, nullable = false)
	private String alies;

	@Column(name = "nombre", length = 256, nullable = false)
	private String nom;

	@Column(name = "password", length = 256, nullable = false)
	private String password;

	@Column(name = "numeroserie", length = 256, nullable = false)
	private String numSerie;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechabaja")
	private Date dataBaixa;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechaalta")
	private Date dataAlta;

	@Column(name = "interoperabilidad")
	private boolean interoperabilitat;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "organismo")
	private OrganismeCessionariResourceEntity organisme;

	@Column(name = "per_entitat")
	private boolean perEntitat;

}
