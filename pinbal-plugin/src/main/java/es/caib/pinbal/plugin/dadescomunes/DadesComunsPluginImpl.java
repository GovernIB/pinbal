package es.caib.pinbal.plugin.dadescomunes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.pinbal.plugin.PluginMetricHelper;
import es.caib.pinbal.plugin.SistemaExternException;
import es.caib.pinbal.plugin.dadescomuns.*;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.*;

public class DadesComunsPluginImpl implements DadesComunsPlugin {

    private final Properties properties;
    public DadesComunsPluginImpl(Properties properties) {
        this.properties = properties;
        PluginMetricHelper.addEndpoint(IntegracioApp.DCM, getDadesComunesBaseUrl());
    }

    @Override
    public List<Pais> findPaisos(String idioma) throws SistemaExternException {
        if (Strings.isBlank(idioma) || !"ES".equalsIgnoreCase(idioma)) {
            idioma = "CA";
        }
        idioma = idioma.toUpperCase();

        List<Pais> paisos = new ArrayList<>();

        try {
            URL url = new URL(getDadesComunesBaseUrl() + "/services/paisos/format/JSON/idioma/" + idioma.toLowerCase());
            HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            byte[] response = IOUtils.toByteArray(httpConnection.getInputStream());

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            if (response != null && response.length > 0) {
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
            LOGGER.error("Error al obtenir els països del servei de dades comunes", ex);
            throw new SistemaExternException(ex);
        }
    }

    private String getPaisNom(PaisML paisML, String idiomaActual) {

        if ("ES".equals(idiomaActual)) {
            return paisML.getNom_es() != null ? paisML.getNom_es() : paisML.getNom();
        }
        return paisML.getNom_ca() != null ? paisML.getNom_ca() : paisML.getNom();
    }

    @Override
    public List<Provincia> findProvincies(String idioma) throws SistemaExternException {
        if (Strings.isBlank(idioma) || !"ES".equalsIgnoreCase(idioma)) {
            idioma = "CA";
        }
        idioma = idioma.toUpperCase();

        List<Provincia> provincies = new ArrayList<>();

        try {
            URL url = new URL(getDadesComunesBaseUrl() + "/services/provincies/format/JSON/idioma/" + idioma.toLowerCase());
            HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            byte[] response = IOUtils.toByteArray(httpConnection.getInputStream());

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            if (response != null && response.length > 0) {
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
            LOGGER.error("Error al obtenir les províncies del servei de dades comunes", ex);
            throw new SistemaExternException(ex);
        }
    }

    private String getProvinciaNom(ProvinciaML provinciaML, String idiomaActual) {
        if ("ES".equals(idiomaActual)) {
            return provinciaML.getNom_es() != null ? provinciaML.getNom_es() : provinciaML.getNom();
        }
        return provinciaML.getNom_ca() != null ? provinciaML.getNom_ca() : provinciaML.getNom();
    }

    @Override
    public List<Municipi> findMunicipisPerProvincia(String provinciaCodi) throws SistemaExternException {
        List<Municipi> municipis = new ArrayList<>();

        // Dades per el monitor
        String accioDescripcio = "Consulta de municipis per província";
        Map<String, String> accioParams = new HashMap<>();
        accioParams.put("provinciaCodi", provinciaCodi);
        long startTime = System.currentTimeMillis();

        try {
            URL url = new URL(getDadesComunesBaseUrl() + "/services/municipis/" + provinciaCodi + "/format/JSON");
            accioParams.put("url", url.toString());
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
            LOGGER.error("Error al obtenir les els municipis per província del servei de dades comunes", ex);
            throw new SistemaExternException(ex);
        }
    }

    private String getDadesComunesBaseUrl() {
        return properties.getProperty("es.caib.pinbal.dadescomunes.base.url", "https://proves.caib.es/dadescomunsfront");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DadesComunsPluginImpl.class);
}
