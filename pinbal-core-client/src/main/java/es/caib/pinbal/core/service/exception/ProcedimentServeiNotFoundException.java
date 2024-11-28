/**
 * 
 */
package es.caib.pinbal.core.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Excepció que es llança quan l'entitat-servei consultada no existeix.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProcedimentServeiNotFoundException extends Exception {

    private String procedimentCodi;
    private String serveiCodi;

    public String getDefaultMessage() {
        return "Procediment-servei no trobat: " + procedimentCodi + " - " + serveiCodi;
    }

}
