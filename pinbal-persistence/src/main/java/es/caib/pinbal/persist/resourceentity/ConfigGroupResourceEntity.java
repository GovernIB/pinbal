package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.model.ConfigGroupResource;
import es.caib.pinbal.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

/**
 * Entitat de base de dades del recurs {@link ConfigGroupResource}.
 * <p>
 * Mapeja la mateixa taula que {@link es.caib.pinbal.persist.entity.ConfigGroup}. La relació amb
 * el grup pare ({@code parentCode}) és una simple columna de text a l'entitat de negoci (no una
 * FK JPA formal), així que es manté igual aquí i el frontend construeix l'arbre filtrant per
 * aquest camp de text. {@code configs}/{@code innerConfigs} sí que es mapegen com a relacions
 * (unidireccionals, igual que a l'entitat de negoci) perquè el filtre genèric de l'aplicació
 * (spring filter -&gt; {@code Specification}) pugui fer cerques del tipus
 * {@code exists(configs.key~'...')} des del frontend (equivalent al cercador de Notib).
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "PBL_CONFIG_GROUP")
@Getter
@Setter
@NoArgsConstructor
public class ConfigGroupResourceEntity extends BaseResourceEntity<ConfigGroupResource, String> {

	@Id
	@Column(name = "code", length = 128, nullable = false)
	private String key;

	@Column(name = "description_key", length = 512)
	private String descriptionKey;

	@Column(name = "position")
	private int position;

	@Column(name = "parent_code")
	private String parentCode;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_code")
	private Set<ConfigResourceEntity> configs;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_code")
	private Set<ConfigGroupResourceEntity> innerConfigs;

	@Override
	public String getId() {
		return key;
	}

	@Override
	public void setId(String id) {
		this.key = id;
	}

}
