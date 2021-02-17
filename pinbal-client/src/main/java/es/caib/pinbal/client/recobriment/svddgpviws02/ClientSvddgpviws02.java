package es.caib.pinbal.client.recobriment.svddgpviws02;

import es.caib.pinbal.client.recobriment.ClientBase;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.util.List;

/**
 * Client del recobriment per a fer peticions al servei SCSP SVDDGPVIWS02:
 * "Verificación de datos de identidad" .
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientSvddgpviws02 extends ClientBase {

	private static final String SERVEI_CODI = "SVDDGPVIWS02";

	public ClientSvddgpviws02(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientSvddgpviws02(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public ScspRespuesta peticionSincrona(
			List<SolicitudSvddgpviws02> solicituds) throws IOException {
		return basePeticionSincrona(SERVEI_CODI, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			List<SolicitudSvddgpviws02> solicituds) throws IOException {
		return basePeticionAsincrona(SERVEI_CODI, solicituds);
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	public static class SolicitudSvddgpviws02 extends SolicitudBase {
		private String fecha;
		private String numeroSoporte;
		private String provincia;
		private String pais;

		@Override
		public String getDatosEspecificos() { // xml
			StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			xmlBuilder.append("<DatosEspecificos>");
			if (!isEmptyString(fecha) || !isEmptyString(numeroSoporte) || !isEmptyString(provincia) || !isEmptyString(pais)) {
				xmlBuilder.append("<Consulta>");
				xmlBuilder.append(
						xmlOptionalStringParameter(this.fecha, "Fecha")
				);

				xmlBuilder.append(
						xmlOptionalStringParameter(this.numeroSoporte, "NumeroSoporte")
				);

				xmlBuilder.append(
						xmlOptionalStringParameter(this.provincia, "provincia")
				);

				xmlBuilder.append(
						xmlOptionalStringParameter(this.pais, "País")
				);
				xmlBuilder.append("</Consulta>");
			}
			xmlBuilder.append("</DatosEspecificos>");
			return xmlBuilder.toString();
		}


	}
}
