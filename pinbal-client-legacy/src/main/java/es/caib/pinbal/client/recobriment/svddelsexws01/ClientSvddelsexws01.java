/**
 * 
 */
package es.caib.pinbal.client.recobriment.svddelsexws01;

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
 * Client del recobriment per a fer peticions al servei SCSP SVDDELSEXWS01:
 * "Consulta de inexistencia de delitos sexuales por datos de filiación"
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientSvddelsexws01 extends ClientBase {

	private static final String SERVEI_CODI = "SVDDELSEXWS01";

	public ClientSvddelsexws01(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientSvddelsexws01(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public ScspRespuesta peticionSincrona(
			List<SolicitudSvddelsexws01> solicituds) throws IOException {
		return basePeticionSincrona(SERVEI_CODI, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			List<SolicitudSvddelsexws01> solicituds) throws IOException {
		return basePeticionAsincrona(SERVEI_CODI, solicituds);
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class SolicitudSvddelsexws01 extends SolicitudBase {

		public enum Sexe {
			H,
			M
		}

		/**
		 * Códi de país de naixament (codificació de 3 dígits ISO3166)
		 */
		private String nacionalidad;
		/**
		 * Código del sexo, valores válidos:
		 * · H – Hombre
		 * · M – Mujer
		 */
		private Sexe sexo;
		/**
		 * Nombre del padre. Campo optativo (obligatorio a menos el nombre de un progenitor para nacionales españoles)
		 */
		private String nombrePadre;
		/**
		 * Código de país de nacimiento (codificación de 3 digitos ISO3166)
		 */
		private String paisNacimiento;
		/**
		 * Nombre del madre. Campo optativo (obligatorio a menos el nombre de un progenitor para nacionales españoles)
		 */
		private String nombreMadre;
		/**
		 * Código de provincia de nacimiento. Campo obligatorio si nacido en España (codificación 2 dígitos del INE)
		 */
		private String provinciaNacimiento;
		/**
		 * Descripción de población de nacimiento, obligatorio en caso de nacer en un país extranjero
		 */
		private String poblacionNacimiento;
		/**
		 * Código de población de nacimiento. Campo obligatorio si nacido en España (codificación 5 dígitos del INE)
		 */
		private String codPoblacionNacimiento;
		/**
		 * Fecha de nacimiento. Formatos válidos:
		 * · DD/MM/AAAA
		 * · 00/MM/AAAA
		 * · 00/00/AAAA
		 *  Donde AAAA indica el año, MM indica el mes, DD indica el día
		 */
		private String fechaNacimiento;
		/**
		 * Correo electrónico
		 */
		private String mail;
		/**
		 * Teléfono
		 */
		private String telefono;

		public void setDadesNaixament(
				String paisNacimiento,
				String provinciaNacimiento,
				String codPoblacionNacimiento,
				String poblacionNacimiento,
				String fechaNacimiento) {
			this.paisNacimiento = paisNacimiento;
			this.provinciaNacimiento = provinciaNacimiento;
			this.codPoblacionNacimiento = codPoblacionNacimiento;
			this.poblacionNacimiento = poblacionNacimiento;
			this.fechaNacimiento = fechaNacimiento;
		}

		public void setDadesPersonals(
				String nacionalidad,
				Sexe sexo,
				String nombrePadre,
				String nombreMadre,
				String mail,
				String telefono) {
			this.nacionalidad = nacionalidad;
			this.sexo = sexo;
			this.nombrePadre = nombrePadre;
			this.nombreMadre = nombreMadre;
			this.mail = mail;
			this.telefono = telefono;
		}

		@Override
		public String getDatosEspecificos() { // xml

			if (isEmptyString(nacionalidad)) {
				throw new MissingCampObligatoriException("Nacionalidad");
			}
			if (isEmptyString(paisNacimiento)) {
				throw new MissingCampObligatoriException("PaisNacimiento");
			}
			if (isEmptyString(fechaNacimiento)) {
				throw new MissingCampObligatoriException("FechaNacimiento");
			}

			StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			xmlBuilder.append("<DatosEspecificos>");
			xmlBuilder.append("<Consulta>");

			xmlBuilder.append(xmlOptionalStringParameter(this.nacionalidad, "Nacionalidad"));
			if (sexo != null)
				xmlBuilder.append(xmlOptionalStringParameter(this.sexo.name(), "Sexo"));
			if (!isEmptyString(nombrePadre))
				xmlBuilder.append(xmlOptionalStringParameter(this.nombrePadre, "NombrePadre"));
			xmlBuilder.append(xmlOptionalStringParameter(this.paisNacimiento, "PaisNacimiento"));
			if (!isEmptyString(nombreMadre))
				xmlBuilder.append(xmlOptionalStringParameter(this.nombreMadre, "NombreMadre"));
			if (!isEmptyString(provinciaNacimiento))
				xmlBuilder.append(xmlOptionalStringParameter(this.provinciaNacimiento, "ProvinciaNacimiento"));
			if (!isEmptyString(poblacionNacimiento))
				xmlBuilder.append(xmlOptionalStringParameter(this.poblacionNacimiento, "PoblacionNacimiento"));
			if (!isEmptyString(codPoblacionNacimiento))
				xmlBuilder.append(xmlOptionalStringParameter(this.codPoblacionNacimiento, "CodPoblacionNacimiento"));
			xmlBuilder.append(xmlOptionalStringParameter(this.fechaNacimiento, "FechaNacimiento"));
			if (!isEmptyString(mail))
				xmlBuilder.append(xmlOptionalStringParameter(this.mail, "Mail"));
			if (!isEmptyString(telefono))
				xmlBuilder.append(xmlOptionalStringParameter(this.telefono, "Telefono"));

			xmlBuilder.append("</Consulta>");
			xmlBuilder.append("</DatosEspecificos>");
			return xmlBuilder.toString();
		}
	}

}
