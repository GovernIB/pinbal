/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.comanda.ms.estadistica.model.EstadistiquesInfo;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;

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

}
