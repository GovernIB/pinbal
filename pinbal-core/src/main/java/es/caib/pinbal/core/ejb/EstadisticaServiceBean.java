/**
 * 
 */
package es.caib.pinbal.core.ejb;

import es.caib.comanda.ms.estadistica.model.EstadistiquesInfo;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
import es.caib.pinbal.core.service.EstadisticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.Date;
import java.util.List;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat de estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class EstadisticaServiceBean implements EstadisticaService {

	@Autowired
    EstadisticaService delegate;


    @Override
    public EstadistiquesInfo getEstadistiquesInfo() {
        return delegate.getEstadistiquesInfo();
    }

    @Override
    public RegistresEstadistics consultaUltimesEstadistiques() {
        return delegate.consultaUltimesEstadistiques();
    }

    @Override
    public RegistresEstadistics consultaEstadistiques(Date data) {
        return delegate.consultaEstadistiques(data);
    }

    @Override
    public List<RegistresEstadistics> consultaEstadistiques(Date dataInici, Date dataFi) {
        return delegate.consultaEstadistiques(dataInici, dataFi);
    }

    @Override
    @RolesAllowed("PBL_ADMIN")
    public String generarEstadistiques(Date dataInici, Date dataFi) {
        return delegate.generarEstadistiques(dataInici, dataFi);
    }
}
