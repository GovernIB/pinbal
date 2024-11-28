/**
 * 
 */
package es.caib.pinbal.core.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Excepció que es llança quan el servei consultat no existeix.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServeiNotFoundException extends Exception {

    private String codi;

    public String getDefaultMessage() {
        return "Servei no trobat: " + codi;
    }

}
