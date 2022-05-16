/**
 * 
 */
package es.caib.pinbal.core.service;

import java.net.HttpURLConnection;
import java.net.URL;

import es.caib.pinbal.core.helper.ConfigHelper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementació dels mètodes per obtenir dades de fonts
 * externes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class DadesExternesServiceImpl implements DadesExternesService {

	@Autowired
	private ConfigHelper configHelper;

	@Override
	public byte[] findProvincies() {
		log.debug("Cercant totes les províncies");
		try {
			URL url = new URL(getDadesComunesBaseUrl() + "/services/provincies/format/JSON/idioma/ca");
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			return IOUtils.toByteArray(httpConnection.getInputStream());
		} catch (Exception ex) {
			log.error("Error al obtenir les províncies de la font externa", ex);
			return null;
		}
	}

	@Override
	public byte[] findMunicipisPerProvincia(String provinciaCodi) {
		log.debug("Cercant els municipis de la província (provinciaCodi=" + provinciaCodi + ")");
		try {
			URL url = new URL(getDadesComunesBaseUrl() + "/services/municipis/" + provinciaCodi + "/format/JSON");
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			String resposta = new String(
					IOUtils.toByteArray(
							httpConnection.getInputStream()));
			boolean afegirCodiProvincia = false;
			String tokenInici = "{\"codi\":\""; // {"codi":"
			String tokenFi = "\","; // ",
			if (resposta.indexOf(tokenInici) != -1) {
				int indexInici = resposta.indexOf(tokenInici);
				int indexFi = resposta.indexOf(tokenFi, indexInici);
				String codi = resposta.substring(indexInici + tokenInici.length(), indexFi);
				if (codi.length() < 5) {
					afegirCodiProvincia = true;
				}
			}
			if (afegirCodiProvincia) {
				return resposta.replace(
						tokenInici,
						tokenInici + provinciaCodi).getBytes();
			} else {
				return resposta.getBytes();
			}
		} catch (Exception ex) {
			log.error("Error al obtenir les províncies de la font externa", ex);
			return null;
		}
	}

	@Override
	public byte[] findPaisos() {
		log.debug("Cercant tots els paisos");
		try {
			URL url = new URL(getDadesComunesBaseUrl() + "/services/paisos/format/JSON/idioma/ca");
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			return IOUtils.toByteArray(httpConnection.getInputStream());
		} catch (Exception ex) {
			log.error("Error al obtenir les províncies de la font externa", ex);
			return null;
		}
	}

	private String getDadesComunesBaseUrl() {
		return configHelper.getConfig("es.caib.pinbal.dadescomunes.base.url", "https://proves.caib.es/dadescomunsfront");
	}

}
