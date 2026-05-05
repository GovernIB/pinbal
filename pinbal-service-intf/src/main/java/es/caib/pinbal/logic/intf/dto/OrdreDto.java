/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import lombok.*;

import java.io.Serializable;

/**
 * Informació per a ordenar una consulta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrdreDto implements Serializable {

	public enum OrdreDireccio {
		ASCENDENT,
		DESCENDENT
	}

	private String camp;
	private OrdreDireccio direccio;

	private static final long serialVersionUID = -139254994389509932L;

}
