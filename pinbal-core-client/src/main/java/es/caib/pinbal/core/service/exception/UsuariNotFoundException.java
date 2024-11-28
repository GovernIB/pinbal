/**
 * 
 */
package es.caib.pinbal.core.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Excepció que es llança quan s'intenta crear un usuari
 * que no existeix al sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsuariNotFoundException extends Exception {

    private String codi;

    public String getDefaultMessage() {
        return "Usuari no trobat: " + codi;
    }

}
