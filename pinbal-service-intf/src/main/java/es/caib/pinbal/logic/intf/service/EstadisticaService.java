/**
 * 
 */
package es.caib.pinbal.logic.intf.service;

import es.caib.comanda.model.server.monitoring.EstadistiquesInfo;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Date;
import java.util.List;

/**
 * Declaració dels mètodes comuns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EstadisticaService {

    EstadistiquesInfo getEstadistiquesInfo();
    RegistresEstadistics consultaUltimesEstadistiques();
    RegistresEstadistics consultaEstadistiques(Date data);
    List<RegistresEstadistics> consultaEstadistiques(Date dataInici, Date dataFi);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    String generarEstadistiques(Date dataInici, Date dataFi);
}
