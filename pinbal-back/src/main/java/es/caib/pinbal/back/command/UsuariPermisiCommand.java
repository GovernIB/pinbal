/**
 * 
 */
package es.caib.pinbal.back.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuariPermisiCommand implements Serializable {

	@NotEmpty
	private String usuariCodi;
	private String usuariNom;

	private static final long serialVersionUID = -5717352829281579663L;

}
