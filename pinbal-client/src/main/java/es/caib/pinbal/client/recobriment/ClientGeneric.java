/**
 * 
 */
package es.caib.pinbal.client.recobriment;

import java.io.IOException;
import java.util.List;

import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.Solicitud;

/**
 * Client genèric pel recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientGeneric extends ClientBase {

	public ClientGeneric(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientGeneric(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public ScspRespuesta peticionSincrona(
			String serveiCodi,
			List<Solicitud> solicituds) throws IOException {
		return basePeticionSincrona(serveiCodi, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			String serveiCodi,
			List<Solicitud> solicituds) throws IOException {
		return basePeticionAsincrona(serveiCodi, solicituds);
	}

}
