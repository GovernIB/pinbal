/**
 * 
 */
package es.caib.pinbal.core.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Excepció que es llança quan el servei consultat no te configurat el node de resposta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServeiRespostaNotFoundException extends Exception {

    private String codi;

    public String getDefaultMessage() {
        return "El servei " + codi + " no te configurat el node de resposta correctament";
    }

}
