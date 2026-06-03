/**
 *
 */
package es.caib.pinbal.api.interna.ws;

import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.logic.intf.service.RecobrimentService;
import es.caib.pinbal.logic.intf.service.exception.RecobrimentScspException;
import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.common.exceptions.ScspException;
import lombok.RequiredArgsConstructor;
import org.jboss.ws.api.annotation.WebContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import java.security.Principal;

/**
 * Implementacio dels metodes per al recobriment de les peticions SCSP exposats
 * per SOAP. El bean viu a l'API interna i delega en el mateix RecobrimentService
 * que els controladors REST.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@Component("recobrimentSoapBean")
@Stateless
@WebService(
		name = "Recobriment",
		serviceName = "RecobrimentService",
		portName = "RecobrimentServicePort",
		endpointInterface = "es.caib.pinbal.logic.intf.ws.Recobriment",
		targetNamespace = "http://www.caib.es/pinbal/ws/recobriment")
@WebContext(
		contextRoot = "/pinbal/ws",
		urlPattern = "/recobriment",
		authMethod = "WSBASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@RolesAllowed({"PBL_WS"})
public class RecobrimentBean implements es.caib.pinbal.logic.intf.ws.Recobriment {

	private static final String ERROR_CODE_SECURITY = "0227";

	private final RecobrimentService recobrimentService;
	private final WebServiceContext webServiceContext;

	private final RecobrimentSoapMapper mapper = new RecobrimentSoapMapper();

	@Override
	public Respuesta peticionSincrona(Peticion peticion) throws ScspException {
		checkPermission();
		try {
			return mapper.toRespuesta(
					recobrimentService.peticionSincrona(mapper.toScspPeticion(peticion)));
		} catch (Exception ex) {
			throw toScspException(ex);
		}
	}

	@Override
	public ConfirmacionPeticion peticionAsincrona(Peticion peticion) throws ScspException {
		checkPermission();
		try {
			return mapper.toConfirmacionPeticion(
					recobrimentService.peticionAsincrona(mapper.toScspPeticion(peticion)));
		} catch (Exception ex) {
			throw toScspException(ex);
		}
	}

	@Override
	public Respuesta getRespuesta(String idpeticion) throws ScspException {
		checkPermission();
		try {
			return mapper.toRespuesta(recobrimentService.getRespuesta(idpeticion));
		} catch (Exception ex) {
			throw toScspException(ex);
		}
	}

	@Override
	public byte[] getJustificante(String idpeticion, String idsolicitud) throws ScspException {
		checkPermission();
		try {
			ScspJustificante justificante = recobrimentService.getJustificante(idpeticion, idsolicitud);
			return justificante != null ? justificante.getContingut() : null;
		} catch (RecobrimentScspException ex) {
			throw toScspException(ex);
		}
	}

	private void checkPermission() throws ScspException {
		if (hasWebServiceRole() || hasSpringSecurityRole()) {
			return;
		}
		throw new ScspException("L'usuari no te permisos per accedir al servei de recobriment", ERROR_CODE_SECURITY);
	}

	private boolean hasWebServiceRole() {
		if (webServiceContext == null) {
			return false;
		}
		try {
			Principal principal = webServiceContext.getUserPrincipal();
			return principal != null
					&& (webServiceContext.isUserInRole("PBL_WS")
					|| webServiceContext.isUserInRole("ROLE_WS")
					|| webServiceContext.isUserInRole("ROLE_PBL_WS"));
		} catch (IllegalStateException ex) {
			return false;
		}
	}

	private boolean hasSpringSecurityRole() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return false;
		}
		return authentication.getAuthorities().stream()
				.anyMatch(authority -> "PBL_WS".equals(authority.getAuthority())
						|| "ROLE_WS".equals(authority.getAuthority())
						|| "ROLE_PBL_WS".equals(authority.getAuthority()));
	}

	private ScspException toScspException(Exception ex) {
		Throwable current = ex;
		while (current != null) {
			if (current instanceof ScspException) {
				return (ScspException) current;
			}
			current = current.getCause();
		}
		return new ScspException(ex.getMessage(), ERROR_CODE_SECURITY);
	}

}
