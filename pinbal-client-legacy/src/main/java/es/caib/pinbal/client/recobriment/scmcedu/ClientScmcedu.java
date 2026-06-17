/**
 * 
 */
package es.caib.pinbal.client.recobriment.scmcedu;

import java.io.IOException;
import java.util.List;

import es.caib.pinbal.client.recobriment.ClientBase;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Client del recobriment per a fer peticions al servei SCSP SCMCEDU:
 * "Servei de consulta de dades d'escolarització"
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientScmcedu extends ClientBase {

	private static final String SERVEI_CODI = "SCMCEDU";

	public ClientScmcedu(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientScmcedu(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public ScspRespuesta peticionSincrona(
			List<SolicitudScmcedu> solicituds) throws IOException {
		return basePeticionSincrona(SERVEI_CODI, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			List<SolicitudScmcedu> solicituds) throws IOException {
		return basePeticionAsincrona(SERVEI_CODI, solicituds);
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class SolicitudScmcedu extends SolicitudBase {

		public enum TipoDocumentacion {
			NIF,
			NIE,
			Passaport,
			Document_comunitari
		}

		/**
		 * Data de naixament en format DD/MM/YYYY
		 */
		@Getter @Setter
		private String fechaNacimientoTitular;

		private TipoDocumentacion tutor_tipoDocumentacion;
		private String tutor_documentacion;

		/**
		 * Dades identificatives del tutor
		 *
		 * @param tipo Tipus de documentació del tutor. Valors possibles: [NIF, NIE, Passaport, Document comunitari]
		 * @param documento Número de document del tutor
		 */
		public void setDadesTutor(TipoDocumentacion tipo, String documento) {
			this.tutor_tipoDocumentacion = tipo;
			this.tutor_documentacion = documento;
		}

		@Override
		public String getDatosEspecificos() { // xml

			StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			xmlBuilder.append("<DatosEspecificos>");
			if (!isEmptyString(fechaNacimientoTitular) || !isEmptyString(tutor_documentacion)) {
				xmlBuilder.append("<Solicitud>");
				if (!isEmptyString(fechaNacimientoTitular)) {
					xmlBuilder.append(
							xmlOptionalStringParameter(this.fechaNacimientoTitular, "FechaNacimientoTitular")
					);
				}
				if (!isEmptyString(tutor_documentacion)) {
					xmlBuilder.append("<IdTutor>");
					xmlBuilder.append(
							xmlOptionalStringParameter(this.tutor_tipoDocumentacion.name().replace("_", " "), "TipoDocumentacion")
					);
					xmlBuilder.append(
							xmlOptionalStringParameter(this.tutor_documentacion, "Documentacion")
					);
					xmlBuilder.append("</IdTutor>");
				}
				xmlBuilder.append("</Solicitud>");

			}
			xmlBuilder.append("</DatosEspecificos>");
			return xmlBuilder.toString();
		}
	}

}
