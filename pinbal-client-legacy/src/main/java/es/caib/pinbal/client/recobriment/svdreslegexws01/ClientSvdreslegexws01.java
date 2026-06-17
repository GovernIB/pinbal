/**
 * 
 */
package es.caib.pinbal.client.recobriment.svdreslegexws01;

import es.caib.pinbal.client.recobriment.ClientBase;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.util.List;

/**
 * Client del recobriment per a fer peticions al servei SCSP SVDRESLEGEXWS01:
 * "Consulta de datos de residencia legal ciutadans d'origen estranger (MINHAP)"
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientSvdreslegexws01 extends ClientBase {

	private static final String SERVEI_CODI = "SVDRESLEGEXWS01";

	public ClientSvdreslegexws01(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientSvdreslegexws01(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public ScspRespuesta peticionSincrona(
			List<SolicitudSvdreslegexws01> solicituds) throws IOException {
		return basePeticionSincrona(SERVEI_CODI, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			List<SolicitudSvdreslegexws01> solicituds) throws IOException {
		return basePeticionAsincrona(SERVEI_CODI, solicituds);
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class SolicitudSvdreslegexws01 extends SolicitudBase {
		private String anioNacimiento;
		private String nacionalidad;

		@Override
		public String getDatosEspecificos() { // xml
			StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			xmlBuilder.append("<DatosEspecificos>");
			if (!isEmptyString(anioNacimiento) || !isEmptyString(nacionalidad)) {
				xmlBuilder.append("<DatosPeticion>");
				if (!isEmptyString(anioNacimiento))
					xmlBuilder.append(
							xmlOptionalStringParameter(this.anioNacimiento, "AnioNacimiento")
					);
				if (!isEmptyString(nacionalidad))
					xmlBuilder.append(
							xmlOptionalStringParameter(this.nacionalidad, "Nacionalidad")
					);
				xmlBuilder.append("</DatosPeticion>");

			}
			xmlBuilder.append("</DatosEspecificos>");
			return xmlBuilder.toString();
		}
	}

}
