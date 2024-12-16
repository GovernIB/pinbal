/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
public class ProcedimentServeiCommand implements Serializable {

	@NotEmpty
	private String serveiCodi;
	private String serveiNom;

	private static final long serialVersionUID = -5717352829281579663L;

}
