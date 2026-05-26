/**
 * 
 */
package es.caib.pinbal.ejb;

import es.caib.comanda.model.server.monitoring.*;
import es.caib.comanda.ms.log.helper.LogFileStream;
import org.springframework.context.annotation.Primary;

import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat de salut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Primary
@Stateless
public class SalutServiceBean extends AbstractService<es.caib.pinbal.logic.intf.service.SalutService> implements es.caib.pinbal.logic.intf.service.SalutService {

    @Override
    public List<IntegracioInfo> getIntegracions() {
        return getDelegateService().getIntegracions();
    }

    @Override
    public List<SubsistemaInfo> getSubsistemes() {
        return getDelegateService().getSubsistemes();
    }

    @Override
    public List<ContextInfo> getContexts(String baseUrl) {
        return getDelegateService().getContexts(baseUrl);
    }

    @Override
    public SalutInfo checkSalut(String versio) {
        return getDelegateService().checkSalut(versio);
    }

    @Override
    public List<FitxerInfo> getFitxersLog() {
        return getDelegateService().getFitxersLog();
    }

    @Override
    public FitxerContingut getFitxerLogByNom(String nom) {
        return getDelegateService().getFitxerLogByNom(nom);
    }

    @Override
    public LogFileStream getFitxerLogStream(String nom) {
        return getDelegateService().getFitxerLogStream(nom);
    }

    @Override
    public List<String> getFitxerLogLinies(String nom, Long nLinies) {
        return getDelegateService().getFitxerLogLinies(nom, nLinies);
    }
}
