/**
 * 
 */
package es.caib.pinbal.core.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Excepció que es llança quan l'entitat consultada no existeix.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrganNotFoundException extends Exception {

    private String codi;

    public String getDefaultMessage() {
        return "Organ gestor no trobat: " + codi;
    }

}
