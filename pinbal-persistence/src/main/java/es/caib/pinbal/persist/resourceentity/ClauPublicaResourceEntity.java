package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.model.ClauPublicaResource;
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
 * Entitat de base de dades del recurs {@link ClauPublicaResource}.
 * <p>
 * Mapeja la mateixa taula que {@link es.caib.pinbal.persist.entity.ClauPublica}.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "core_clave_publica")
@Getter
@Setter
@NoArgsConstructor
public class ClauPublicaResourceEntity extends BaseResourceEntity<ClauPublicaResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "nombre", length = 256, nullable = false)
	private String nom;

	@Column(name = "alias", length = 256, nullable = false)
	private String alies;

	@Column(name = "numeroserie", length = 256, nullable = false)
	private String numSerie;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechaalta", nullable = false)
	private Date dataAlta;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechabaja")
	private Date dataBaixa;

}
