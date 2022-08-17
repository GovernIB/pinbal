package es.caib.pinbal.client.recobriment.svdsctfnws01;

import es.caib.pinbal.client.recobriment.ClientBase;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.util.List;

/**
 * Client del recobriment per a fer peticions al servei SCSP SVDSCTFNWS01:
 * "Consulta de los datos de familia numerosa" .
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientSvdsctfnws01 extends ClientBase {

	private static final String SERVEI_CODI = "SVDSCTFNWS01";

	public ClientSvdsctfnws01(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientSvdsctfnws01(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public ScspRespuesta peticionSincrona(
			List<SolicitudSvdsctfnws01> solicituds) throws IOException {
		return basePeticionSincrona(SERVEI_CODI, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			List<SolicitudSvdsctfnws01> solicituds) throws IOException {
		return basePeticionAsincrona(SERVEI_CODI, solicituds);
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	public static class SolicitudSvdsctfnws01 extends SolicitudBase {
		private String codigoComunidadAutonoma;
		private String numeroTitulo;
		/**
		 * Format dd/mm/yyyy
		 */
		private String fechaConsulta;
		/**
		 * Format dd/mm/yyyy
		 */
		private String fechaNacimiento;
		@Override
		public String getDatosEspecificos() {
			StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			xmlBuilder.append("<DatosEspecificos>");
			if (!isEmptyString(codigoComunidadAutonoma) || !isEmptyString(numeroTitulo) || !isEmptyString(fechaConsulta) || !isEmptyString(fechaNacimiento)) {
				xmlBuilder.append("<Consulta>");
				if (!isEmptyString(codigoComunidadAutonoma) || !isEmptyString(numeroTitulo) || !isEmptyString(fechaConsulta)) {
					xmlBuilder.append("<TituloFamiliaNumerosa>");
					xmlBuilder.append(
							xmlOptionalStringParameter(codigoComunidadAutonoma, "CodigoComunidadAutonoma")
					);
					xmlBuilder.append(
							xmlOptionalStringParameter(numeroTitulo, "NumeroTitulo")
					);
					xmlBuilder.append(
							xmlOptionalStringParameter(fechaConsulta, "FechaConsulta")
					);
					xmlBuilder.append("</TituloFamiliaNumerosa>");
				}
				if (!isEmptyString(fechaNacimiento)) {
					xmlBuilder.append("<DatosAdicionalesTitular>");
					xmlBuilder.append(
							xmlOptionalStringParameter(this.fechaNacimiento, "FechaNacimiento")
					);
					xmlBuilder.append("</DatosAdicionalesTitular>");
				}
				xmlBuilder.append("</Consulta>");
			}
			xmlBuilder.append("</DatosEspecificos>");
			return xmlBuilder.toString();
		}

	}
}
