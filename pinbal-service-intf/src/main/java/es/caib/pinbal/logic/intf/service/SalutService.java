/**
 * 
 */
package es.caib.pinbal.logic.intf.service;

import es.caib.comanda.model.server.monitoring.*;
import es.caib.comanda.ms.log.helper.LogFileStream;

import java.util.List;

/**
 * Declaració dels mètodes comuns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SalutService {

    public List<IntegracioInfo> getIntegracions();
    public List<SubsistemaInfo> getSubsistemes();
    public List<ContextInfo> getContexts(String baseUrl);
    public SalutInfo checkSalut(String versio);

    List<FitxerInfo> getFitxersLog();
    FitxerContingut getFitxerLogByNom(String nom);
    LogFileStream getFitxerLogStream(String nom);
    List<String> getFitxerLogLinies(String nom, Long nLinies);
}
