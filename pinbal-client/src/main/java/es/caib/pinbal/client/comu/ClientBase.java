package es.caib.pinbal.client.comu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Client amb la lògica bàsica per a accedir al servei de consulta
 * d'estadístiques.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class ClientBase {

	private String urlBase;
	private Client rsClient;
	protected ObjectMapper mapper;
	protected LogLevel logLevel;

	protected ClientBase(
			String urlBase,
			String usuari,
			String contrasenya) {
		init(urlBase, usuari, contrasenya, true, null, null, LogLevel.INFO);
	}

	protected ClientBase(
			String urlBase,
			String usuari,
			String contrasenya,
			LogLevel logLevel) {
		init(urlBase, usuari, contrasenya, true, null, null, logLevel);
	}

	protected ClientBase(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		init(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead, LogLevel.INFO);
	}

	protected ClientBase(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead,
			LogLevel logLevel) {
		init(urlBase, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead, logLevel);
	}

	protected Response restPeticioGet(
			String metode,
			Map<String, String> queryParams) throws IOException {
		String urlAmbMetode = getUrlAmbMetode(metode);
		logDebug("Enviant petició HTTP GET a PINBAL (url=" + urlAmbMetode + ", queryParams=" + queryParams + ")");
		WebTarget target = rsClient.target(urlAmbMetode);
		if (queryParams != null) {
			for (Map.Entry<String, String> entry : queryParams.entrySet()) {
				if (entry.getValue() != null) {
					target = target.queryParam(entry.getKey(), entry.getValue());
				}
			}
		}
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		logDebug("Rebuda resposta HTTP GET de PINBAL (url=" + urlAmbMetode + ", status=" + response.getStatus() + ")");
		return response;
	}

	protected <R> List<R> restPeticioGetList(
			String metode,
			Map<String, String> queryParams,
			Class<R> responseType) throws IOException {
		Response response = restPeticioGet(metode, queryParams);
		return processListResponse(response, responseType);
	}

	protected String restPeticioGetString(
			String metode,
			Map<String, String> queryParams) throws IOException {
		Response response = restPeticioGet(metode, queryParams);
		try {
			response.bufferEntity();
			int statusCode = response.getStatus();
			if (statusCode != 200) {
				throw new RuntimeException("Error HTTP " + statusCode + ": " + getErrorMessage(response));
			}
			return response.readEntity(String.class);
		} finally {
			response.close();
		}
	}

	protected Response restPeticioGetResponse(
			String metode,
			Map<String, String> queryParams,
			MediaType acceptedMediaType) throws IOException {
		String urlAmbMetode = getUrlAmbMetode(metode);
		logDebug("Enviant petició HTTP GET a PINBAL (url=" + urlAmbMetode + ", queryParams=" + queryParams + ")");
		WebTarget target = rsClient.target(urlAmbMetode);
		if (queryParams != null) {
			for (Map.Entry<String, String> entry : queryParams.entrySet()) {
				if (entry.getValue() != null) {
					target = target.queryParam(entry.getKey(), entry.getValue());
				}
			}
		}
		Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON_TYPE);
		if (acceptedMediaType != null) {
			builder = builder.accept(acceptedMediaType);
		}
		Response response = builder.get();
		logDebug("Rebuda resposta HTTP GET de PINBAL (url=" + urlAmbMetode + ", status=" + response.getStatus() + ")");
		return response;
	}

	protected Response restPeticioPost(
			String metode,
			Object request,
			MediaType acceptedMediaType) throws IOException {
		String urlAmbMetode = getUrlAmbMetode(metode);
		String body = mapper.writeValueAsString(request);
		logDebug("Enviant petició HTTP POST a PINBAL (url=" + urlAmbMetode + ", body=" + body + ")");
		Invocation.Builder builder = rsClient.target(urlAmbMetode).request();
		if (acceptedMediaType != null) {
			builder = builder.accept(acceptedMediaType);
		}
		Response response = builder.post(Entity.json(body));
		logDebug("Rebuda resposta HTTP POST de PINBAL (url=" + urlAmbMetode + ", status=" + response.getStatus() + ")");
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
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead,
			LogLevel logLevel) {
		this.urlBase = urlBase;
		this.logLevel = logLevel;
		mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		CookieFilter cookieFilter = new CookieFilter();
		ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
		builder.setFollowRedirects(true);
		builder.register(new JacksonJsonProvider(mapper));
		builder.register(cookieFilter);
		if (timeoutConnect != null) {
			builder.connectTimeout(timeoutConnect, TimeUnit.MILLISECONDS);
		}
		if (timeoutRead != null) {
			builder.readTimeout(timeoutRead, TimeUnit.MILLISECONDS);
		}
		if (usuari != null && basicAuth) {
			final String credentials = Base64.getEncoder().encodeToString(
					(usuari + ":" + contrasenya).getBytes(StandardCharsets.UTF_8));
			builder.register((ClientRequestFilter) ctx ->
					ctx.getHeaders().putSingle("Authorization", "Basic " + credentials));
			logDebug("Autenticant REST amb autenticació de tipus HTTP basic (usuari=" + usuari + ", contrasenya=********)");
		}
		rsClient = builder.build();
		if (usuari != null && !basicAuth) {
			logDebug("Autenticant client REST per a fer peticions cap a servei desplegat a damunt jBoss (usuari=" + usuari + ", contrasenya=********)");
			rsClient.target(getUrlAmbMetode("getRespuesta")).request().get().close();
			Form form = new Form();
			form.param("j_username", usuari);
			form.param("j_password", contrasenya);
			rsClient.target(urlBase + "/j_security_check").request()
					.post(Entity.form(form)).close();
		}
	}

	public void enableLogginFilter() {
		// No-op: request/response details are logged via logDebug
	}

	protected String getErrorMessage(Response response) {
		if (!response.hasEntity()) {
			return String.valueOf(response.getStatus());
		}
		String entity = response.readEntity(String.class);
		try {
			ErrorResponse errorResponse = mapper.readValue(entity, ErrorResponse.class);
			if (errorResponse != null && errorResponse.getErrorMessage() != null
					&& !errorResponse.getErrorMessage().isEmpty()) {
				return errorResponse.getErrorMessage();
			}
			return entity;
		} catch (Exception e) {
			return entity;
		}
	}

	protected void processVoidResponse(Response response) throws IOException {
		try {
			response.bufferEntity();
			String errorMsg;
			int statusCode = response.getStatus();
			switch (statusCode) {
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
				default:
					break;
			}
		} finally {
			response.close();
		}
	}

	protected <T> T processResponse(Response response, Class<T> clazz) throws IOException {
		try {
			response.bufferEntity();
			String errorMsg;
			int statusCode = response.getStatus();
			switch (statusCode) {
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
				default:
					break;
			}
			if (!response.hasEntity()) {
				return null;
			}
			String jsonOutput = response.readEntity(String.class);
			if (String.class.equals(clazz)) {
				return clazz.cast(jsonOutput);
			}
			return mapper.readValue(jsonOutput, clazz);
		} finally {
			response.close();
		}
	}

	protected <T> List<T> processListResponse(Response response, Class<T> clazz) throws IOException {
		try {
			response.bufferEntity();
			String errorMsg;
			int statusCode = response.getStatus();
			switch (statusCode) {
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
				default:
					break;
			}
			if (!response.hasEntity()) {
				return null;
			}
			String jsonOutput = response.readEntity(String.class);
			JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
			return mapper.readValue(jsonOutput, listType);
		} finally {
			response.close();
		}
	}

	protected <T> Page<T> processPagedResponse(Response response, Class<T> clazz, int page, int size, String sort) throws IOException {
		try {
			response.bufferEntity();
			String errorMsg;
			int statusCode = response.getStatus();
			switch (statusCode) {
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
				default:
					break;
			}
			String jsonOutput = response.readEntity(String.class);
			PagedResources<T> pagedResources = mapper.readValue(jsonOutput,
					mapper.getTypeFactory().constructParametricType(PagedResources.class, clazz));
			Page<T> resultPage = new Page<>();
			resultPage.setContent(pagedResources.getContent());
			resultPage.setSize(pagedResources.getPage().getSize());
			resultPage.setTotalElements(pagedResources.getPage().getTotalElements());
			resultPage.setTotalPages(pagedResources.getPage().getTotalPages());
			resultPage.setNumber(pagedResources.getPage().getNumber());
			return resultPage;
		} finally {
			response.close();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ClientBase.class);

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

	private static class CookieFilter implements ClientRequestFilter, ClientResponseFilter {
		private final List<Object> cookies = new ArrayList<>();

		@Override
		public void filter(ClientRequestContext requestContext) {
			if (!cookies.isEmpty()) {
				requestContext.getHeaders().put("Cookie", new ArrayList<>(cookies));
			}
		}

		@Override
		public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
			for (NewCookie cookie : responseContext.getCookies().values()) {
				cookies.add(cookie.getName() + "=" + cookie.getValue());
			}
		}
	}

}
