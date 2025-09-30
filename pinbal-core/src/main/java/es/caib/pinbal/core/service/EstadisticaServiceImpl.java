/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.comanda.ms.estadistica.model.EstadistiquesInfo;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Implementació dels mètodes per a gestionar l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class EstadisticaServiceImpl implements EstadisticaService {

//	@Resource
//	private IntegracioHelper integracioHelper;
//	@Resource
//	private CacheHelper cacheHelper;
//	@Resource
//	private PaginacioHelper paginacioHelper;

    @Override
    public EstadistiquesInfo getEstadistiquesInfo() {
        return null;
    }

    @Override
    public RegistresEstadistics consultaUltimesEstadistiques() {
        return null;
    }

    @Override
    public RegistresEstadistics consultaEstadistiques(Date data) {
        return null;
    }

    @Override
    public List<RegistresEstadistics> consultaEstadistiques(Date dataInici, Date dataFi) {
        return Collections.emptyList();
    }

}
