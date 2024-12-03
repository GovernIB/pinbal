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
public class EntitatServeiNotFoundException extends Exception {

    private String entitatCodi;
    private String serveiCodi;

    public String getDefaultMessage() {
        return "Entitat-servei no trobada: " + entitatCodi + " - " + serveiCodi;
    }

}
