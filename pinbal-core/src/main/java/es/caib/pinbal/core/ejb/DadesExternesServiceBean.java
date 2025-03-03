/**
 * 
 */
package es.caib.pinbal.core.ejb;

import es.caib.pinbal.core.dto.IdiomaEnumDto;
import es.caib.pinbal.core.dto.dadesexternes.Municipi;
import es.caib.pinbal.core.dto.dadesexternes.Pais;
import es.caib.pinbal.core.dto.dadesexternes.Provincia;
import es.caib.pinbal.core.service.DadesExternesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * Implementació de DadesExternesService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DadesExternesServiceBean implements DadesExternesService {

	@Autowired
	DadesExternesService delegate;



	@Override
	@RolesAllowed("tothom")
	public List<Provincia> findProvincies(IdiomaEnumDto idioma) {
		return delegate.findProvincies(idioma);
	}

	@Override
	@RolesAllowed("tothom")
	public List<Municipi> findMunicipisPerProvincia(String provinciaCodi) {
		return delegate.findMunicipisPerProvincia(provinciaCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public List<Pais> findPaisos(IdiomaEnumDto idioma) {
		return delegate.findPaisos(idioma);
	}

}
