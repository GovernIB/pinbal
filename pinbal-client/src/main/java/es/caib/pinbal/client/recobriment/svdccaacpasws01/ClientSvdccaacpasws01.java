/**
 * 
 */
package es.caib.pinbal.client.recobriment.svdccaacpasws01;

import java.io.IOException;
import java.util.List;

import es.caib.pinbal.client.recobriment.ClientBase;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;

/**
 * Client del recobriment per a fer peticions al servei SCSP SVDCCAACPASWS01:
 * "Estar al corriente de obligaciones tributarias para solicitud de
 * subvenciones y ayudas de la CCAA" .
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientSvdccaacpasws01 extends ClientBase {

	public ClientSvdccaacpasws01(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientSvdccaacpasws01(
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
			List<SolicitudSvdccaacpasws01> solicituds) throws IOException {
		return basePeticionSincrona(serveiCodi, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			String serveiCodi,
			List<SolicitudSvdccaacpasws01> solicituds) throws IOException {
		return basePeticionAsincrona(serveiCodi, solicituds);
	}

	public static class SolicitudSvdccaacpasws01 extends SolicitudBase {
		@Override
		public String getDatosEspecificos() {
			return null;
		}
	}

}
