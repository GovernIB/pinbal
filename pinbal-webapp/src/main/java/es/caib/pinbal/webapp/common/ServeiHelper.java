/**
 *
 */
package es.caib.pinbal.webapp.common;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;

/**
 * Utilitat per a gestionar els serveis disponibles o permesos
 * per a l'usuari actual.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiHelper {
    public static final String REQUEST_ATTRIBUTE_SERVEIS = "serveis";

    public static List<ServeiDto> getServeis(HttpServletRequest request) {
        // Atribute set in interceptor
        if (request.getAttribute(REQUEST_ATTRIBUTE_SERVEIS) != null) {
            return (List<ServeiDto>) request.getAttribute(REQUEST_ATTRIBUTE_SERVEIS);
        } else {
            return null;
        }
    }
}
