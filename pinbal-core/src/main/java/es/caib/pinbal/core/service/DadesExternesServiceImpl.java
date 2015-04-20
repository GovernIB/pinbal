/**
 * 
 */
package es.caib.pinbal.core.service;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.caib.pinbal.core.helper.PropertiesHelper;

/**
 * Implementació dels mètodes per obtenir dades de fonts
 * externes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DadesExternesServiceImpl implements DadesExternesService {

	@Override
	public byte[] findProvincies() {
		LOGGER.debug("Cercant totes les províncies");
		try {
			URL url = new URL(getDadesComunesBaseUrl() + "/services/provincies/format/JSON/idioma/ca");
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			return IOUtils.toByteArray(httpConnection.getInputStream());
		} catch (Exception ex) {
			LOGGER.error("Error al obtenir les províncies de la font externa", ex);
			return null;
		}
	}

	@Override
	public byte[] findMunicipisPerProvincia(String provinciaCodi) {
		LOGGER.debug("Cercant els municipis de la província (provinciaCodi=" + provinciaCodi + ")");
		try {
			URL url = new URL(getDadesComunesBaseUrl() + "/services/municipis/" + provinciaCodi + "/format/JSON");
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			return IOUtils.toByteArray(httpConnection.getInputStream());
		} catch (Exception ex) {
			LOGGER.error("Error al obtenir les províncies de la font externa", ex);
			return null;
		}
	}



	private String getDadesComunesBaseUrl() {
		String baseUrl = PropertiesHelper.getProperties().getProperty("es.caib.pinbal.dadescomunes.base.url");
		if (baseUrl != null && baseUrl.length() > 0) {
			return baseUrl;
		} else {
			return "https://proves.caib.es/dadescomunsfront";
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DadesExternesServiceImpl.class);

}
