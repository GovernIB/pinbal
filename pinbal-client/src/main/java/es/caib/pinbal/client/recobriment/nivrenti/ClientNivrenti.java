/**
 * 
 */
package es.caib.pinbal.client.recobriment.nivrenti;

import es.caib.pinbal.client.recobriment.ClientBase;
import es.caib.pinbal.client.recobriment.model.MissingCampObligatoriException;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

/**
 * Client del recobriment per a fer peticions al servei SCSP NIVRENTI:
 * "Consulta del nivel de renta"
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientNivrenti extends ClientBase {

	private static final String SERVEI_CODI = "NIVRENTI";

	public ClientNivrenti(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientNivrenti(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public ScspRespuesta peticionSincrona(
			List<SolicitudNivrenti> solicituds) throws IOException {
		return basePeticionSincrona(SERVEI_CODI, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			List<SolicitudNivrenti> solicituds) throws IOException {
		return basePeticionAsincrona(SERVEI_CODI, solicituds);
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class SolicitudNivrenti extends SolicitudBase {

		private Integer ejercicio;

		@Override
		public String getDatosEspecificos() { // xml

			if (ejercicio == null) {
				throw new MissingCampObligatoriException("Ejercicio");
			}

			StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			xmlBuilder.append("<DatosEspecificos>");
			xmlBuilder.append(xmlOptionalStringParameter(this.ejercicio.toString(), "Ejercicio"));
			xmlBuilder.append("</DatosEspecificos>");
			return xmlBuilder.toString();
		}
	}

}
