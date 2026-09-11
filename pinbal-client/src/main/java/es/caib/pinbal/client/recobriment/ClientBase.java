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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger logger = LoggerFactory.getLogger(ClientBase.class);

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
				throwRecobrimentException("peticionSincrona", rawResponse);
			}
			return rawResponse.readEntity(ScspRespuesta.class);
		} catch (RecobrimentException re) {
			throw re;
		} catch (ProcessingException ex) {
			if (ex.getMessage() != null && ex.getMessage().contains("media type text/html")) {
				throw new RecobrimentException("Error d'autenticació: usuari o contrasenya incorrectes.", 403, null);
			}
			logger.error("Error de comunicació amb PINBAL (peticionSincrona): {}", ex.getMessage(), ex);
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
				throwRecobrimentException("peticionAsincrona", rawResponse);
			}
			return rawResponse.readEntity(ScspConfirmacionPeticion.class);
		} catch (RecobrimentException re) {
			throw re;
		} catch (ProcessingException ex) {
			if (ex.getMessage() != null && ex.getMessage().contains("media type text/html")) {
				throw new RecobrimentException("Error d'autenticació: usuari o contrasenya incorrectes.", 403, null);
			}
			logger.error("Error de comunicació amb PINBAL (peticionAsincrona): {}", ex.getMessage(), ex);
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
				throwRecobrimentException("getRespuesta", rawResponse);
			}
			return rawResponse.readEntity(ScspRespuesta.class);
		} catch (RecobrimentException re) {
			throw re;
		} catch (ProcessingException ex) {
			if (ex.getMessage() != null && ex.getMessage().contains("media type text/html")) {
				throw new RecobrimentException("Error d'autenticació: usuari o contrasenya incorrectes.", 403, null);
			}
			logger.error("Error de comunicació amb PINBAL (getRespuesta): {}", ex.getMessage(), ex);
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
			logger.error("Error de comunicació amb PINBAL (getJustificante): {}", ex.getMessage(), ex);
			throw new RecobrimentException(ex.getMessage(), 500, null);
		} finally {
			response.close();
		}
	}

	private boolean isHtmlAuthError(Response response) {
		MediaType mediaType = response.getMediaType();
		return mediaType != null && mediaType.isCompatible(MediaType.TEXT_HTML_TYPE);
	}

	/**
	 * Converteix un error HTTP rebut de PINBAL en una {@link RecobrimentException}, deixant sempre
	 * a la traça de log el motiu exacte de l'error: si PINBAL ha respost amb el seu format habitual
	 * d'error de negoci (JSON amb "message"/"trace"), o si la resposta prové d'un element
	 * d'infraestructura (proxy, balancejador, contenidor) davant de PINBAL -- p.ex. un rebuig per
	 * capçalera de petició massa gran -- cas en què el cos de la resposta no és el JSON esperat.
	 */
	private void throwRecobrimentException(String operacio, Response response) throws RecobrimentException {
		String errorBody = response.readEntity(String.class);
		String contentType = response.getMediaType() != null ? response.getMediaType().toString() : null;
		ErrorResponse errorResponse = null;
		try {
			errorResponse = mapper.readValue(errorBody, ErrorResponse.class);
		} catch (Exception ignored) {}
		if (errorResponse != null && errorResponse.getMessage() != null) {
			String[] parts = errorResponse.getMessage().split("\n", 2);
			logger.error(
					"PINBAL ({}) ha retornat un error de validació de negoci HTTP {}: {}",
					operacio, response.getStatus(), parts.length > 0 ? parts[0] : null);
			throw new RecobrimentException(
					parts.length > 0 ? parts[0] : null,
					response.getStatus(),
					errorResponse.getTrace());
		}
		logger.error(
				"PINBAL ({}) ha retornat HTTP {} amb un cos que no és el format JSON d'error de negoci de PINBAL " +
				"(Content-Type={}). Això normalment indica que la resposta prové d'un element d'infraestructura " +
				"(proxy, balancejador o contenidor JBoss) i no de la lògica de negoci de PINBAL -- per exemple, " +
				"un rebuig per capçalera de petició (Cookie inclosa) massa gran. Cos de la resposta: {}",
				operacio, response.getStatus(), contentType, errorBody);
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
