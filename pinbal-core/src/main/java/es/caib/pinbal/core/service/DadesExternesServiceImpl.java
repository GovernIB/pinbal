/**
 * 
 */
package es.caib.pinbal.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.pinbal.core.dto.IdiomaEnumDto;
import es.caib.pinbal.core.dto.dadesexternes.Municipi;
import es.caib.pinbal.core.dto.dadesexternes.Pais;
import es.caib.pinbal.core.dto.dadesexternes.PaisML;
import es.caib.pinbal.core.dto.dadesexternes.Provincia;
import es.caib.pinbal.core.dto.dadesexternes.ProvinciaML;
import es.caib.pinbal.core.helper.ConfigHelper;
import es.caib.pinbal.core.helper.UsuariHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	@Autowired
	private UsuariHelper usuariHelper;

	@Cacheable(value = "paisos", key = "#idioma")
	@Override
	public List<Pais> findPaisos(IdiomaEnumDto idioma) {
		log.debug("Cercant tots els paisos");
		if (idioma == null) {
			idioma = IdiomaEnumDto.CA;
		}

		List<Pais> paisos = new ArrayList<>();
		try {
			URL url = new URL(getDadesComunesBaseUrl() + "/services/paisos/format/JSON/idioma/" + idioma.name().toLowerCase());
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			byte[] response = IOUtils.toByteArray(httpConnection.getInputStream());

			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			if (response != null && response.length > 0) {
//				IdiomaEnumDto idiomaActual = usuariHelper.getIdiomaUsuariActual();

				List<PaisML> paisosML = mapper.readValue(response, new TypeReference<List<PaisML>>() {});
				for(PaisML paisML: paisosML) {
					String nom = getPaisNom(paisML, idioma);
					paisos.add(Pais.builder()
							.codi_numeric(paisML.getCodi_numeric())
							.alpha2(paisML.getAlpha2())
							.alpha3(paisML.getAlpha3())
							.nom(nom)
							.build());
				}
				final Collator collator = Collator.getInstance();
				collator.setStrength(Collator.PRIMARY);
				Collections.sort(paisos, new Comparator<Pais>() {
					@Override
					public int compare(Pais p1, Pais p2) {
						return collator.compare(p1.getNom(), p2.getNom());
					}
				});
			}
			return paisos;
		} catch (Exception ex) {
			log.error("Error al obtenir les províncies de la font externa", ex);
			return null;
		}
	}

	private String getPaisNom(PaisML paisML, IdiomaEnumDto idiomaActual) {

		if (IdiomaEnumDto.ES.equals(idiomaActual)) {
			return paisML.getNom_es() != null ? paisML.getNom_es() : paisML.getNom();
		}

		return paisML.getNom_ca() != null ? paisML.getNom_ca() : paisML.getNom();
	}

	@Cacheable(value = "provincies", key = "#idioma")
	@Override
	public List<Provincia> findProvincies(IdiomaEnumDto idioma) {
		log.debug("Cercant totes les províncies");

		if (idioma == null) {
			idioma = IdiomaEnumDto.CA;
		}

		List<Provincia> provincies = new ArrayList<>();
		try {
			URL url = new URL(getDadesComunesBaseUrl() + "/services/provincies/format/JSON/idioma/" + idioma.name().toLowerCase());
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			byte[] response = IOUtils.toByteArray(httpConnection.getInputStream());

			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			if (response != null && response.length > 0) {
//				IdiomaEnumDto idiomaActual = usuariHelper.getIdiomaUsuariActual();

				List<ProvinciaML> provinciesML = mapper.readValue(response, new TypeReference<List<ProvinciaML>>() {});
				for(ProvinciaML provinciaML: provinciesML) {
					String nom = getProvinciaNom(provinciaML, idioma);
					provincies.add(Provincia.builder()
							.codi(provinciaML.getCodi())
							.nom(nom)
							.build());
				}
				final Collator collator = Collator.getInstance();
				collator.setStrength(Collator.PRIMARY);
				Collections.sort(provincies, new Comparator<Provincia>() {
					@Override
					public int compare(Provincia p1, Provincia p2) {
						return collator.compare(p1.getNom(), p2.getNom());
					}
				});
			}
			return provincies;
		} catch (Exception ex) {
			log.error("Error al obtenir les províncies de la font externa", ex);
			return null;
		}
	}

	private String getProvinciaNom(ProvinciaML provinciaML, IdiomaEnumDto idiomaActual) {

		if (IdiomaEnumDto.ES.equals(idiomaActual)) {
			return provinciaML.getNom_es() != null ? provinciaML.getNom_es() : provinciaML.getNom();
		}

		return provinciaML.getNom_ca() != null ? provinciaML.getNom_ca() : provinciaML.getNom();
	}

	@Override
	@Cacheable(value = "municipis", key = "#provinciaCodi")
	public List<Municipi> findMunicipisPerProvincia(String provinciaCodi) {
		log.debug("Cercant els municipis de la província (provinciaCodi=" + provinciaCodi + ")");
		List<Municipi> municipis = new ArrayList<>();
		try {
			URL url = new URL(getDadesComunesBaseUrl() + "/services/municipis/" + provinciaCodi + "/format/JSON");
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			String resposta = new String(
					IOUtils.toByteArray(
							httpConnection.getInputStream()),
					StandardCharsets.UTF_8);
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
				resposta = resposta.replace(tokenInici, tokenInici + provinciaCodi);
			}

			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			if (resposta != null && !resposta.isEmpty()) {
				municipis = mapper.readValue(resposta, new TypeReference<List<Municipi>>() {});
				final Collator collator = Collator.getInstance();
				collator.setStrength(Collator.PRIMARY);
				Collections.sort(municipis, new Comparator<Municipi>() {
					@Override
					public int compare(Municipi m1, Municipi m2) {
						return collator.compare(m1.getNom(), m2.getNom());
					}
				});
			}
			return municipis;
		} catch (Exception ex) {
			log.error("Error al obtenir les províncies de la font externa", ex);
			return null;
		}
	}

	private String getDadesComunesBaseUrl() {
		return configHelper.getConfig("es.caib.pinbal.dadescomunes.base.url", "https://proves.caib.es/dadescomunsfront");
	}

}
