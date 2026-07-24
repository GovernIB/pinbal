package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.model.EmissorCertResource;
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
 * Entitat de base de dades del recurs {@link EmissorCertResource}.
 * <p>
 * Mapeja la mateixa taula que {@link es.caib.pinbal.persist.entity.EmissorCert}.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "core_emisor_certificado")
@Getter
@Setter
@NoArgsConstructor
public class EmissorCertResourceEntity extends BaseResourceEntity<EmissorCertResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "nombre", length = 50, nullable = false)
	private String nom;

	@Column(name = "cif", length = 16, nullable = false)
	private String cif;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechabaja")
	private Date dataBaixa;

}
