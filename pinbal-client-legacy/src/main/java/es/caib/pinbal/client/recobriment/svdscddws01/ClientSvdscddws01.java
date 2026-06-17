package es.caib.pinbal.client.recobriment.svdscddws01;

import es.caib.pinbal.client.recobriment.ClientBase;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.util.List;

/**
 * Client del recobriment per a fer peticions al servei SCSP SVDSCDDWS01:
 * "Consulta de los datos de discapacidad" .
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientSvdscddws01 extends ClientBase {

	private static final String SERVEI_CODI = "SVDSCDDWS01";

	public ClientSvdscddws01(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientSvdscddws01(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public ScspRespuesta peticionSincrona(
			List<SolicitudSvdscddws01> solicituds) throws IOException {
		return basePeticionSincrona(SERVEI_CODI, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			List<SolicitudSvdscddws01> solicituds) throws IOException {
		return basePeticionAsincrona(SERVEI_CODI, solicituds);
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	public static class SolicitudSvdscddws01 extends SolicitudBase {
		private String codigoComunidadAutonoma;
		private String codigoProvincia;
		private String expediente;
		/**
		 * Format dd/mm/yyyy
		 */
		private String fechaConsulta;
		/**
		 * Format dd/mm/yyyy
		 */
		private String fechaNacimiento;
		/**
		 * Valors: S o N
		 */
		private String consentimientoTiposDiscapacidad;

		@Override
		public String getDatosEspecificos() { // xml
			StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			xmlBuilder.append("<DatosEspecificos>");
			if (!isEmptyString(codigoComunidadAutonoma) || !isEmptyString(codigoProvincia) || !isEmptyString(expediente) || !isEmptyString(fechaConsulta) || !isEmptyString(fechaNacimiento) || !isEmptyString(consentimientoTiposDiscapacidad)) {
				xmlBuilder.append("<Consulta>");
				xmlBuilder.append(
						xmlOptionalStringParameter(this.codigoComunidadAutonoma, "CodigoComunidadAutonoma")
				);
				xmlBuilder.append(
						xmlOptionalStringParameter(this.codigoProvincia, "CodigoProvincia")
				);
				xmlBuilder.append(
						xmlOptionalStringParameter(this.expediente, "Expediente")
				);
				xmlBuilder.append(
						xmlOptionalStringParameter(this.fechaConsulta, "FechaConsulta")
				);
				if (!isEmptyString(fechaNacimiento)) {
					xmlBuilder.append("<DatosAdicionalesTitular>");
					xmlBuilder.append(
							xmlOptionalStringParameter(this.fechaNacimiento, "FechaNacimiento")
					);
					xmlBuilder.append("</DatosAdicionalesTitular>");
				}
				xmlBuilder.append(
						xmlOptionalStringParameter(this.consentimientoTiposDiscapacidad, "ConsentimientoTiposDiscapacidad")
				);
				xmlBuilder.append("</Consulta>");
			}
			xmlBuilder.append("</DatosEspecificos>");
			return xmlBuilder.toString();
		}

	}
}
