/**
 * 
 */
package es.caib.pinbal.back.command;

import es.caib.pinbal.back.validation.UsuariExists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
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
@UsuariExists
public class UsuariCodiCommand implements Serializable {

	@NotNull
	private String codiAntic;
	private String codiNou;

	private String nom;
	private String nif;
	private String email;
	private String idioma;

}
