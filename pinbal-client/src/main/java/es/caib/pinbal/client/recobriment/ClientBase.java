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

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.util.MultivaluedMapImpl;

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

/**
 * Client amb la lògica bàsica per a accedir al servei de recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class ClientBase {

	private String urlBase;

	private Client jerseyClient;
	private ObjectMapper mapper;

	protected ClientBase(
			String urlBase,
			String usuari,
			String contrasenya) {
		super();
		init(urlBase, usuari, contrasenya, false, null, null);
	}

	protected ClientBase(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super();
		init(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	protected ScspRespuesta basePeticionSincrona(
			String serveiCodi,
			List<? extends SolicitudBase> solicituds) throws IOException {
		ScspPeticion peticion = toScspPeticion(serveiCodi, solicituds);
		ScspRespuesta response = restPeticioPost(
				"peticionSincrona",
				peticion,
				ScspRespuesta.class);
		return response;
	}

	protected ScspConfirmacionPeticion basePeticionAsincrona(
			String serveiCodi,
			List<? extends SolicitudBase> solicituds) throws IOException {
		ScspPeticion peticion = toScspPeticion(serveiCodi, solicituds);
		ScspConfirmacionPeticion response = restPeticioPost(
				"peticionSincrona",
				peticion,
				ScspConfirmacionPeticion.class);
		return response;
	}

	public ScspRespuesta getRespuesta(
			String idPeticion) throws IOException {
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("idPeticion", idPeticion);
		ScspRespuesta response = restPeticioGet(
				"getRespuesta",
				requestParams,
				ScspRespuesta.class);
		return response;
	}

	public ScspJustificante getJustificante(
			String idPeticion) throws IOException {
		return getJustificante(idPeticion, idPeticion);
	}

	public ScspJustificante getJustificante(
			String idPeticion,
			String idSolicitud) throws IOException {
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

	private void init(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		this.urlBase = urlBase;
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getClasses().add(JacksonJsonProvider.class);
		jerseyClient = Client.create(clientConfig);
		if (timeoutConnect != null) {
			jerseyClient.setConnectTimeout(timeoutConnect);
		}
		if (timeoutRead != null) {
			jerseyClient.setReadTimeout(timeoutRead);
		}
		//jerseyClient.addFilter(new LoggingFilter(System.out));
		jerseyClient.addFilter(
				new ClientFilter() {
					private ArrayList<Object> cookies;
					@Override
					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
						if (cookies != null) {
							request.getHeaders().put("Cookie", cookies);
						}
						ClientResponse response = getNext().handle(request);
						if (response.getCookies() != null) {
							if (cookies == null) {
								cookies = new ArrayList<Object>();
							}
							cookies.addAll(response.getCookies());
						}
						return response;
					}
				}
		);
		jerseyClient.addFilter(
				new ClientFilter() {
					@Override
					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
						ClientHandler ch = getNext();
				        ClientResponse resp = ch.handle(request);
				        if (resp.getStatusInfo().getFamily() != Response.Status.Family.REDIRECTION) {
				            return resp;
				        } else {
				            String redirectTarget = resp.getHeaders().getFirst("Location");
				            request.setURI(UriBuilder.fromUri(redirectTarget).build());
				            return ch.handle(request);
				        }
					}
				}
		);
		if (basicAuth) {
			logger.debug(
					"Autenticant REST amb autenticació de tipus HTTP basic (" +
					"usuari=" + usuari +
					"contrasenya=********)");
			jerseyClient.addFilter(
					new HTTPBasicAuthFilter(usuari, contrasenya));
		} else {
			logger.debug(
					"Autenticant client REST per a fer peticions cap a servei desplegat a damunt jBoss (" +
					"usuari=" + usuari +
					"contrasenya=********)");
			jerseyClient.resource(getUrlAmbMetode("getRespuesta")).get(String.class);
			Form form = new Form();
			form.putSingle("j_username", usuari);
			form.putSingle("j_password", contrasenya);
			jerseyClient.
			resource(urlBase + "/j_security_check").
			type("application/x-www-form-urlencoded").
			post(form);
		}
		//jerseyClient.addFilter(new LoggingFilter(System.out));
		mapper = new ObjectMapper();
		// Permet rebre un sol objecte en el lloc a on hi hauria d'haver una llista.
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		// Mecanisme de deserialització dels enums
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		// Per a no serialitzar propietats amb valors NULL
		mapper.setSerializationInclusion(Include.NON_NULL);
	}

	private <R> R restPeticioGet(
			String metode,
			Map<String, String> queryParams,
			Class<R> responseType) throws UniformInterfaceException, ClientHandlerException, IOException {
		MultivaluedMap<String, String> requestParams = new MultivaluedMapImpl();
		for (String key: queryParams.keySet()) {
			requestParams.add(key, queryParams.get(key));
		}
		String urlAmbMetode = getUrlAmbMetode(metode);
		logger.debug("Enviant petició HTTP GET a PINBAL (" +
				"url=" + urlAmbMetode + ", " +
				"queryParams=" + queryParams + ")");
		R response = jerseyClient.
				resource(urlAmbMetode).
				queryParams(requestParams).
				type("application/json").
				get(responseType);
		if (logger.isDebugEnabled()) {
			logger.debug("Rebuda resposta HTTP GET de PINBAL (" +
					"url=" + urlAmbMetode + ", " +
					"body=" + mapper.writeValueAsString(response) + ")");
		}
		return response;
	}

	private ClientResponse restPeticioGetResponse(
			String metode,
			Map<String, String> queryParams) throws UniformInterfaceException, ClientHandlerException, IOException {
		MultivaluedMap<String, String> requestParams = new MultivaluedMapImpl();
		for (String key: queryParams.keySet()) {
			requestParams.add(key, queryParams.get(key));
		}
		String urlAmbMetode = getUrlAmbMetode(metode);
		logger.debug("Enviant petició HTTP GET a PINBAL (" +
				"url=" + urlAmbMetode + ", " +
				"queryParams=" + queryParams + ")");
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				queryParams(requestParams).
				type("application/json").
				get(ClientResponse.class);
		if (logger.isDebugEnabled()) {
			logger.debug("Rebuda resposta HTTP GET de PINBAL (" +
					"url=" + urlAmbMetode + ", " +
					"body=" + mapper.writeValueAsString(response) + ")");
		}
		return response;
	}

	private <R> R restPeticioPost(
			String metode,
			Object request,
			Class<R> responseType) throws UniformInterfaceException, ClientHandlerException, IOException {
		String urlAmbMetode = getUrlAmbMetode(metode);
		String body = mapper.writeValueAsString(request);
		logger.debug("Enviant petició HTTP POST a PINBAL (" +
				"url=" + urlAmbMetode + ", " +
				"body=" + body + ")");
		R response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				post(responseType, body);
		if (logger.isDebugEnabled()) {
			logger.debug("Rebuda resposta HTTP POST de PINBAL (" +
					"url=" + urlAmbMetode + ", " +
					"body=" + mapper.writeValueAsString(response) + ")");
		}
		return response;
	}

	private String getUrlAmbMetode(String metode) {
		return urlBase + "/api/recobriment/" + metode;
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

	private static final Logger logger = LoggerFactory.getLogger(ClientBase.class);

}
