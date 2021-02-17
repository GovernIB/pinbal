package es.caib.pinbal.client.recobriment.svddgpciws02;

import es.caib.pinbal.client.recobriment.ClientBase;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.util.List;

/**
 * Client del recobriment per a fer peticions al servei SCSP SVDDGPCIWS02:
 * "Consulta de datos de identidad" .
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientSvddgpciws02 extends ClientBase {

	private static final String SERVEI_CODI = "SVDDGPCIWS02";

	public ClientSvddgpciws02(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientSvddgpciws02(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public ScspRespuesta peticionSincrona(
			List<SolicitudSvddgpciws02> solicituds) throws IOException {
		return basePeticionSincrona(SERVEI_CODI, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			List<SolicitudSvddgpciws02> solicituds) throws IOException {
		return basePeticionAsincrona(SERVEI_CODI, solicituds);
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	public static class SolicitudSvddgpciws02 extends SolicitudBase {
		private String numeroSoporte;

		@Override
		public String getDatosEspecificos() { // xml
			StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			xmlBuilder.append("<DatosEspecificos>");
			if (numeroSoporte != null && !numeroSoporte.isEmpty()) {
				xmlBuilder.append("<Consulta>");
				xmlBuilder.append(
						xmlOptionalStringParameter(this.numeroSoporte, "NumeroSoporte")
				);
				xmlBuilder.append("</Consulta>");
			}
			xmlBuilder.append("</DatosEspecificos>");
			return xmlBuilder.toString();
		}
	}
}
