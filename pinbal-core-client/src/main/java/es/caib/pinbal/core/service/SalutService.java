/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.comanda.ms.log.helper.LogFileStream;
import es.caib.comanda.ms.log.model.FitxerContingut;
import es.caib.comanda.ms.log.model.FitxerInfo;
import es.caib.comanda.ms.salut.model.ContextInfo;
import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.comanda.ms.salut.model.SubsistemaInfo;

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
