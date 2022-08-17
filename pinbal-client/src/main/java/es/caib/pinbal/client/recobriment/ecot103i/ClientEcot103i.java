/**
 * 
 */
package es.caib.pinbal.client.recobriment.ecot103i;

import es.caib.pinbal.client.recobriment.ClientBase;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.util.List;

/**
 * Client del recobriment per a fer peticions al servei SCSP ECOT103I:
 * "Estar al corriente de obligaciones tributarias para solicitud de subvenciones y ayudas con indicación de incumplimientos"
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientEcot103i extends ClientBase {

	private static final String SERVEI_CODI = "ECOT103I";

	public ClientEcot103i(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientEcot103i(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public ScspRespuesta peticionSincrona(
			List<SolicitudEcot103i> solicituds) throws IOException {
		return basePeticionSincrona(SERVEI_CODI, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			List<SolicitudEcot103i> solicituds) throws IOException {
		return basePeticionAsincrona(SERVEI_CODI, solicituds);
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class SolicitudEcot103i extends SolicitudBase {

		@Override
		public String getDatosEspecificos() {
			return null;
		}
	}

}
