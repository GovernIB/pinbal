/**
 * 
 */
package es.caib.pinbal.client.recobriment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

import es.caib.pinbal.client.recobriment.model.ScspAtributos;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspDatosGenericos;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspPeticion;
import es.caib.pinbal.client.recobriment.model.ScspProcedimiento;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.ScspSolicitante;
import es.caib.pinbal.client.recobriment.model.ScspSolicitud;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import lombok.Getter;
import lombok.Setter;

/**
 * Client amb la lògica bàsica per a accedir al servei de recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class ClientBase extends es.caib.pinbal.client.comu.ClientBase {

	private static final String BASE_URL_SUFIX = "/api/recobriment/";

	protected ClientBase(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase + BASE_URL_SUFIX, usuari, contrasenya);
	}

	protected ClientBase(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	protected ScspRespuesta basePeticionSincrona(
			String serveiCodi,
			List<? extends SolicitudBase> solicituds) throws RecobrimentException, IOException {
		try {
			ScspPeticion peticion = toScspPeticion(serveiCodi, solicituds);
			ScspRespuesta response = restPeticioPost(
					"peticionSincrona",
					peticion,
					ScspRespuesta.class);
			return response;
		} catch (UniformInterfaceException ex) {
			ClientResponse response = ex.getResponse();
			ErrorResponse errorResponse = ex.getResponse().getEntity(ErrorResponse.class);
			throw new RecobrimentException(
					errorResponse.getMessage(),
					response.getStatus(),
					errorResponse.getTrace());
		}
	}

	protected ScspConfirmacionPeticion basePeticionAsincrona(
			String serveiCodi,
			List<? extends SolicitudBase> solicituds) throws RecobrimentException, IOException {
		try {
			ScspPeticion peticion = toScspPeticion(serveiCodi, solicituds);
			ScspConfirmacionPeticion response = restPeticioPost(
					"peticionSincrona",
					peticion,
					ScspConfirmacionPeticion.class);
			return response;
		} catch (UniformInterfaceException ex) {
			ClientResponse response = ex.getResponse();
			ErrorResponse errorResponse = ex.getResponse().getEntity(ErrorResponse.class);
			throw new RecobrimentException(
					errorResponse.getMessage(),
					response.getStatus(),
					errorResponse.getTrace());
		}
	}

	public ScspRespuesta getRespuesta(
			String idPeticion) throws RecobrimentException, IOException {
		try {
			Map<String, String> requestParams = new HashMap<String, String>();
			requestParams.put("idPeticion", idPeticion);
			ScspRespuesta response = restPeticioGet(
					"getRespuesta",
					requestParams,
					ScspRespuesta.class);
			return response;
		} catch (UniformInterfaceException ex) {
			ClientResponse response = ex.getResponse();
			ErrorResponse errorResponse = ex.getResponse().getEntity(ErrorResponse.class);
			throw new RecobrimentException(
					errorResponse.getMessage(),
					response.getStatus(),
					errorResponse.getTrace());
		}
	}

	public ScspJustificante getJustificante(
			String idPeticion) throws RecobrimentException, IOException {
		return getJustificante(idPeticion, idPeticion);
	}

	public ScspJustificante getJustificante(
			String idPeticion,
			String idSolicitud) throws RecobrimentException, IOException {
		try {
			Map<String, String> requestParams = new HashMap<String, String>();
			requestParams.put("idPeticion", idPeticion);
			requestParams.put("idSolicitud", idSolicitud);
			ClientResponse response = restPeticioGetResponse(
					"getJustificante",
					requestParams);
			ScspJustificante justificante = new ScspJustificante();
			String contentDisposition = getHeaderFirst(response, "Content-Disposition");
			if (contentDisposition != null) {
				String token = "filename=\"";
				int tokenIndex = contentDisposition.indexOf(token);
				if (tokenIndex != -1) {
					justificante.setNom(
							contentDisposition.substring(
									tokenIndex + token.length(),
									contentDisposition.indexOf("\"", tokenIndex + token.length())));
				}
			}
			justificante.setContentType(getHeaderFirst(response, "Content-Type"));
			justificante.setContingut(
					inputStreamToByteArray(response.getEntityInputStream()));
			return justificante;
		} catch (UniformInterfaceException ex) {
			ClientResponse response = ex.getResponse();
			ErrorResponse errorResponse = ex.getResponse().getEntity(ErrorResponse.class);
			throw new RecobrimentException(
					errorResponse.getMessage(),
					response.getStatus(),
					errorResponse.getTrace());
		}
	}

	private ScspPeticion toScspPeticion(
			String serveiCodi,
			List<? extends SolicitudBase> solicituds) {
		ScspPeticion peticion = new ScspPeticion();
		ScspAtributos atributos = new ScspAtributos();
		atributos.setCodigoCertificado(serveiCodi);
		atributos.setNumElementos(
				new Integer(solicituds != null ? solicituds.size() : 0).toString());
		peticion.setAtributos(atributos);
		if (solicituds != null) {
			List<ScspSolicitud> solicitudes = new ArrayList<ScspSolicitud>();
			for (SolicitudBase solicitud: solicituds) {
				ScspSolicitud sol = new ScspSolicitud();
				ScspDatosGenericos datosGenericos = new ScspDatosGenericos();
				ScspSolicitante solicitante = new ScspSolicitante();
				ScspProcedimiento procedimiento = new ScspProcedimiento();
				procedimiento.setCodProcedimiento(solicitud.getCodigoProcedimiento());
				solicitante.setProcedimiento(procedimiento);
				solicitante.setIdentificadorSolicitante(solicitud.getIdentificadorSolicitante());
				solicitante.setNombreSolicitante(solicitud.getNombreSolicitante());
				solicitante.setFuncionario(solicitud.getFuncionario());
				solicitante.setUnidadTramitadora(solicitud.getUnidadTramitadora());
				solicitante.setCodigoUnidadTramitadora(solicitud.getCodigoUnidadTramitadora());
				solicitante.setFinalidad(solicitud.getFinalidad());
				solicitante.setConsentimiento(solicitud.getConsentimiento());
				solicitante.setIdExpediente(solicitud.getIdExpediente());
				datosGenericos.setSolicitante(solicitante);
				datosGenericos.setTitular(solicitud.getTitular());
				sol.setDatosGenericos(datosGenericos);
				sol.setDatosEspecificos(solicitud.getDatosEspecificos());
				solicitudes.add(sol);
			}
			peticion.setSolicitudes(solicitudes);
		}
		return peticion;
	}

	private String getHeaderFirst(ClientResponse response, String headerName) {
		if (response.getHeaders() != null) {
			List<String> headerValue = response.getHeaders().get(headerName);
			if (headerValue != null && headerValue.size() > 0) {
				return headerValue.get(0);
			}
		}
		return null;
	}

	private byte[] inputStreamToByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    int nRead;
	    byte[] data = new byte[1024];
	    while ((nRead = is.read(data, 0, data.length)) != -1) {
	        buffer.write(data, 0, nRead);
	    }
	    buffer.flush();
	    return buffer.toByteArray();
	}

	@Getter
	@Setter
	private static class ErrorResponse {
		private String message;
		private String trace;
	}

}
