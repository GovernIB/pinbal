/**
 * 
 */
package es.caib.pinbal.client.recobriment;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.ws.rs.core.MediaType;
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
		try {
			ScspPeticion peticion = toScspPeticion(serveiCodi, solicituds);
			ScspRespuesta response = restPeticioPost(
					"peticionSincrona",
					peticion,
					ScspRespuesta.class,
					MediaType.APPLICATION_JSON_TYPE);
			return response;
		} catch (UniformInterfaceException ex) {
			throw toRecobrimentException("peticionSincrona", ex);
		} catch (ClientHandlerException ex) {
			throw toRecobrimentException("peticionSincrona", ex);
		}
	}

	protected ScspConfirmacionPeticion basePeticionAsincrona(
			String serveiCodi,
			List<? extends SolicitudBase> solicituds) throws RecobrimentException, IOException {
		try {
			ScspPeticion peticion = toScspPeticion(serveiCodi, solicituds);
			ScspConfirmacionPeticion response = restPeticioPost(
					"peticionAsincrona",
					peticion,
					ScspConfirmacionPeticion.class,
					MediaType.APPLICATION_JSON_TYPE);
			return response;
		} catch (UniformInterfaceException ex) {
			throw toRecobrimentException("peticionAsincrona", ex);
		} catch (ClientHandlerException ex) {
			throw toRecobrimentException("peticionAsincrona", ex);
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
			throw toRecobrimentException("getRespuesta", ex);
		} catch (ClientHandlerException ex) {
			throw toRecobrimentException("getRespuesta", ex);
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
					requestParams,
					null);//MediaType.APPLICATION_OCTET_STREAM_TYPE);
			if (response.getType().isCompatible(MediaType.APPLICATION_OCTET_STREAM_TYPE)) {
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
			} else {
				if (response.getType().isCompatible(MediaType.TEXT_HTML_TYPE)) {
					throw new RecobrimentException(
							"Error d'autenticació: usuari o contrasenya incorrectes.",
							403,
							null);
				} else {
					throw new RecobrimentException(
							"El tipus de contingut de la resposta (" + response.getType().getType() + ") no és del tipus esperat: " + MediaType.APPLICATION_OCTET_STREAM,
							406,
							null);
				}
			}
		} catch (UniformInterfaceException ex) {
			throw toRecobrimentException("getJustificante", ex);
		}
	}

	/**
	 * Converteix un error HTTP rebut de PINBAL en una {@link RecobrimentException}, deixant sempre
	 * a la traça de log el motiu exacte de l'error: si PINBAL ha respost amb el seu format habitual
	 * d'error de negoci (JSON amb "message"/"trace"), o si la resposta prové d'un element
	 * d'infraestructura (proxy, balancejador, contenidor) davant de PINBAL -- p.ex. un rebuig per
	 * capçalera de petició massa gran -- cas en què el cos de la resposta no és el JSON esperat.
	 */
	private RecobrimentException toRecobrimentException(String operacio, UniformInterfaceException ex) {
		ClientResponse response = ex.getResponse();
		String contentType = response.getType() != null ? response.getType().toString() : null;
		String body = null;
		try {
			if (response.hasEntity()) {
				body = response.getEntity(String.class);
			}
		} catch (Exception e) {
			// No s'ha pogut llegir el cos de la resposta; es continua amb body = null.
		}
		ErrorResponse errorResponse = null;
		if (body != null) {
			try {
				errorResponse = mapper.readValue(body, ErrorResponse.class);
			} catch (Exception e) {
				errorResponse = null;
			}
		}
		if (errorResponse == null || errorResponse.getMessage() == null) {
			logger.error(
					"PINBAL ({}) ha retornat HTTP {} amb un cos que no és el format JSON d'error de negoci de PINBAL " +
					"(Content-Type={}). Això normalment indica que la resposta prové d'un element d'infraestructura " +
					"(proxy, balancejador o contenidor JBoss) i no de la lògica de negoci de PINBAL -- per exemple, " +
					"un rebuig per capçalera de petició (Cookie inclosa) massa gran. Cos de la resposta: {}",
					operacio, response.getStatus(), contentType, body);
			return new RecobrimentException(ex.getMessage(), response.getStatus(), null);
		}
		String[] errorMessageParts = errorResponse.getMessage().split("\n", 2);
		String message = errorMessageParts.length > 0 ? errorMessageParts[0] : null;
		logger.error(
				"PINBAL ({}) ha retornat un error de validació de negoci HTTP {}: {}",
				operacio, response.getStatus(), message);
		return new RecobrimentException(message, response.getStatus(), errorResponse.getTrace());
	}

	private RecobrimentException toRecobrimentException(String operacio, ClientHandlerException ex) {
		boolean isErrorAutenticacio = ex.getMessage() != null && ex.getMessage().contains("media type text/html");
		if (isErrorAutenticacio) {
			logger.error(
					"PINBAL ({}) ha retornat una pàgina HTML en lloc de JSON -- probablement la sessió/autenticació " +
					"ha caducat o les credencials són incorrectes.", operacio);
			return new RecobrimentException("Error d'autenticació: usuari o contrasenya incorrectes.", 403, null);
		} else {
			logger.error(
					"Error de comunicació amb PINBAL ({}): {}", operacio, ex.getMessage(), ex);
			return new RecobrimentException(ex.getMessage(), 500, null);
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
