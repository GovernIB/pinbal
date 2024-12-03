/**
 * 
 */
package es.caib.pinbal.core.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Excepció que es llança quan l'entitat-usuari consultada no existeix.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EntitatUsuariNotFoundException extends Exception {

    private String entitatCodi;
    private String usuariCodi;

    public String getDefaultMessage() {
        return "Entitat-usuari no trobat: " + entitatCodi + " - " + usuariCodi;
    }

}
