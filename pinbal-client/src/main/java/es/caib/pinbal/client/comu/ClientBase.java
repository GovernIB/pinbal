/**
 * 
 */
package es.caib.pinbal.client.comu;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Client amb la lògica bàsica per a accedir al servei de consulta
 * d'estadístiques.
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
		init(urlBase, usuari, contrasenya, true, null, null);
	}

	protected ClientBase(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		init(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	protected <R> R restPeticioGet(
			String metode,
			Map<String, String> queryParams,
			Class<R> responseType) throws UniformInterfaceException, ClientHandlerException, IOException {
		MultivaluedMap<String, String> requestParams = new MultivaluedMapImpl();
		if (queryParams != null) {
			for (String key: queryParams.keySet()) {
				requestParams.add(key, queryParams.get(key));
			}
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

	protected <R> List<R> restPeticioGetList(
			String metode,
			Map<String, String> queryParams,
			Class<R> responseType) throws UniformInterfaceException, ClientHandlerException, IOException {
		MultivaluedMap<String, String> requestParams = new MultivaluedMapImpl();
		if (queryParams != null) {
			for (String key: queryParams.keySet()) {
				requestParams.add(key, queryParams.get(key));
			}
		}
		String urlAmbMetode = getUrlAmbMetode(metode);
		logger.debug("Enviant petició HTTP GET a PINBAL (" +
				"url=" + urlAmbMetode + ", " +
				"queryParams=" + queryParams + ")");
		List<R> response = jerseyClient.
				resource(urlAmbMetode).
				queryParams(requestParams).
				type("application/json").
				get(new GenericType<List<R>>(){});
		if (logger.isDebugEnabled()) {
			logger.debug("Rebuda resposta HTTP GET de PINBAL (" +
					"url=" + urlAmbMetode + ", " +
					"body=" + mapper.writeValueAsString(response) + ")");
		}
		return response;
	}

	protected String restPeticioGetString(
			String metode,
			Map<String, String> queryParams) throws UniformInterfaceException, ClientHandlerException, IOException {
		MultivaluedMap<String, String> requestParams = new MultivaluedMapImpl();
		if (queryParams != null) {
			for (String key: queryParams.keySet()) {
				requestParams.add(key, queryParams.get(key));
			}
		}
		String urlAmbMetode = getUrlAmbMetode(metode);
		logger.debug("Enviant petició HTTP GET a PINBAL (" +
				"url=" + urlAmbMetode + ", " +
				"queryParams=" + queryParams + ")");
		String response = jerseyClient.
				resource(urlAmbMetode).
				queryParams(requestParams).
				type("application/json").
				get(String.class);
		if (logger.isDebugEnabled()) {
			logger.debug("Rebuda resposta HTTP GET de PINBAL (" +
					"url=" + urlAmbMetode + ", " +
					"body=" + mapper.writeValueAsString(response) + ")");
		}
		return response;
	}

	protected ClientResponse restPeticioGetResponse(
			String metode,
			Map<String, String> queryParams,
			MediaType acceptedMediaType) throws UniformInterfaceException, ClientHandlerException, IOException {
		MultivaluedMap<String, String> requestParams = new MultivaluedMapImpl();
		if (queryParams != null) {
			for (String key: queryParams.keySet()) {
				requestParams.add(key, queryParams.get(key));
			}
		}
		String urlAmbMetode = getUrlAmbMetode(metode);
		logger.debug("Enviant petició HTTP GET a PINBAL (" +
				"url=" + urlAmbMetode + ", " +
				"queryParams=" + queryParams + ")");
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				queryParams(requestParams).
				type("application/json").
				accept(acceptedMediaType).
				get(ClientResponse.class);
		if (logger.isDebugEnabled()) {
			logger.debug("Rebuda resposta HTTP GET de PINBAL (" +
					"url=" + urlAmbMetode + ", " +
					"body=" + mapper.writeValueAsString(response) + ")");
		}
		return response;
	}

	protected <R> R restPeticioPost(
			String metode,
			Object request,
			Class<R> responseType,
			MediaType acceptedMediaType) throws UniformInterfaceException, ClientHandlerException, IOException {
		String urlAmbMetode = getUrlAmbMetode(metode);
		String body = mapper.writeValueAsString(request);
		logger.debug("Enviant petició HTTP POST a PINBAL (" +
				"url=" + urlAmbMetode + ", " +
				"body=" + body + ")");
		R response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				accept(acceptedMediaType).
				post(responseType, body);
		if (logger.isDebugEnabled()) {
			logger.debug("Rebuda resposta HTTP POST de PINBAL (" +
					"url=" + urlAmbMetode + ", " +
					"body=" + mapper.writeValueAsString(response) + ")");
		}
		return response;
	}

	protected String dateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		sdf.setTimeZone(TimeZone.getTimeZone("CET"));
		return sdf.format(date);
	}

	private String getUrlAmbMetode(String metode) {
		return urlBase + metode;
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
		if (usuari != null) {
			if (basicAuth) {
				logger.debug(
						"Autenticant REST amb autenticació de tipus HTTP basic (" +
								"usuari=" + usuari + ", " +
								"contrasenya=********)");
				jerseyClient.addFilter(
						new HTTPBasicAuthFilter(usuari, contrasenya));
			} else {
				logger.debug(
						"Autenticant client REST per a fer peticions cap a servei desplegat a damunt jBoss (" +
								"usuari=" + usuari + ", " +
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
		}
		//jerseyClient.addFilter(new LoggingFilter(System.out));
		mapper = new ObjectMapper();
		// Permet rebre un sol objecte en el lloc a on hi hauria d'haver una llista.
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		// Mecanisme de deserialització dels enums
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		// Per a no serialitzar propietats amb valors NULL
		mapper.setSerializationInclusion(Include.NON_NULL);
		
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	public void enableLogginFilter() {
		jerseyClient.addFilter(new LoggingFilter(System.out));
	}

	private static final Logger logger = LoggerFactory.getLogger(ClientBase.class);

}
