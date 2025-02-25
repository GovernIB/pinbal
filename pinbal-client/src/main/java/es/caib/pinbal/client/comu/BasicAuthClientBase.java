/**
 * 
 */
package es.caib.pinbal.client.comu;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
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
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Client amb la lògica bàsica per a accedir al servei de consulta
 * d'estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class BasicAuthClientBase {

	private String urlBase;
	private Client jerseyClient;
	protected ObjectMapper mapper;
	protected LogLevel logLevel;

	protected BasicAuthClientBase(
			String urlBase,
			String usuari,
			String contrasenya) {
		init(urlBase, usuari, contrasenya, null, null, LogLevel.INFO);
	}

	protected BasicAuthClientBase(
			String urlBase,
			String usuari,
			String contrasenya,
			LogLevel logLevel) {
		init(urlBase, usuari, contrasenya, null, null, logLevel);
	}

	protected BasicAuthClientBase(
			String urlBase,
			String usuari,
			String contrasenya,
			Integer timeoutConnect,
			Integer timeoutRead) {
		init(urlBase, usuari, contrasenya, timeoutConnect, timeoutRead, LogLevel.INFO);
	}

	protected BasicAuthClientBase(
			String urlBase,
			String usuari,
			String contrasenya,
			Integer timeoutConnect,
			Integer timeoutRead,
			LogLevel logLevel) {
		init(urlBase, usuari, contrasenya, timeoutConnect, timeoutRead, logLevel);
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
		logDebug("Enviant petició HTTP GET a PINBAL (url=" + urlAmbMetode + ", queryParams=" + queryParams + ")");
		R response = jerseyClient.
				resource(urlAmbMetode).
				queryParams(requestParams).
				type("application/json").
				get(responseType);
		logDebug("Rebuda resposta HTTP GET de PINBAL (url=" + urlAmbMetode + ", body=" + mapper.writeValueAsString(response) + ")");
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
		logDebug("Enviant petició HTTP GET a PINBAL (url=" + urlAmbMetode + ", queryParams=" + queryParams + ")");
		List<R> response = jerseyClient.
				resource(urlAmbMetode).
				queryParams(requestParams).
				type("application/json").
				get(new GenericType<List<R>>(){});
		logDebug("Rebuda resposta HTTP GET de PINBAL (url=" + urlAmbMetode + ", body=" + mapper.writeValueAsString(response) + ")");
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
		logDebug("Enviant petició HTTP GET a PINBAL (url=" + urlAmbMetode + ", queryParams=" + queryParams + ")");
		String response = jerseyClient.
				resource(urlAmbMetode).
				queryParams(requestParams).
				type("application/json").
				get(String.class);
		logDebug("Rebuda resposta HTTP GET de PINBAL (url=" + urlAmbMetode + ", body=" + mapper.writeValueAsString(response) + ")");
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
		logDebug("Enviant petició HTTP GET a PINBAL (url=" + urlAmbMetode + ", queryParams=" + queryParams + ")");
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				queryParams(requestParams).
				type("application/json").
				accept(acceptedMediaType).
				get(ClientResponse.class);
		logDebug("Rebuda resposta HTTP GET de PINBAL (url=" + urlAmbMetode + ", body=" + mapper.writeValueAsString(response) + ")");
		return response;
	}

	protected <R> R restPeticioPost(
			String metode,
			Object request,
			Class<R> responseType,
			MediaType acceptedMediaType) throws UniformInterfaceException, ClientHandlerException, IOException {
		String urlAmbMetode = getUrlAmbMetode(metode);
		String body = mapper.writeValueAsString(request);
		logDebug("Enviant petició HTTP POST a PINBAL (url=" + urlAmbMetode + ", body=" + body + ")");
		R response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				accept(acceptedMediaType).
				post(responseType, body);
		logDebug("Rebuda resposta HTTP POST de PINBAL (url=" + urlAmbMetode + ", body=" + mapper.writeValueAsString(response) + ")");
		return response;
	}

	protected String simpleDateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("CET"));
		return sdf.format(date);
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
			Integer timeoutConnect,
			Integer timeoutRead,
			LogLevel logLevel) {
		this.urlBase = urlBase;
		this.logLevel = logLevel;
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
//		jerseyClient.addFilter(new PatchRequestFilter());
		if (logLevel.isDebugEnabled()) {
			enableLogginFilter();
		}
		if (usuari != null) {
			logDebug("Autenticant REST amb autenticació de tipus HTTP basic (usuari=" + usuari + ", contrasenya=********)");
			jerseyClient.addFilter(new HTTPBasicAuthFilter(usuari, contrasenya));
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
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public void enableLogginFilter() {
		jerseyClient.addFilter(new LoggingFilter(System.out));
	}

	private static final Logger logger = LoggerFactory.getLogger(BasicAuthClientBase.class);

	private void logTrace(String message) {
		if (logLevel.isTraceEnabled()) {
			logger.trace(message);
		}
	}
	private void logDebug(String message) {
		if (logLevel.isDebugEnabled()) {
			logger.debug(message);
		}
	}
	private void logInfo(String message) {
		if (logLevel.isInfoEnabled()) {
			logger.info(message);
		}
	}
	private void logWarn(String message) {
		if (logLevel.isWarnEnabled()) {
			logger.warn(message);
		}
	}
	private void logError(String message) {
		if (logLevel.isErrorEnabled()) {
			logger.error(message);
		}
	}

	protected String getErrorMessage(ClientResponse response) {
		// Llegeix el contingut de la resposta com a String.
		if (!response.hasEntity()) {
			return response.toString();
		}

		String entity = response.getEntity(String.class);
		try {
			// Intenta transformar el text en un objecte ErrorResponse.
			ErrorResponse errorResponse = mapper.readValue(entity, ErrorResponse.class);
			if (errorResponse != null && errorResponse.getErrorMessage() != null && !errorResponse.getErrorMessage().isEmpty())
				return errorResponse.getErrorMessage();
			else
				return response.toString();
		} catch (Exception e) {
			// Si hi ha error deserialitzant, retorna l'entity com a String.
			return entity;
//            return response.toString();
		}
	}

	protected void processVoidResponse(ClientResponse response) throws IOException {
		String errorMsg = "";
		int statusCode = response.getStatus();
		switch(statusCode) {
			case 200:
				logDebug("Operació realitzada amb èxit: " + response.getLocation());
				break;
			case 400:
				errorMsg = "Entrada invàlida: " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
			case 401:
				errorMsg = "Accés denegat: " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
			case 404:
				errorMsg = "Recurs no trobat : " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
			case 500:
				errorMsg = "Resposta inesperada del servidor: " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
		}
	}

	protected <T> T processResponse(ClientResponse response, Class<T> clazz) throws IOException {
		String errorMsg = "";
		int statusCode = response.getStatus();
		switch(statusCode) {
			case 200:
				logDebug("Operació realitzada amb èxit: " + response.getLocation());
				break;
			case 204:
				logInfo("Sense contingut (" + clazz.getSimpleName() + ")");
				return null;
			case 400:
				errorMsg = "Entrada invàlida: " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
			case 401:
				errorMsg = "Accés denegat: " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
			case 404:
				errorMsg = "Recurs no trobat : " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
			case 500:
				errorMsg = "Resposta inesperada del servidor: " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
		}

		if (!response.hasEntity()) {
			return null;
		}
		String jsonOutput = response.getEntity(String.class);
		T recurs =  mapper.readValue(jsonOutput, clazz);
		return recurs;
	}

	protected <T> List<T> processListResponse(ClientResponse response, Class<T> clazz) throws IOException {
		String errorMsg = "";
		int statusCode = response.getStatus();
		switch(statusCode) {
			case 200:
				logDebug("Operació realitzada amb èxit: " + response.getLocation());
				break;
			case 204:
				logInfo("Sense contingut (" + clazz.getSimpleName() + ")");
				return null;
			case 400:
				errorMsg = "Entrada invàlida: " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
			case 401:
				errorMsg = "Accés denegat: " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
			case 404:
				errorMsg = "Recurs no trobat : " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
			case 500:
				errorMsg = "Resposta inesperada del servidor: " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
		}

		if (!response.hasEntity()) {
			return null;
		}
		String jsonOutput = response.getEntity(String.class);
		JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
		List<T> recurs =  mapper.readValue(jsonOutput, listType);
		return recurs;
	}

	protected <T> Page<T> processPagedResponse(ClientResponse response, Class<T> clazz, int page, int size, String sort) throws IOException {
		String errorMsg = "";
		int statusCode = response.getStatus();
		switch(statusCode) {
			case 200:
				logDebug("Operació realitzada amb èxit: " + response.getLocation());
				break;
			case 204:
				logInfo("Sense continguts (" + clazz.getSimpleName() + ")");
				return Page.<T>builder().number(page).size(size).totalPages(0).totalElements(0).content(new ArrayList<T>()).build();
			case 400:
				errorMsg = "Entrada invàlida: " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
			case 401:
				errorMsg = "Accés denegat: " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
			case 404:
				errorMsg = "Recurs no trobat: " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
			case 500:
				errorMsg = "Resposta inesperada del servidor: " + getErrorMessage(response);
				logError(errorMsg);
				throw new RuntimeException(errorMsg);
		}

		String jsonOutput = response.getEntity(String.class);
		PagedResources<T> usuaris = mapper.readValue(jsonOutput, mapper.getTypeFactory().constructParametricType(PagedResources.class, clazz));

		Page<T> resultPage = new Page<>();
		resultPage.setContent(usuaris.getContent() );
		resultPage.setSize(usuaris.getPage().getSize());
		resultPage.setTotalElements(usuaris.getPage().getTotalElements());
		resultPage.setTotalPages(usuaris.getPage().getTotalPages());
		resultPage.setNumber(usuaris.getPage().getNumber());

		return resultPage;
	}

}
