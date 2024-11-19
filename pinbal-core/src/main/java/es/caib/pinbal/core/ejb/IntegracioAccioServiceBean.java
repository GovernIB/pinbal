package es.caib.pinbal.core.ejb;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.pinbal.core.dto.IntegracioAccioDto;
import es.caib.pinbal.core.dto.IntegracioDto;
import es.caib.pinbal.core.dto.IntegracioFiltreDto;
import es.caib.pinbal.core.dto.PaginaDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.service.IntegracioAccioService;


/**
 * Implementació de IntegracioAccioService com a EJB que empra una clase delegada
 * per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class IntegracioAccioServiceBean implements IntegracioAccioService {

	@Autowired
	IntegracioAccioService delegate;

	@Override
	@RolesAllowed({ "PBL_ADMIN" })
	public List<IntegracioAccioDto> findAll() {
		return delegate.findAll();
	}
	
	@Override
	@RolesAllowed({ "PBL_ADMIN" })
	public IntegracioAccioDto create(IntegracioAccioDto integracioAccio) {
		return delegate.create(integracioAccio);
	}
	
	@Override
	@RolesAllowed({ "PBL_ADMIN" })
	public PaginaDto<IntegracioAccioDto> findPaginat(PaginacioAmbOrdreDto paginacioParams, IntegracioFiltreDto integracioFiltreDto) {
		return delegate.findPaginat(paginacioParams, integracioFiltreDto);
	}

	@Override
	@RolesAllowed({ "PBL_ADMIN" })
	public int delete(String codi) {
		return delegate.delete(codi);
	}
	
	@Override
	public int deleteAll() {
		return delegate.deleteAll();
	}
	
	@Override
	public int esborrarDadesAntigues(Date data) {
		return delegate.esborrarDadesAntigues(data);
	}
	
	@Override
	public void esborrarDadesAntigesMonitorIntegracio() {
		delegate.esborrarDadesAntigesMonitorIntegracio();
	}
	
	@Override
	public Map<String, Integer> countErrors(int numeroHores) {
		return delegate.countErrors(numeroHores);
	}
	
	@Override
	public List<IntegracioDto> integracioFindAll() {
		return delegate.integracioFindAll();
	}
}
