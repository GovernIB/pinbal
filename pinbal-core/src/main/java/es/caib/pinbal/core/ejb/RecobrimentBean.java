/**
 * 
 */
package es.caib.pinbal.core.ejb;

import java.security.Principal;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.jboss.annotation.security.SecurityDomain;
import org.jboss.wsf.spi.annotation.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.pinbal.core.helper.PluginHelper;
import es.caib.pinbal.core.helper.UsuariHelper;
import es.caib.pinbal.core.ws.Recobriment;
import es.caib.pinbal.plugins.DadesUsuari;
import es.scsp.bean.common.ConfirmacionPeticion;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Respuesta;
import es.scsp.common.exceptions.ScspException;

/**
 * Implementació dels mètodes per al recobriment de les peticions
 * SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "Recobriment",
		serviceName = "RecobrimentService",
		portName = "RecobrimentServicePort",
		targetNamespace = "http://www.caib.es/pinbal/ws/recobriment")
@WebContext(
		contextRoot = "/pinbal/ws",
		urlPattern = "/recobriment",
		authMethod = "WSBASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@RolesAllowed({"PBL_WS"})
@SecurityDomain("seycon")
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class RecobrimentBean implements Recobriment {

	@Resource
	private WebServiceContext webServiceContext;

	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private UsuariHelper usuariHelper;

	@Autowired
	Recobriment delegate;



	@Override
	public Respuesta peticionSincrona(
			Peticion peticion) throws ScspException {
		propagarUsuariAutenticat();
		return delegate.peticionSincrona(peticion);
	}

	@Override
	public ConfirmacionPeticion peticionAsincrona(
			Peticion peticion) throws ScspException {
		propagarUsuariAutenticat();
		return delegate.peticionAsincrona(peticion);
	}

	@Override
	public Respuesta getRespuesta(
			String idpeticion) throws ScspException {
		propagarUsuariAutenticat();
		return delegate.getRespuesta(idpeticion);
	}



	private ScspException getErrorValidacio(
			String codi,
			String missatge) {
		return new ScspException(missatge, codi);
	}

	private void propagarUsuariAutenticat() throws ScspException {
		Principal principal = webServiceContext.getUserPrincipal();
		if (principal != null) {
			DadesUsuari dadesUsuari = null;
			try {
				dadesUsuari = pluginHelper.dadesUsuariConsultarAmbUsuariCodi(principal.getName());
			} catch (Exception ex) {
				LOGGER.error("Error al consultar les dades de l'usuari (codi=" + principal.getName() + ")", ex);
				throw getErrorValidacio(
						"0227",
						"Error al consultar les dades de l'usuari");
			}
			if (dadesUsuari == null) {
				LOGGER.debug("No s'ha trobat l'usuari (codi=" + principal.getName() + ")");
				throw getErrorValidacio(
						"0227",
						"No s'ha trobat l'usuari");
			}
			usuariHelper.generarUsuariAutenticat(
					dadesUsuari,
					true);
		} else {
			throw getErrorValidacio(
					"0227",
					"El principal obtingut del webServiceContext és null");
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RecobrimentBean.class);

}
