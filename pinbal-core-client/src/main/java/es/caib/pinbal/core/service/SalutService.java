/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.comanda.ms.salut.model.AppInfo;
import es.caib.comanda.ms.salut.model.ContextInfo;
import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.SalutInfo;

import java.util.List;

/**
 * Declaració dels mètodes comuns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SalutService {

    public List<IntegracioInfo> getIntegracions();
    public List<AppInfo> getSubsistemes();
    public List<ContextInfo> getContexts(String baseUrl);
    public SalutInfo checkSalut(String versio);

}
