/**
 * 
 */
package es.caib.pinbal.client.recobriment.scdhpaju;

import es.caib.pinbal.client.recobriment.ClientBase;
import es.caib.pinbal.client.recobriment.model.MissingCampObligatoriException;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.util.List;

/**
 * Client del recobriment per a fer peticions al servei SCSP SCDHPAJU:
 * "Servei de consulta de DADES HISTÒRIQUES sobre els PADRONS MUNICIPALS"
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientScdhpaju extends ClientBase {

	private static final String SERVEI_CODI = "SCDHPAJU";

	public ClientScdhpaju(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientScdhpaju(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public ScspRespuesta peticionSincrona(
			List<SolicitudScdhpaju> solicituds) throws IOException {
		return basePeticionSincrona(SERVEI_CODI, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			List<SolicitudScdhpaju> solicituds) throws IOException {
		return basePeticionAsincrona(SERVEI_CODI, solicituds);
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class SolicitudScdhpaju extends SolicitudBase {
		private String provinciaSolicitud;
		private String municipioSolicitud;
		private String documentacion_tipo;
		private String documentacion_valor;
		private String documentacion_numSoporte;
		private String datosPersonales_tipo;
		private String datosPersonales_valor;
		private String datosPersonales_numSoporte;
		private String nombre;
		private String particula1;
		private String apellido1;
		private String particula2;
		private String apellido2;
		private String fechaNacimiento;
		private String nia;
		private String numeroAnyos;

		public void setConsultaPerDocumentIdentitat(String tipo, String valor, String numSoporte) {
			this.documentacion_tipo = tipo;
			this.documentacion_valor = valor;
			this.documentacion_numSoporte = numSoporte;
		}

		public void setConsultaPerDadesPersonals(
				String tipo,
				String valor,
				String numSoporte,
				String nombre,
				String particula1,
				String apellido1,
				String particula2,
				String apellido2,
				String fechaNacimiento) {
			this.datosPersonales_tipo = tipo;
			this.datosPersonales_valor = valor;
			this.datosPersonales_numSoporte = numSoporte;
			this.nombre = nombre;
			this.particula1 = particula1;
			this.apellido1 = apellido1;
			this.particula2 = particula2;
			this.apellido2 = apellido2;
			this.fechaNacimiento = fechaNacimiento;
		}

		public void setConsultaPerReferenciaNia(String nia) {
			this.nia = nia;
		}

		@Override
		public String getDatosEspecificos() { // xml

			if (isEmptyString(provinciaSolicitud)) {
				throw new MissingCampObligatoriException("ProvinciaSolicitud");
			}
			if (isEmptyString(municipioSolicitud)) {
				throw new MissingCampObligatoriException("MunicipioSolicitud");
			}

			StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			xmlBuilder.append("<DatosEspecificos>");
			xmlBuilder.append("<Solicitud>");
			xmlBuilder.append(
					xmlOptionalStringParameter(this.provinciaSolicitud, "ProvinciaSolicitud")
			);
			xmlBuilder.append(
					xmlOptionalStringParameter(this.municipioSolicitud, "MunicipioSolicitud")
			);

			if (!isEmptyString(documentacion_tipo) ||
					!isEmptyString(documentacion_valor) ||
					!isEmptyString(documentacion_numSoporte) ||
					!isEmptyString(datosPersonales_tipo) ||
					!isEmptyString(datosPersonales_valor) ||
					!isEmptyString(datosPersonales_numSoporte) ||
					!isEmptyString(nombre) ||
					!isEmptyString(particula1) ||
					!isEmptyString(apellido1) ||
					!isEmptyString(particula2) ||
					!isEmptyString(apellido2) ||
					!isEmptyString(fechaNacimiento) ||
					!isEmptyString(nia)) {

				xmlBuilder.append("<Titular>");

				if (!isEmptyString(documentacion_tipo) ||
						!isEmptyString(documentacion_valor) ||
						!isEmptyString(documentacion_numSoporte)) {
					xmlBuilder.append("<Documentacion>");
					if (!isEmptyString(documentacion_tipo))
						xmlBuilder.append(
								xmlOptionalStringParameter(this.documentacion_tipo, "Tipo")
						);
					if (!isEmptyString(documentacion_valor))
						xmlBuilder.append(
								xmlOptionalStringParameter(this.documentacion_valor, "Valor")
						);
					if (!isEmptyString(documentacion_numSoporte))
						xmlBuilder.append(
								xmlOptionalStringParameter(this.documentacion_numSoporte, "NumSoporte")
						);
					xmlBuilder.append("</Documentacion>");
				}

				if (!isEmptyString(datosPersonales_tipo) ||
						!isEmptyString(datosPersonales_valor) ||
						!isEmptyString(datosPersonales_numSoporte) ||
						!isEmptyString(nombre) ||
						!isEmptyString(particula1) ||
						!isEmptyString(apellido1) ||
						!isEmptyString(particula2) ||
						!isEmptyString(apellido2) ||
						!isEmptyString(fechaNacimiento)) {

					xmlBuilder.append("<DatosPersonales>");

					if (!isEmptyString(datosPersonales_tipo) ||
							!isEmptyString(datosPersonales_valor) ||
							!isEmptyString(datosPersonales_numSoporte)) {
						xmlBuilder.append("<Documentacion>");
						if (!isEmptyString(datosPersonales_tipo))
							xmlBuilder.append(
									xmlOptionalStringParameter(this.datosPersonales_tipo, "Tipo")
							);
						if (!isEmptyString(datosPersonales_valor))
							xmlBuilder.append(
									xmlOptionalStringParameter(this.datosPersonales_valor, "Valor")
							);
						if (!isEmptyString(datosPersonales_numSoporte))
							xmlBuilder.append(
									xmlOptionalStringParameter(this.datosPersonales_numSoporte, "NumSoporte")
							);
						xmlBuilder.append("</Documentacion>");
					}

					if (!isEmptyString(nombre))
						xmlBuilder.append(
								xmlOptionalStringParameter(this.nombre, "Nombre")
						);
					if (!isEmptyString(particula1))
						xmlBuilder.append(
								xmlOptionalStringParameter(this.particula1, "Particula1")
						);
					if (!isEmptyString(apellido1))
						xmlBuilder.append(
								xmlOptionalStringParameter(this.apellido1, "Apellido1")
						);
					if (!isEmptyString(particula2))
						xmlBuilder.append(
								xmlOptionalStringParameter(this.particula2, "Particula2")
						);
					if (!isEmptyString(apellido2))
						xmlBuilder.append(
								xmlOptionalStringParameter(this.apellido2, "Apellido2")
						);
					if (!isEmptyString(fechaNacimiento))
						xmlBuilder.append(
								xmlOptionalStringParameter(this.fechaNacimiento, "FechaNacimiento")
						);

					xmlBuilder.append("</DatosPersonales>");
				}

				if(!isEmptyString(nia)) {
					xmlBuilder.append(
							xmlOptionalStringParameter(this.nia, "NIA")
					);
				}
				xmlBuilder.append("</Titular>");

				if (!isEmptyString(numeroAnyos))
					xmlBuilder.append(
							xmlOptionalStringParameter(this.numeroAnyos, "NumeroAnyos")
					);
			}

			xmlBuilder.append("</Solicitud>");
			xmlBuilder.append("</DatosEspecificos>");
			return xmlBuilder.toString();
		}
	}

}
