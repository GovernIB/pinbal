package es.caib.pinbal.ejb;

import es.caib.pinbal.logic.intf.dto.*;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Implementació de IntegracioAccioService com a EJB que empra una clase delegada
 * per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class IntegracioAccioService extends AbstractService<es.caib.pinbal.logic.intf.service.IntegracioAccioService> implements es.caib.pinbal.logic.intf.service.IntegracioAccioService {

	@Override
	@RolesAllowed({ "PBL_ADMIN" })
	public IntegracioAccioDto create(IntegracioAccioDto integracioAccio) {
		return getDelegateService().create(integracioAccio);
	}
	
	@Override
	@RolesAllowed({ "PBL_ADMIN" })
	public PaginaDto<IntegracioAccioDto> findPaginat(PaginacioAmbOrdreDto paginacioParams, IntegracioFiltreDto integracioFiltreDto) {
		return getDelegateService().findPaginat(paginacioParams, integracioFiltreDto);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN" })
	public int delete(String codi) {
		return getDelegateService().delete(codi);
	}
	
	@Override
	public int deleteAll() {
		return getDelegateService().deleteAll();
	}
	
	@Override
	public int esborrarDadesAntigues(Date data) {
		return getDelegateService().esborrarDadesAntigues(data);
	}
	
	@Override
	public void esborrarDadesAntigesMonitorIntegracio() {
		getDelegateService().esborrarDadesAntigesMonitorIntegracio();
	}
	
	@Override
	public Map<String, Integer> countErrors(int numeroHores) {
		return getDelegateService().countErrors(numeroHores);
	}
	
	@Override
	public List<IntegracioDto> integracioFindAll() {
		return getDelegateService().integracioFindAll();
	}

    @Override
	@RolesAllowed({ "PBL_ADMIN" })
    public List<IntegracioDto> getAll() {
        return getDelegateService().getAll();
    }

	@Override
	@RolesAllowed({ "PBL_ADMIN" })
	public IntegracioAccioDto findById(Long id) {
		return getDelegateService().findById(id);
	}
}
