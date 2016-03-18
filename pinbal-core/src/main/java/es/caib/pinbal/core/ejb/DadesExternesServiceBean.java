/**
 * 
 */
package es.caib.pinbal.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.pinbal.core.service.DadesExternesService;

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
	public byte[] findProvincies() {
		return delegate.findProvincies();
	}

	@Override
	@RolesAllowed("tothom")
	public byte[] findMunicipisPerProvincia(String provinciaCodi) {
		return delegate.findMunicipisPerProvincia(provinciaCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public byte[] findPaisos() {
		return delegate.findPaisos();
	}

}
