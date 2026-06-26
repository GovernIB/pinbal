/**
 * 
 */
package es.caib.pinbal.ejb;

import es.caib.comanda.model.server.monitoring.EstadistiquesInfo;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.Date;
import java.util.List;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat de estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Primary
@Stateless
@RolesAllowed({"PBL_COM", "PBL_ADMIN"})
public class EstadisticaService extends AbstractService<es.caib.pinbal.logic.intf.service.EstadisticaService> implements es.caib.pinbal.logic.intf.service.EstadisticaService {

    @Override
    public EstadistiquesInfo getEstadistiquesInfo() {
        return getDelegateService().getEstadistiquesInfo();
    }

    @Override
    public RegistresEstadistics consultaUltimesEstadistiques() {
        return getDelegateService().consultaUltimesEstadistiques();
    }

    @Override
    public RegistresEstadistics consultaEstadistiques(Date data) {
        return getDelegateService().consultaEstadistiques(data);
    }

    @Override
    public List<RegistresEstadistics> consultaEstadistiques(Date dataInici, Date dataFi) {
        return getDelegateService().consultaEstadistiques(dataInici, dataFi);
    }

    @Override
    @RolesAllowed("PBL_ADMIN")
    public String generarEstadistiques(Date dataInici, Date dataFi) {
        return getDelegateService().generarEstadistiques(dataInici, dataFi);
    }
}
