/**
 * 
 */
package es.caib.pinbal.client.recobriment.svddgpresidencialegaldocws01;

import es.caib.pinbal.client.recobriment.ClientBase;
import es.caib.pinbal.client.recobriment.model.MissingCampObligatoriException;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Client del recobriment per a fer peticions al servei SCSP SVDDGPRESIDENCIALEGALDOCWS01:
 * "Servicio de consulta de datos de residencia legal de extranjeros por documentación"
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientSvddgpresidencialegaldocws01 extends ClientBase {

	private static final String SERVEI_CODI = "SVDDGPRESIDENCIALEGALDOCWS01";

	public ClientSvddgpresidencialegaldocws01(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientSvddgpresidencialegaldocws01(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public ScspRespuesta peticionSincrona(
			List<SolicitudSvddgpresidencialegaldocws01> solicituds) throws IOException {
		return basePeticionSincrona(SERVEI_CODI, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			List<SolicitudSvddgpresidencialegaldocws01> solicituds) throws IOException {
		return basePeticionAsincrona(SERVEI_CODI, solicituds);
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class SolicitudSvddgpresidencialegaldocws01 extends SolicitudBase {

		public enum TipusPassaport {
			AR,
			AS,
			CE,
			DN,
			IN,
			LN,
			OT,
			PA,
			PD,
			SC,
			TA,
			TD,
			TU,
			TV
		}

		private String numeroSoporte;
		private TipusPassaport tipo;
		private String nacionalidad;
		private Date fechaExpedicion;
		private Date fechaCaducidad;

		public void setPassaport(
				TipusPassaport tipo,
				String nacionalidad,
				Date fechaExpedicion,
				Date fechaCaducidad) {
			this.tipo = tipo;
			this.nacionalidad = nacionalidad;
			this.fechaExpedicion = fechaExpedicion;
			this.fechaCaducidad = fechaCaducidad;
		}

		@Override
		public String getDatosEspecificos() { // xml

			if (!isEmptyString(numeroSoporte) || tipo != null || !isEmptyString(nacionalidad) || fechaExpedicion != null || fechaCaducidad != null) {
				if (isEmptyString(numeroSoporte) && tipo == null) {
					throw new MissingCampObligatoriException("numeroSoporte", "El camp numeroSoporte, o el passaport han d'estar informats.");
				}

				StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				xmlBuilder.append("<DatosEspecificos>");
				xmlBuilder.append("<Consulta>");

				xmlBuilder.append(xmlOptionalStringParameter(this.numeroSoporte, "NumeroSoporte"));
				if (tipo != null || !isEmptyString(nacionalidad) || fechaExpedicion != null || fechaCaducidad != null) {
					xmlBuilder.append("<Pasaporte>");
					if (tipo != null)
						xmlBuilder.append(xmlOptionalStringParameter(this.tipo.name(), "Sexo"));
					xmlBuilder.append(xmlOptionalStringParameter(this.nacionalidad, "Nacionalidad"));
					xmlBuilder.append(xmlOptionalDateParameter(this.fechaExpedicion, "FechaExpedicion"));
					xmlBuilder.append(xmlOptionalDateParameter(this.fechaCaducidad, "FechaCaducidad"));
					xmlBuilder.append("</Pasaporte>");
				}

				xmlBuilder.append("</Consulta>");
				xmlBuilder.append("</DatosEspecificos>");
				return xmlBuilder.toString();
			}

			return null;
		}
	}

}
