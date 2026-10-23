package es.caib.pinbal.plugin.arxiu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import es.caib.comanda.ms.salut.helper.IntegracioApp;
import es.caib.pinbal.plugin.PluginMetricHelper;
import es.caib.pluginsib.arxiu.api.ArxiuException;
import es.caib.pluginsib.arxiu.api.ConsultaFiltre;
import es.caib.pluginsib.arxiu.api.ConsultaResultat;
import es.caib.pluginsib.arxiu.api.ContingutArxiu;
import es.caib.pluginsib.arxiu.api.ContingutOrigen;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentContingut;
import es.caib.pluginsib.arxiu.api.DocumentEstatElaboracio;
import es.caib.pluginsib.arxiu.api.DocumentExtensio;
import es.caib.pluginsib.arxiu.api.DocumentFormat;
import es.caib.pluginsib.arxiu.api.DocumentMetadades;
import es.caib.pluginsib.arxiu.api.DocumentTipus;
import es.caib.pluginsib.arxiu.api.Expedient;
import es.caib.pluginsib.arxiu.api.Firma;
import es.caib.pluginsib.arxiu.api.FirmaTipus;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ArxiuPluginCaib extends es.caib.pluginsib.arxiu.caib.ArxiuPluginCaib implements ArxiuPlugin {

    public static final String ARXIU_BASE_PROP = "plugin.arxiu.";

    private static final String ARXIUCAIB_BASE_PROP = ARXIU_BASE_PROP + "caib.";
    private static final String PLUGINSIB_ARXIUCAIB_BASE_PROP = "pluginsib.arxiu.caib.";
    private static final String CONCSV_URL_PROP = PLUGINSIB_ARXIUCAIB_BASE_PROP + "concsv.base.url";
    private static final String CONCSV_USUARI_PROP = PLUGINSIB_ARXIUCAIB_BASE_PROP + "concsv.usuari";
    private static final String CONCSV_CONTRASENYA_PROP = PLUGINSIB_ARXIUCAIB_BASE_PROP + "concsv.contrasenya";
    private static final String TIMEOUT_CONNECT_PROP = PLUGINSIB_ARXIUCAIB_BASE_PROP + "timeout.connect";
    private static final String TIMEOUT_READ_PROP = PLUGINSIB_ARXIUCAIB_BASE_PROP + "timeout.read";
    private static final String TIMEOUT_CONNECT = "10000";
    private static final String TIMEOUT_READ = "60000";
    private static final String ERROR_METADADES = "No ha estat possible obtenir les metadades del document amb UUID ";

    private Client versioImprimibleClient;

    @FunctionalInterface
    private interface ArxiuOp<T> {
        T call() throws ArxiuException;
    }

    public ArxiuPluginCaib() {
        throw new RuntimeException("No es possible instanciar la clase ArxiuCaibPluginImpl sense paràmetres");
    }

    public ArxiuPluginCaib(String propertyKeyBase) {
        throw new RuntimeException("No es possible instanciar la clase ArxiuCaibPluginImpl sense propietats");
    }

    public ArxiuPluginCaib(String propertyKeyBase, Properties properties) {
        super(propertyKeyBase, ambPropietatsPluginsib(propertyKeyBase, properties));
        PluginMetricHelper.addEndpoint(IntegracioApp.ARX, this.getProperty(PLUGINSIB_ARXIUCAIB_BASE_PROP + "base.url"));
    }

    /**
     * La llibreria pluginsib-arxiu-caib 3.x llegeix les propietats amb el prefix "pluginsib.arxiu.caib."
     * (a la 2.x era "plugin.arxiu.caib."). Per mantenir la configuració existent, es copia cada
     * propietat "plugin.arxiu.caib.X" a "pluginsib.arxiu.caib.X" si aquesta no està definida.
     */
    static Properties ambPropietatsPluginsib(String propertyKeyBase, Properties properties) {
        String base = propertyKeyBase != null ? propertyKeyBase : "";
        String prefixAntic = base + ARXIUCAIB_BASE_PROP;
        String prefixNou = base + PLUGINSIB_ARXIUCAIB_BASE_PROP;
        Properties resultat = new Properties();
        if (properties == null)
            return resultat;
        for (String clau: properties.stringPropertyNames()) {
            resultat.setProperty(clau, properties.getProperty(clau));
        }
        for (String clau: properties.stringPropertyNames()) {
            if (clau.startsWith(prefixAntic)) {
                String clauNova = prefixNou + clau.substring(prefixAntic.length());
                if (resultat.getProperty(clauNova) == null) {
                    resultat.setProperty(clauNova, properties.getProperty(clau));
                }
            }
        }
        return resultat;
    }

    private <T> T tracked(ArxiuOp<T> op) throws ArxiuException {
        try {
            long start = System.currentTimeMillis();
            T result = op.call();
            PluginMetricHelper.addSuccessOperation(IntegracioApp.ARX, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable ex) {
            PluginMetricHelper.addErrorOperation(IntegracioApp.ARX);
            throw ex;
        }
    }

    @Override
    public ContingutArxiu expedientCrear(Expedient expedient) throws ArxiuException {
        return tracked(() -> super.expedientCrear(expedient));
    }

    @Override
    public Expedient expedientDetalls(String identificador, String versio) throws ArxiuException {
        return tracked(() -> super.expedientDetalls(identificador, versio));
    }

    @Override
    public ConsultaResultat expedientConsulta(List<ConsultaFiltre> filtres, Integer pagina, Integer itemsPerPagina) throws ArxiuException {
        return tracked(() -> super.expedientConsulta(filtres, pagina, itemsPerPagina));
    }

    @Override
    public String expedientTancar(String identificador) throws ArxiuException {
        return tracked(() -> super.expedientTancar(identificador));
    }

    @Override
    public void expedientEsborrar(String identificador) throws ArxiuException {
        tracked(() -> { super.expedientEsborrar(identificador); return null; });
    }

    @Override
    public ContingutArxiu documentCrear(Document document, String identificadorPare) throws ArxiuException {
        return tracked(() -> super.documentCrear(document, identificadorPare));
    }

    @Override
    public Document documentDetalls(String identificador, String versio, boolean ambContingut) throws ArxiuException {
        if (!isConcsvConfigurat()) {
            // Sense ConCSV configurat, els detalls s'obtenen directament de l'arxiu (com a la versió 1.4.x)
            logger.debug("[ARXIU] No s'ha configurat la propietat " + CONCSV_URL_PROP + ". Obtenint els detalls del document " + identificador + " directament de l'arxiu");
            return tracked(() -> super.documentDetalls(identificador, versio, ambContingut));
        }
        return tracked(() -> {
            try {
                Document response = new Document();
                response.setIdentificador(identificador);
                if (ambContingut) {
                    // Es retorna sempre el document original. La versió imprimible s'ha de demanar explícitament amb documentImprimible.
                    response.setContingut(documentOriginal(identificador));
                }
                try {
                    var result = documentMetadades(identificador);
                    response.setMetadades(toDocumentMetadades(result));
                    response.setFirmes(result.containsKey("eni:tipoFirma") ? Collections.singletonList(toFirma(result.get("eni:tipoFirma"))) : null);
                } catch(Exception e) {
                    logger.warn(ERROR_METADADES + identificador + ": " + e.getMessage());
                    response.setMetadades(null);
                }
                return response;
            } catch (Exception var12) {
                throw new ArxiuException("S'ha produit un error obtenent els detalls del document: " + identificador, var12);
            }
        });
    }

//    @Override
//    public DocumentContingut documentImprimible(String identificador) throws ArxiuException {
//        return tracked(() -> {
//            try {
//                return getDocumentContingut(identificador);
//            } catch (Exception ex) {
//                logger.debug("S'ha produit un error generant la versió imprimible del document amb UUID " + identificador, ex);
//                throw new ArxiuException("S'ha produit un error generant la versió imprimible del document amb UUID " + identificador, ex);
//            }
//        });
//    }
//
//    /*
//     * La URL de consulta és la següent:
//     *   https://intranet.caib.es/concsv/rest/printable/uuid/IDENTIFICADOR?metadata1=METADADA_1&metadata2=METADADA_2&watermark=MARCA_AIGUA
//     * A on:
//     *   - IDENTIFICADOR és el UUID del document a consultar [OBLIGATORI]
//     *   - METADADA_1 és la primera metadada [OPCIONAL]
//     *   - METADADA_2 és la segona metadada [OPCIONAL]
//     *   - MARCA_AIGUA és el text de la marca d'aigua que apareixerà impresa a cada fulla [OPCIONAL]
//     * Només es obligatori informar la HASH, la resta d'elements son opcionals. Si no s'informen metadades s'imprimeix l'hora i dia de la generació del document imprimible.
//     */
//    @Nonnull
//    private DocumentContingut getDocumentContingut(String identificador) throws IOException {
//        InputStream is = generarVersioImprimible(identificador, null, null, null);
//        DocumentContingut contingut = new DocumentContingut();
//        contingut.setArxiuNom("versio_imprimible.pdf");
//        contingut.setTipusMime("application/pdf");
//        contingut.setContingut(IOUtils.toByteArray(is));
//        contingut.setTamany(contingut.getContingut().length);
//        return contingut;
//    }
//
//    private InputStream generarVersioImprimible(String identificador, String metadada1, String metadada2, String marcaAigua) {
//
//        String url = getPropertyConcsvUrl();
//        url += (url.endsWith("/") ? "" : "/") + "printable/uuid/";
//        WebResource webResource;
//        logger.debug("[ARXIU] Generant versio imprimible uuid. identificador " + identificador + " metadada1 " + metadada1 + " metadada2 " + metadada2 + " marcaAigua " + marcaAigua);
//        webResource = getVersioImprimibleClientConcsv().resource(url + (url.endsWith("/") ? "" : "/") + identificador);
//        if (metadada1 != null) {
//            webResource.queryParam("metadata1", metadada1);
//        }
//        if (metadada2 != null) {
//            webResource.queryParam("metadata2", metadada2);
//        }
//        if (marcaAigua != null) {
//            webResource.queryParam("watermark", marcaAigua);
//        }
//        return webResource.get(InputStream.class);
//    }

    private DocumentContingut documentOriginal(String identificador) throws ArxiuException {

        try {
            logger.info("[ConCSV] Recuperant versio original uuid. identificador " + identificador);
            String url = getPropertyConcsvUrlObligatoria();
            url += (url.endsWith("/") ? "" : "/") + "original/uuid/";
            var uuid = identificador.contains(":") ? identificador.substring(identificador.indexOf(':') + 1) : identificador;
            var webResource = getVersioImprimibleClientConcsv().resource(url + uuid);
            var is = webResource.get(InputStream.class);
            var contingut = new DocumentContingut();
            contingut.setContingut(IOUtils.toByteArray(is));
            contingut.setTipusMime(getMimeType(contingut.getContingut()));
            contingut.setArxiuNom("versio_original" + getMimeExtension(contingut.getTipusMime()));
            contingut.setTamany(contingut.getContingut().length);
            return contingut;
        } catch (Exception ex) {
            logger.debug("S'ha produit un error generant el document " + identificador, ex);
            throw new ArxiuException("S'ha produit un error recuperant el document original " + identificador, ex);
        }
    }

    private String getMimeExtension(String tipusMime) throws MimeTypeException {
        return MimeTypes.getDefaultMimeTypes().forName(tipusMime).getExtension();
    }

    private String getMimeType(byte[] contingut) {
        try {
            Tika tika = new Tika();
            return tika.detect(contingut);
        } catch (Exception ex) {
            logger.warn("No s'ha pogut detectar el tipus MIME del contingut", ex);
            return "application/octet-stream";
        }
    }

    private Map<String,Object> documentMetadades(String identificador) {
        try {
            String url = getPropertyConcsvUrlObligatoria();
            url += (url.endsWith("/") ? "" : "/") + "metadata/uuid/";
            logger.info("[ARXIU] Obtinguent metadades uuid del document " + identificador + " url " + url);
            var webResource = getVersioImprimibleClientConcsv().resource(url + identificador);
            var jsonData = webResource.accept(MediaType.APPLICATION_JSON).get(String.class);
            return new ObjectMapper().readValue(jsonData, HashMap.class);
        } catch (Exception ex) {
            var msg = ERROR_METADADES + identificador;
            logger.debug(msg, ex);
            throw new ArxiuException(msg, ex);
        }
    }

    /**
     * Construeix la firma a partir del valor de la metadada "eni:tipoFirma" (TF01..TF09 o el nom del tipus).
     * Si el valor no es reconeix, la firma es retorna sense tipus.
     */
    static Firma toFirma(Object tipoFirma) {
        Firma firma = new Firma();
        if (tipoFirma != null) {
            try {
                firma.setTipus(FirmaTipus.toEnum(tipoFirma.toString()));
            } catch (Exception ex) {
                logger.debug("Tipus de firma no reconegut: " + tipoFirma);
            }
        }
        return firma;
    }

    private static DocumentMetadades toDocumentMetadades(Map<String,Object> metadatas) throws ArxiuException {

        DocumentMetadades metadades = new DocumentMetadades();
        for (var entry : metadatas.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            if ("eni:id".equals(entry.getKey())) {
                metadades.setIdentificador(value.toString());
            } else if ("eni:v_nti".equals(entry.getKey())) {
                metadades.setVersioNti(value.toString());
            } else if ("eni:origen".equals(entry.getKey())) {
                metadades.setOrigen(ContingutOrigen.toEnum(String.valueOf(value)));
            } else if ("eni:fecha_inicio".equals(entry.getKey())) {
                metadades.setDataCaptura(parseDateIso8601(value.toString()));
            } else if ("eni:estado_elaboracion".equals(entry.getKey())) {
                metadades.setEstatElaboracio(DocumentEstatElaboracio.toEnum(value.toString()));
            } else if ("eni:tipo_doc_ENI".equals(entry.getKey())) {
                metadades.setTipusDocumental(DocumentTipus.toEnum(value.toString()));
            } else {
                Object val;
                if ("eni:organo".equals(entry.getKey())) {
                    val = value;
                    if (val instanceof List) {
                        metadades.setOrgans((List<String>) value);
                    } else {
                        metadades.setOrgans(Arrays.asList((String) val));
                    }
                } else if ("eni:nombre_formato".equals(entry.getKey())) {
                    metadades.setFormat(DocumentFormat.toEnum(value.toString()));
                } else if ("eni:extension_formato".equals(entry.getKey())) {
                    metadades.setExtensio(DocumentExtensio.toEnum(value.toString()));
                } else {
                    Map<String, Object> metadadesAddicionals = metadades.getMetadadesAddicionals();
                    if (metadadesAddicionals == null) {
                        metadadesAddicionals = new HashMap<>();
                        metadades.setMetadadesAddicionals(metadadesAddicionals);
                    }
                    metadadesAddicionals.put(entry.getKey(), value);
                }
            }
        }
        return metadades;
    }

    private static Date parseDateIso8601(String date) throws ArxiuException {
        if (date == null) {
            return null;
        }
        try {
            return Date.from(OffsetDateTime.parse(date).toInstant());
        } catch (DateTimeParseException e) {
            throw new ArxiuException("No s'ha pogut parsejar el valor de la data (valor=" + date + ")");
        }
    }

    private Client getVersioImprimibleClientConcsv() {

        if (versioImprimibleClient != null) {
            return versioImprimibleClient;
        }
        versioImprimibleClient = Client.create();
        versioImprimibleClient.setConnectTimeout(getPropertyTimeoutConnect());
        versioImprimibleClient.setReadTimeout(getPropertyTimeoutRead());
        String usuari = getPropertyConcsvUsuari();
        String contrasenya = getPropertyConcsvContrasenya();
        if (usuari != null) {
            versioImprimibleClient.addFilter(new HTTPBasicAuthFilter(usuari, contrasenya));
        }
        return versioImprimibleClient;
    }

    private String getPropertyConcsvUrl() {
        return this.getProperty(CONCSV_URL_PROP);
    }

    private boolean isConcsvConfigurat() {
        String url = getPropertyConcsvUrl();
        return url != null && !url.isBlank();
    }

    private String getPropertyConcsvUrlObligatoria() throws ArxiuException {
        if (!isConcsvConfigurat()) {
            throw new ArxiuException("No s'ha configurat la propietat " + CONCSV_URL_PROP + " amb la URL base del servei ConCSV");
        }
        return getPropertyConcsvUrl();
    }

    private String getPropertyConcsvUsuari() {
        return this.getProperty(CONCSV_USUARI_PROP);
    }

    private String getPropertyConcsvContrasenya() {
        return this.getProperty(CONCSV_CONTRASENYA_PROP);
    }

    private int getPropertyTimeoutConnect() {
        var timeout = this.getProperty(TIMEOUT_CONNECT_PROP, TIMEOUT_CONNECT);
        return Integer.parseInt(timeout);
    }

    private int getPropertyTimeoutRead() {
        var timeout = this.getProperty(TIMEOUT_READ_PROP, TIMEOUT_READ);
        return Integer.parseInt(timeout);
    }

    private static final Logger logger = LoggerFactory.getLogger(ArxiuPluginCaib.class);
}
