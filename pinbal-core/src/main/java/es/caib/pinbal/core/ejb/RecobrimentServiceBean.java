/**
 * 
 */
package es.caib.pinbal.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.core.service.RecobrimentService;
import es.caib.pinbal.core.service.exception.RecobrimentScspException;

/**
 * Implementació de RecobrimentService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class RecobrimentServiceBean implements RecobrimentService {

	@Autowired
	RecobrimentService delegate;

	@Override
	@RolesAllowed("PBL_WS")
	public ScspRespuesta peticionSincrona(
			ScspPeticion peticion) throws RecobrimentScspException {
		return delegate.peticionSincrona(peticion);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public ScspConfirmacionPeticion peticionAsincrona(
			ScspPeticion peticion) throws RecobrimentScspException {
		return delegate.peticionAsincrona(peticion);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public ScspRespuesta getRespuesta(
			String idPeticion) throws RecobrimentScspException {
		return delegate.getRespuesta(idPeticion);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public ScspJustificante getJustificante(
			String idPeticion,
			String idSolicitud) throws RecobrimentScspException {
		return delegate.getJustificante(idPeticion, idSolicitud);
	}

}