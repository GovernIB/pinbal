/**
 * 
 */
package es.caib.pinbal.core.ejb;

import es.caib.comanda.ms.salut.model.AppInfo;
import es.caib.comanda.ms.salut.model.ContextInfo;
import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.SalutInfo;
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
    public List<AppInfo> getSubsistemes() {
        return delegate.getSubsistemes();
    }

    @Override
    public List<ContextInfo> getContexts(String baseUrl) {
        return delegate.getContexts(baseUrl);
    }

    @Override
    public SalutInfo checkSalut(String versio, String performanceUrl) {
        return delegate.checkSalut(versio, performanceUrl);
    }
}
