/**
 * 
 */
package es.caib.pinbal.plugin.caib.unitat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import es.caib.pinbal.plugin.PropertiesHelper;
import es.caib.pinbal.plugin.unitat.NodeDir3;
import es.caib.pinbal.plugin.unitat.UnitatOrganitzativa;
import es.caib.pinbal.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.pinbal.plugins.SistemaExternException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementació de proves del plugin d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatsOrganitzativesPluginDir3 implements UnitatsOrganitzativesPlugin {
	
	private static final String SERVEI_CERCA = "/rest/busqueda/";
	private static final String SERVEI_CATALEG = "/rest/catalogo/";
	private static final String SERVEI_UNITAT = "/rest/unidad/";
	private static final String SERVEI_UNITATS = "/rest/unidades/";
	private static final String SERVEI_ORGANIGRAMA = "/rest/organigrama/";
	private static final String SERVEI_OFICINES = "/rest/oficinas/";

	private static final String PROPERTY_SERVICE_URL = "es.caib.pinbal.plugin.unitats.organitzatives.dir3.service.url";
	private static final String PROPERTY_SERVICE_USERNAME = "es.caib.pinbal.plugin.unitats.organitzatives.dir3.service.username";
	private static final String PROPERTY_SERVICE_PASSWORD = "es.caib.pinbal.plugin.unitats.organitzatives.dir3.service.password";

	@Override
	public Map<String, NodeDir3> organigrama(String codiEntitat) throws SistemaExternException {
		Map<String, NodeDir3> organigrama = new HashMap<>();
		try {
			URL url = new URL(getServiceUrl() + SERVEI_ORGANIGRAMA + "?codigo=" + codiEntitat);
			logger.debug("URL: " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			byte[] response = IOUtils.toByteArray(httpConnection.getInputStream());
			if (response != null && response.length > 0) {
				NodeDir3 arrel = mapper.readValue(response, NodeDir3.class);
				nodeToOrganigrama(arrel, organigrama);
			}
			return organigrama;
		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut consultar l'organigrama de unitats organitzatives via REST (codiEntitat=" + codiEntitat + ")", ex);
		}
	}

	@Override
	public List<UnitatOrganitzativa> findAmbPare(String pareCodi) throws SistemaExternException {
		return findAmbPare(pareCodi, null, null);
	}

	public List<UnitatOrganitzativa> findAmbPare(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {

		try {
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<UnitatOrganitzativa> unitats = new ArrayList<>();
			List<UnidadTF> unidades = new ArrayList<>();
			URL url = new URL(getServiceUrl() + SERVEI_UNITATS + "obtenerArbolUnidades?codigo=" + pareCodi + "&denominacionCooficial=true" +
					(dataActualitzacio != null ? "&fechaActualizacion=" + sdf.format(dataActualitzacio) : "") +
					(dataSincronitzacio != null ? "&fechaSincronizacion=" + sdf.format(dataSincronitzacio) : ""));
			byte[] response = getResponse(url);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			if (response != null && response.length > 0) {
				unidades = mapper.readValue(response, new TypeReference<List<UnidadTF>>() {});
			}

			if (unidades != null) {
				for (UnidadTF unidad : unidades) {
					if ("V".equalsIgnoreCase(unidad.getCodigoEstadoEntidad())) {
						unitats.add(toUnitatOrganitzativa(unidad));
					}
				}
			}
			return unitats;
		} catch (Exception ex) {
			throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via REST (pareCodi=" + pareCodi + ")", ex);
		}
	}

	@Override
	public UnitatOrganitzativa findAmbCodi(String codi) throws SistemaExternException {
		return findAmbCodi(codi, null, null);
	}

	public UnitatOrganitzativa findAmbCodi(String codi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {
//		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			UnidadTF unidad = null;
			URL url = new URL(getServiceUrl() + SERVEI_UNITATS + "obtenerUnidad?codigo=" + codi +
					(dataActualitzacio != null ? "&fechaActualizacion=" + dataActualitzacio : "") +
					(dataSincronitzacio != null ? "&fechaSincronizacion=" + dataSincronitzacio : ""));
			byte[] response = getResponse(url);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			if (response != null && response.length > 0) {
				unidad = mapper.readValue(response, UnidadTF.class);
			}
			return unidad != null ? toUnitatOrganitzativa(unidad) : null;
		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut consultar la unitat organitzativa amb codi (codi=" + codi + ")", ex);
		}
	}

	@Override
	public List<UnitatOrganitzativa> cercaUnitats(
			String codi,
			String denominacio,
			Long nivellAdministracio,
			Long comunitatAutonoma,
			Boolean ambOficines,
			Boolean esUnitatArrel,
			Long provincia,
			String municipi) throws SistemaExternException {

		List<UnitatOrganitzativa> unitats = new ArrayList<>();
		List<NodeDir3> unidades = new ArrayList<>();
		try {
			URL url = new URL(getServiceUrl() + SERVEI_CERCA + "organismos?"
					+ "codigo=" + (codi != null ? codi : "")
					+ "&denominacion=" + (denominacio != null ? denominacio : "")
					+ "&codNivelAdministracion=" + (nivellAdministracio != null ? nivellAdministracio : "-1")
					+ "&codComunidadAutonoma=" + (comunitatAutonoma != null ? comunitatAutonoma : "-1")
					+ "&conOficinas=" + (ambOficines != null && ambOficines ? "true" : "false")
					+ "&unidadRaiz=" + (esUnitatArrel != null && esUnitatArrel ? "true" : "false")
					+ "&provincia="+ (provincia != null ? provincia : "-1")
					+ "&localidad=" + ((municipi != null && !municipi.isEmpty() )  ? municipi+"-01" : "-1")
					+ "&vigentes=true");
			logger.debug("URL: " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			byte[] response = IOUtils.toByteArray(httpConnection.getInputStream());
			if (response != null && response.length > 0) {
				unidades = mapper.readValue(response, TypeFactory.defaultInstance().constructCollectionType(List.class, NodeDir3.class));
				Collections.sort(unidades);
			}
			for (NodeDir3 node: unidades) {
				unitats.add(toUnitatOrganitzativa(node));
			}
			return unitats;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les unitats organitzatives via REST (" +
							"denominacio=" + denominacio + ", " +
							"nivellAdministracio=" + nivellAdministracio + ", " +
							"comunitatAutonoma=" + comunitatAutonoma + ", " +
							"ambOficines=" + ambOficines + ", " +
							"esUnitatArrel=" + esUnitatArrel + ", " +
							"provincia=" + provincia + ", " +
							"municipi=" + municipi + ")",
					ex);
		}
	}

	private UnitatOrganitzativa toUnitatOrganitzativa(UnidadTF unidad) {
		return new UnitatOrganitzativa(
				unidad.getCodigo(),
				unidad.getDenominacionCooficial() == null || unidad.getDenominacionCooficial().isEmpty() ? unidad.getDenominacion() : unidad.getDenominacionCooficial(),
				unidad.getCodigo(), // CifNif
				unidad.getFechaAltaOficial(),
				unidad.getCodigoEstadoEntidad(),
				unidad.getCodUnidadSuperior(),
				unidad.getCodUnidadRaiz(),
				unidad.getCodigoAmbPais(),
				unidad.getCodAmbComunidad(),
				unidad.getCodAmbProvincia(),
				unidad.getCodPostal(),
				unidad.getDescripcionLocalidad(),
				unidad.getCodigoTipoVia(),
				unidad.getNombreVia(),
				unidad.getNumVia());
	}

	private UnitatOrganitzativa toUnitatOrganitzativa(NodeDir3 unidad) {
		return new UnitatOrganitzativa(
				unidad.getCodi(),
				unidad.getDenominacio(),
				null,
				null,
				unidad.getEstat());
	}

	private void nodeToOrganigrama(NodeDir3 unitat, Map<String, NodeDir3> organigrama) {
		if (unitat.getEstat().startsWith("V") || unitat.getEstat().startsWith("T")) {	// Unitats Vigents o Transitòries
			organigrama.put(unitat.getCodi(), unitat);
			if (unitat.getFills() != null)
				for (NodeDir3 fill: unitat.getFills())
					nodeToOrganigrama(fill, organigrama);
		}
	}


	private byte[] getResponse(URL url) throws IOException {
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		httpConnection.setRequestMethod("GET");
		httpConnection.setRequestProperty("Authorization", createBasicAuthHeaderValue());
		httpConnection.setDoInput(true);
		httpConnection.setDoOutput(true);
		return IOUtils.toByteArray(httpConnection.getInputStream());
	}

	private String createBasicAuthHeaderValue() {
		String auth = getServiceUsername() + ":" + getServicePassword();
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
		return "Basic " + new String(encodedAuth);
	}

	private String getServiceUrl() {
		return PropertiesHelper.getProperties().getProperty(PROPERTY_SERVICE_URL);
	}

	private String getServiceUsername() {
		return PropertiesHelper.getProperties().getProperty(PROPERTY_SERVICE_USERNAME);
	}

	private String getServicePassword() {
		return PropertiesHelper.getProperties().getProperty(PROPERTY_SERVICE_PASSWORD);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(UnitatsOrganitzativesPluginDir3.class);
}
