package es.caib.pinbal.client.recobriment;

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

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client amb la lògica bàsica per a accedir al servei de recobriment.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class ClientBase extends es.caib.pinbal.client.comu.ClientBase {

	private static final String BASE_URL_SUFIX = "/interna/recobriment/";

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
		ScspPeticion peticion = toScspPeticion(serveiCodi, solicituds);
		Response rawResponse = restPeticioPost("peticionSincrona", peticion, MediaType.APPLICATION_JSON_TYPE);
		try {
			rawResponse.bufferEntity();
			if (isHtmlAuthError(rawResponse)) {
				throw new RecobrimentException("Error d'autenticació: usuari o contrasenya incorrectes.", 403, null);
			}
			if (rawResponse.getStatus() >= 400) {
				throwRecobrimentException(rawResponse);
			}
			return rawResponse.readEntity(ScspRespuesta.class);
		} catch (RecobrimentException re) {
			throw re;
		} catch (ProcessingException ex) {
			if (ex.getMessage() != null && ex.getMessage().contains("media type text/html")) {
				throw new RecobrimentException("Error d'autenticació: usuari o contrasenya incorrectes.", 403, null);
			}
			throw new RecobrimentException(ex.getMessage(), 500, null);
		} finally {
			rawResponse.close();
		}
	}

	protected ScspConfirmacionPeticion basePeticionAsincrona(
			String serveiCodi,
			List<? extends SolicitudBase> solicituds) throws RecobrimentException, IOException {
		ScspPeticion peticion = toScspPeticion(serveiCodi, solicituds);
		Response rawResponse = restPeticioPost("peticionAsincrona", peticion, MediaType.APPLICATION_JSON_TYPE);
		try {
			rawResponse.bufferEntity();
			if (isHtmlAuthError(rawResponse)) {
				throw new RecobrimentException("Error d'autenticació: usuari o contrasenya incorrectes.", 403, null);
			}
			if (rawResponse.getStatus() >= 400) {
				throwRecobrimentException(rawResponse);
			}
			return rawResponse.readEntity(ScspConfirmacionPeticion.class);
		} catch (RecobrimentException re) {
			throw re;
		} catch (ProcessingException ex) {
			if (ex.getMessage() != null && ex.getMessage().contains("media type text/html")) {
				throw new RecobrimentException("Error d'autenticació: usuari o contrasenya incorrectes.", 403, null);
			}
			throw new RecobrimentException(ex.getMessage(), 500, null);
		} finally {
			rawResponse.close();
		}
	}

	public ScspRespuesta getRespuesta(
			String idPeticion) throws RecobrimentException, IOException {
		Map<String, String> requestParams = new HashMap<>();
		requestParams.put("idPeticion", idPeticion);
		Response rawResponse = restPeticioGet("getRespuesta", requestParams);
		try {
			rawResponse.bufferEntity();
			if (isHtmlAuthError(rawResponse)) {
				throw new RecobrimentException("Error d'autenticació: usuari o contrasenya incorrectes.", 403, null);
			}
			if (rawResponse.getStatus() >= 400) {
				throwRecobrimentException(rawResponse);
			}
			return rawResponse.readEntity(ScspRespuesta.class);
		} catch (RecobrimentException re) {
			throw re;
		} catch (ProcessingException ex) {
			if (ex.getMessage() != null && ex.getMessage().contains("media type text/html")) {
				throw new RecobrimentException("Error d'autenticació: usuari o contrasenya incorrectes.", 403, null);
			}
			throw new RecobrimentException(ex.getMessage(), 500, null);
		} finally {
			rawResponse.close();
		}
	}

	public ScspJustificante getJustificante(
			String idPeticion) throws RecobrimentException, IOException {
		return getJustificante(idPeticion, idPeticion);
	}

	public ScspJustificante getJustificante(
			String idPeticion,
			String idSolicitud) throws RecobrimentException, IOException {
		Map<String, String> requestParams = new HashMap<>();
		requestParams.put("idPeticion", idPeticion);
		requestParams.put("idSolicitud", idSolicitud);
		Response response = restPeticioGetResponse("getJustificante", requestParams, null);
		try {
			response.bufferEntity();
			MediaType mediaType = response.getMediaType();
			if (mediaType != null && mediaType.isCompatible(MediaType.APPLICATION_OCTET_STREAM_TYPE)) {
				ScspJustificante justificante = new ScspJustificante();
				String contentDisposition = response.getHeaderString("Content-Disposition");
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
				justificante.setContentType(response.getHeaderString("Content-Type"));
				justificante.setContingut(inputStreamToByteArray(response.readEntity(InputStream.class)));
				return justificante;
			} else if (mediaType != null && mediaType.isCompatible(MediaType.TEXT_HTML_TYPE)) {
				throw new RecobrimentException("Error d'autenticació: usuari o contrasenya incorrectes.", 403, null);
			} else {
				String mediaTypeStr = mediaType != null
						? (mediaType.getType() + "/" + mediaType.getSubtype()) : "unknown";
				throw new RecobrimentException(
						"El tipus de contingut de la resposta (" + mediaTypeStr + ") no és del tipus esperat: "
								+ MediaType.APPLICATION_OCTET_STREAM,
						406, null);
			}
		} catch (RecobrimentException re) {
			throw re;
		} catch (ProcessingException ex) {
			throw new RecobrimentException(ex.getMessage(), 500, null);
		} finally {
			response.close();
		}
	}

	private boolean isHtmlAuthError(Response response) {
		MediaType mediaType = response.getMediaType();
		return mediaType != null && mediaType.isCompatible(MediaType.TEXT_HTML_TYPE);
	}

	private void throwRecobrimentException(Response response) throws RecobrimentException {
		String errorBody = response.readEntity(String.class);
		ErrorResponse errorResponse = null;
		try {
			errorResponse = mapper.readValue(errorBody, ErrorResponse.class);
		} catch (Exception ignored) {}
		if (errorResponse != null && errorResponse.getMessage() != null) {
			String[] parts = errorResponse.getMessage().split("\n", 2);
			throw new RecobrimentException(
					parts.length > 0 ? parts[0] : null,
					response.getStatus(),
					errorResponse.getTrace());
		}
		throw new RecobrimentException(errorBody, response.getStatus(), null);
	}

	private ScspPeticion toScspPeticion(
			String serveiCodi,
			List<? extends SolicitudBase> solicituds) {
		ScspPeticion peticion = new ScspPeticion();
		ScspAtributos atributos = new ScspAtributos();
		atributos.setCodigoCertificado(serveiCodi);
		atributos.setNumElementos(
				Integer.toString(solicituds != null ? solicituds.size() : 0));
		peticion.setAtributos(atributos);
		if (solicituds != null) {
			List<ScspSolicitud> solicitudes = new ArrayList<>();
			for (SolicitudBase solicitud : solicituds) {
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
