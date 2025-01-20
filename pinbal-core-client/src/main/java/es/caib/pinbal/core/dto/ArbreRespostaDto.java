/**
 * 
 */
package es.caib.pinbal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Estructura de dades en forma d'arbre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArbreRespostaDto implements Serializable {

	private String titol;
	private String descripcio;
	private String xpath;
	private List<ArbreRespostaDto> fills;

	public void addFill(ArbreRespostaDto fill) {
		if (fills == null) {
			fills = new ArrayList<>();
		}
		fills.add(fill);
	}


	private static final long serialVersionUID = 4533405911014688170L;

}
