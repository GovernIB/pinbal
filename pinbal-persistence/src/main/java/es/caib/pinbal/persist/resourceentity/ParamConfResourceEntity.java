package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.model.ParamConfResource;
import es.caib.pinbal.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entitat de base de dades del recurs {@link ParamConfResource}.
 * <p>
 * Mapeja la mateixa taula que {@link es.caib.pinbal.persist.entity.ParamConf} (compartida amb la llibreria scsp-core).
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "core_parametro_configuracion")
@Getter
@Setter
@NoArgsConstructor
public class ParamConfResourceEntity extends BaseResourceEntity<ParamConfResource, String> {

	@Id
	@Column(name = "nombre", length = 64, nullable = false)
	private String nom;

	@Column(name = "valor", length = 512, nullable = false)
	private String valor;

	@Column(name = "descripcion", length = 512)
	private String descripcio;

	@Override
	public String getId() {
		return nom;
	}

	@Override
	public void setId(String id) {
		this.nom = id;
	}

}
