/**
 * 
 */
package es.caib.pinbal.client.recobriment.q2827003atgss001;

import es.caib.pinbal.client.recobriment.ClientBase;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.util.List;

/**
 * Client del recobriment per a fer peticions al servei SCSP SVDCCAACPASWS01:
 * "Estar al corriente de obligaciones tributarias para solicitud de
 * subvenciones y ayudas de la CCAA" .
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientQ2827003atgss001 extends ClientBase {

	private static final String SERVEI_CODI = "Q2827003ATGSS001";

	public ClientQ2827003atgss001(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase, usuari, contrasenya);
	}

	public ClientQ2827003atgss001(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public ScspRespuesta peticionSincrona(
			List<SolicitudQ2827003atgss001> solicituds) throws IOException {
		return basePeticionSincrona(SERVEI_CODI, solicituds);
	}

	public ScspConfirmacionPeticion peticionAsincrona(
			List<SolicitudQ2827003atgss001> solicituds) throws IOException {
		return basePeticionAsincrona(SERVEI_CODI, solicituds);
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class SolicitudQ2827003atgss001 extends SolicitudBase {
//		private String codigoProvincia;
//		private String codigoComunidadAutonoma;

		@Override
		public String getDatosEspecificos() { // xml
//			StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//			xmlBuilder.append("<DatosEspecificos>");
//			if (!isEmptyString(codigoProvincia)) {
//				xmlBuilder.append("<Consulta>");
//				xmlBuilder.append(
//						xmlOptionalStringParameter(this.codigoComunidadAutonoma, "CodigoComunidadAutonoma")
//				);
//				xmlBuilder.append(
//						xmlOptionalStringParameter(this.codigoProvincia, "CodigoProvincia")
//				);
//				xmlBuilder.append("</Consulta>");
//
//			}
//			xmlBuilder.append("</DatosEspecificos>");
//			return xmlBuilder.toString();
			return null;
		}
	}

}
