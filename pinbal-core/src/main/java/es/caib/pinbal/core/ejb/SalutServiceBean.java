/**
 * 
 */
package es.caib.pinbal.core.ejb;

import es.caib.comanda.ms.log.helper.LogFileStream;
import es.caib.comanda.ms.log.model.FitxerContingut;
import es.caib.comanda.ms.log.model.FitxerInfo;
import es.caib.comanda.ms.salut.model.ContextInfo;
import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.comanda.ms.salut.model.SubsistemaInfo;
import es.caib.pinbal.core.service.SalutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat de salut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class SalutServiceBean implements SalutService {

	@Autowired
    SalutService delegate;


    @Override
    public List<IntegracioInfo> getIntegracions() {
        return delegate.getIntegracions();
    }

    @Override
    public List<SubsistemaInfo> getSubsistemes() {
        return delegate.getSubsistemes();
    }

    @Override
    public List<ContextInfo> getContexts(String baseUrl) {
        return delegate.getContexts(baseUrl);
    }

    @Override
    public SalutInfo checkSalut(String versio) {
        return delegate.checkSalut(versio);
    }

    @Override
    public List<FitxerInfo> getFitxersLog() {
        return delegate.getFitxersLog();
    }

    @Override
    public FitxerContingut getFitxerLogByNom(String nom) {
        return delegate.getFitxerLogByNom(nom);
    }

    @Override
    public LogFileStream getFitxerLogStream(String nom) {
        return delegate.getFitxerLogStream(nom);
    }

    @Override
    public List<String> getFitxerLogLinies(String nom, Long nLinies) {
        return delegate.getFitxerLogLinies(nom, nLinies);
    }
}
