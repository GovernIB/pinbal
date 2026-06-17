/**
 * 
 */
package es.caib.pinbal.client.recobriment.svdsccdws01;

import es.caib.pinbal.client.recobriment.ClientBase;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.util.List;

/**
 * Client del recobriment per a fer peticions al servei SCSP SVDSCCDWS01:
 * "Consulta de defunción (MINISTERIO DE JUSTICIA)"
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientSvdsccdws01 extends ClientBase {

	private static final String SERVEI_CODI = "SVDSCCDWS01";

	public ClientSvdsccdws01(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientSvdsccdws01(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public ScspRespuesta peticionSincrona(
			List<SolicitudSvdsccdws01> solicituds) throws IOException {
		return basePeticionSincrona(SERVEI_CODI, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			List<SolicitudSvdsccdws01> solicituds) throws IOException {
		return basePeticionAsincrona(SERVEI_CODI, solicituds);
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class SolicitudSvdsccdws01 extends SolicitudBase {
		private String fechaHechoRegistral;
		private Boolean ausenciaSegundoApellido;
		private String registroCivil;
		private String tomo;
		private String pagina;
		private String poblacionHechoRegistral;
		private String fechaNacimiento;
		private String poblacionNacimiento;
		private String nombrePadre;
		private String nombreMadre;

		public void setDadesAdicionalsTitular(String fechaHechoRegistral, Boolean ausenciaSegundoApellido) {
			this.fechaHechoRegistral = fechaHechoRegistral;
			this.ausenciaSegundoApellido = ausenciaSegundoApellido;
		}

		public void setConsultaPerDadesRegistrals(String registroCivil, String tomo, String pagina) {
			this.registroCivil = registroCivil;
			this.tomo = tomo;
			this.pagina = pagina;
		}

		public void setConsultaPerAltresDades(
				String poblacionHechoRegistral,
				String fechaNacimiento,
				String poblacionNacimiento,
				String nombrePadre,
				String nombreMadre) {
			this.poblacionHechoRegistral = poblacionHechoRegistral;
			this.fechaNacimiento = fechaNacimiento;
			this.poblacionNacimiento = poblacionNacimiento;
			this.nombrePadre = nombrePadre;
			this.nombreMadre = nombreMadre;
		}

		@Override
		public String getDatosEspecificos() { // xml

//			if (isEmptyString(fechaHechoRegistral)) {
//				throw new MissingCampObligatoriException("FechaHechoRegistral");
//			}

			StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			xmlBuilder.append("<DatosEspecificos>");
			xmlBuilder.append("<Consulta>");

			if (!isEmptyString(fechaHechoRegistral) || ausenciaSegundoApellido != null) {
				xmlBuilder.append("<DatosAdicionalesTitularConsulta>");
				if (!isEmptyString(fechaHechoRegistral))
					xmlBuilder.append(
							xmlOptionalStringParameter(this.fechaHechoRegistral, "FechaHechoRegistral")
					);
				if (ausenciaSegundoApellido != null)
					xmlBuilder.append(
							xmlOptionalStringParameter(this.ausenciaSegundoApellido ? "true" : "false", "AusenciaSegundoApellido")
					);
				xmlBuilder.append("</DatosAdicionalesTitularConsulta>");
			}

			if (!isEmptyString(registroCivil) ||
					!isEmptyString(tomo) ||
					!isEmptyString(pagina)) {
				xmlBuilder.append("<ConsultaPorDatosRegistrales>");
				if (!isEmptyString(registroCivil)) {
					xmlBuilder.append(
							xmlOptionalStringParameter(this.registroCivil, "RegistroCivil")
					);
				}
				if (!isEmptyString(tomo)) {
					xmlBuilder.append(
							xmlOptionalStringParameter(this.tomo, "Tomo")
					);
				}
				if (!isEmptyString(pagina)) {
					xmlBuilder.append(
							xmlOptionalStringParameter(this.pagina, "Pagina")
					);
				}
				xmlBuilder.append("</ConsultaPorDatosRegistrales>");
			}

			if (!isEmptyString(poblacionHechoRegistral) ||
					!isEmptyString(fechaNacimiento) ||
					!isEmptyString(poblacionNacimiento) ||
					!isEmptyString(nombrePadre) ||
					!isEmptyString(nombreMadre)) {
				xmlBuilder.append("<ConsultaPorOtrosDatos>");
				if (!isEmptyString(poblacionHechoRegistral))
					xmlBuilder.append(
							xmlOptionalStringParameter(this.poblacionHechoRegistral, "PoblacionHechoRegistral")
					);
				if (!isEmptyString(fechaNacimiento))
					xmlBuilder.append(
							xmlOptionalStringParameter(this.fechaNacimiento, "FechaNacimiento")
					);
				if (!isEmptyString(poblacionNacimiento))
					xmlBuilder.append(
							xmlOptionalStringParameter(this.poblacionNacimiento, "PoblacionNacimiento")
					);
				if (!isEmptyString(nombrePadre))
					xmlBuilder.append(
							xmlOptionalStringParameter(this.nombrePadre, "NombrePadre")
					);
				if (!isEmptyString(nombreMadre))
					xmlBuilder.append(
							xmlOptionalStringParameter(this.nombreMadre, "NombreMadre")
					);
				xmlBuilder.append("</ConsultaPorOtrosDatos>");
			}

			xmlBuilder.append("</Consulta>");
			xmlBuilder.append("</DatosEspecificos>");
			return xmlBuilder.toString();
		}
	}

}
