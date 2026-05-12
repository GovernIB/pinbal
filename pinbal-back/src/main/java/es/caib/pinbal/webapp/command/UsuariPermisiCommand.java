/**
 * 
 */
package es.caib.pinbal.webapp.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
public class UsuariPermisiCommand implements Serializable {

	@NotEmpty
	private String usuariCodi;
	private String usuariNom;

	private static final long serialVersionUID = -5717352829281579663L;

}
