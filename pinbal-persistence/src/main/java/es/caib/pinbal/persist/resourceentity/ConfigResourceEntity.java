package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.dto.ConfigSourceEnumDto;
import es.caib.pinbal.logic.intf.model.ConfigResource;
import es.caib.pinbal.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entitat de base de dades del recurs {@link ConfigResource}.
 * <p>
 * Mapeja la mateixa taula que {@link es.caib.pinbal.persist.entity.Config}. Igual que a
 * l'entitat de negoci, {@code typeCode} és de només lectura (columna {@code type_code}, sense
 * relació JPA formal editable): el catàleg de tipus es defineix a les dades inicials i mai es
 * modifica des de l'aplicació.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "PBL_CONFIG")
@Getter
@Setter
@NoArgsConstructor
public class ConfigResourceEntity extends BaseResourceEntity<ConfigResource, String> {

	@Id
	@Column(name = "key", length = 256, nullable = false)
	private String key;

	@Column(name = "value", length = 2048)
	private String value;

	@Column(name = "description_key", length = 2048)
	private String descriptionKey;

	@Enumerated(EnumType.STRING)
	@Column(name = "source_property", nullable = false)
	private ConfigSourceEnumDto sourceProperty;

	@Column(name = "group_code", length = 2048)
	private String groupCode;

	@Column(name = "type_code", insertable = false, updatable = false)
	private String typeCode;

	@Column(name = "position")
	private int position;

	@Override
	public String getId() {
		return key;
	}

	@Override
	public void setId(String id) {
		this.key = id;
	}

	public boolean isEditable() {
		return sourceProperty == ConfigSourceEnumDto.DATABASE;
	}

}
