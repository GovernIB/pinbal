package es.caib.pinbal.persist.resourceentity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Lectura interna (no exposada com a recurs REST propi) del catàleg de tipus de propietat de
 * configuració, usada per {@code ConfigResourceServiceImpl} per resoldre els valors possibles
 * ({@code validValues}) d'una propietat concreta. Mapeja la mateixa taula que
 * {@link es.caib.pinbal.persist.entity.ConfigType}.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "PBL_CONFIG_TYPE")
@Getter
@NoArgsConstructor
public class ConfigTypeResourceEntity {

	@Id
	@Column(name = "code", length = 128, nullable = false)
	private String code;

	@Column(name = "value", length = 2048, nullable = false)
	private String value;

	public List<String> getValidValues() {
		if (value == null || value.isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.asList(value.split(","));
	}

}
