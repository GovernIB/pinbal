/**
 * 
 */
package es.caib.pinbal.webapp.command;

import es.caib.pinbal.webapp.validation.UsuariExists;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@UsuariExists
@Getter @Setter
public class UsuariCodiCommand implements Serializable {

	@NotNull
	private String codiAntic;
	@NotNull
	private String codiNou;

}
