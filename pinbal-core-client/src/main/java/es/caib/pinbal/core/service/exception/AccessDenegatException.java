/**
 * 
 */
package es.caib.pinbal.core.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Excepció que es llança quan l'entitat-servei consultada no existeix.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccessDenegatException extends RuntimeException {

    private List<String> rolsRequerits;

    public String getMessage() {

        return "Accés denegat. Per poder accedir es requereixen els següents rols: " + unirRolsPerComa(rolsRequerits);
    }

    private String unirRolsPerComa(List<String> rols) {
        if (rols == null || rols.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String rol : rols) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(rol);
        }
        return sb.toString();
    }
}
