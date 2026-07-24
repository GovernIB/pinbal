package es.caib.pinbal.logic.intf.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Paràmetres de l'acció "buidarCache" de {@link CacheResource}.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
public class CacheBuidarParams implements Serializable {

	private List<String> ids;

}
