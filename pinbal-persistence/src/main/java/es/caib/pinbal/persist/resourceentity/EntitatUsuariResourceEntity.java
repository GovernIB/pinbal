package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.model.EntitatUsuariResource;
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
 * Entitat de base de dades del recurs {@link EntitatUsuariResource}.
 * <p>
 * Mapeja la mateixa taula que {@link es.caib.pinbal.persist.entity.EntitatUsuari}. Només
 * s'utilitza per a lectura ({@code getOne}/{@code findPage}): la creació i modificació
 * requereixen la lògica de {@code UsuariService} (aprovisionament d'usuaris des del sistema
 * extern), per la qual cosa {@code EntitatUsuariResourceServiceImpl} sobreescriu els mètodes
 * {@code create}/{@code update} complets en lloc dels hooks de persistència genèrica.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "pbl_entitat_usuari")
@Getter
@Setter
@NoArgsConstructor
public class EntitatUsuariResourceEntity extends BaseResourceEntity<EntitatUsuariResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "departament", length = 64)
	private String departament;

	@Column(name = "principal")
	private boolean principal;

	@Column(name = "representant")
	private boolean representant;

	@Column(name = "delegat")
	private boolean delegat;

	@Column(name = "auditor")
	private boolean auditor;

	@Column(name = "aplicacio")
	private boolean aplicacio;

	@Column(name = "actiu")
	private boolean actiu;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	private EntitatResourceEntity entitat;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "usuari_id")
	private UsuariResourceEntity usuari;

	@Version
	private long version = 0;

}
