package es.caib.pinbal.plugin.arxiu;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import es.caib.pluginsib.arxiu.api.ArxiuException;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentContingut;
import es.caib.pluginsib.arxiu.api.DocumentMetadades;
import es.caib.pluginsib.arxiu.api.FirmaTipus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class ArxiuPluginCaibTest {

    private HttpServer server;

    @AfterEach
    void aturarServidor() {
        if (server != null) {
            server.stop(0);
        }
    }

    private ArxiuPluginCaib crearPlugin() throws IOException {
        return crearPlugin("plugin.arxiu.caib.");
    }

    private ArxiuPluginCaib crearPlugin(String prefix) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        Properties properties = new Properties();
        properties.setProperty(prefix + "concsv.base.url", "http://127.0.0.1:" + server.getAddress().getPort());
        properties.setProperty(prefix + "concsv.usuari", "usuariConcsv");
        properties.setProperty(prefix + "concsv.contrasenya", "contrasenyaConcsv");
        properties.setProperty(prefix + "timeout.connect", "1000");
        properties.setProperty(prefix + "timeout.read", "1000");
        return new ArxiuPluginCaib("", properties);
    }

    private void respon(String path, int status, String cos, String contentType) {
        server.createContext(path, exchange -> {
            exchange.getResponseHeaders().add("Content-Type", contentType);
            byte[] bytes = cos.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
            exchange.close();
        });
    }

    // ------------------------- constructors -------------------------

    @Test
    void constructorSenseParametresLlancaRuntimeException() {
        assertThatThrownBy(ArxiuPluginCaib::new).isInstanceOf(RuntimeException.class);
    }

    @Test
    void constructorAmbNomesPropertyKeyBaseLlancaRuntimeException() {
        assertThatThrownBy(() -> new ArxiuPluginCaib("base")).isInstanceOf(RuntimeException.class);
    }

    // ------------------------- propietats pluginsib.arxiu.caib (llibreria 3.x) -------------------------

    @Test
    void constructorCopiaLesPropietatsAntiguesAlPrefixDeLaLlibreria3() {
        Properties properties = new Properties();
        properties.setProperty("es.caib.pinbal.plugin.arxiu.caib.base.url", "https://arxiu.test/");
        properties.setProperty("es.caib.pinbal.plugin.arxiu.caib.usuari", "usuari");

        ArxiuPluginCaib plugin = new ArxiuPluginCaib("es.caib.pinbal.", properties);

        assertThat(plugin.getProperty("pluginsib.arxiu.caib.base.url")).isEqualTo("https://arxiu.test/");
        assertThat(plugin.getProperty("pluginsib.arxiu.caib.usuari")).isEqualTo("usuari");
        assertThat(plugin.getProperty("plugin.arxiu.caib.base.url")).isEqualTo("https://arxiu.test/");
    }

    @Test
    void ambPropietatsPluginsibRespectaLesPropietatsNovesJaDefinides() {
        Properties properties = new Properties();
        properties.setProperty("es.caib.pinbal.plugin.arxiu.caib.base.url", "https://antiga/");
        properties.setProperty("es.caib.pinbal.pluginsib.arxiu.caib.base.url", "https://nova/");
        properties.setProperty("es.caib.pinbal.altra.propietat", "valor");

        Properties resultat = ArxiuPluginCaib.ambPropietatsPluginsib("es.caib.pinbal.", properties);

        assertThat(resultat.getProperty("es.caib.pinbal.pluginsib.arxiu.caib.base.url")).isEqualTo("https://nova/");
        assertThat(resultat.getProperty("es.caib.pinbal.altra.propietat")).isEqualTo("valor");
        assertThat(properties.size()).isEqualTo(3);
    }

    @Test
    void ambPropietatsPluginsibAmbPropietatsNullRetornaBuides() {
        assertThat(ArxiuPluginCaib.ambPropietatsPluginsib("es.caib.pinbal.", null)).isEmpty();
    }

    // ------------------------- toDocumentMetadades (privat estàtic, per reflexió) -------------------------

    @Test
    void toDocumentMetadadesMapejaCampsConeguts() throws Throwable {
        Map<String, Object> metadatas = new HashMap<>();
        metadatas.put("eni:id", "ID123");
        metadatas.put("eni:v_nti", "10.0");
        metadatas.put("eni:origen", "ADMINISTRACIO");
        metadatas.put("eni:fecha_inicio", "2024-01-15T10:30:00+01:00");
        metadatas.put("eni:estado_elaboracion", "ORIGINAL");
        metadatas.put("eni:tipo_doc_ENI", "RESOLUCIO");
        metadatas.put("eni:organo", List.of("ORGAN1", "ORGAN2"));
        metadatas.put("eni:nombre_formato", "PDF");
        metadatas.put("eni:extension_formato", "PDF");
        metadatas.put("metadada.desconeguda", "valor-desconegut");
        metadatas.put("metadada.nul.la", null);

        DocumentMetadades result = invocarToDocumentMetadades(metadatas);

        assertThat(result.getIdentificador()).isEqualTo("ID123");
        assertThat(result.getVersioNti()).isEqualTo("10.0");
        assertThat(result.getDataCaptura()).isNotNull();
        assertThat(result.getOrgans()).containsExactly("ORGAN1", "ORGAN2");
        assertThat(result.getMetadadesAddicionals()).containsEntry("metadada.desconeguda", "valor-desconegut");
        assertThat(result.getMetadadesAddicionals()).doesNotContainKey("metadada.nul.la");
    }

    @Test
    void toDocumentMetadadesAmbOrganUnicNoLlistaElConverteixEnLlista() throws Throwable {
        Map<String, Object> metadatas = new HashMap<>();
        metadatas.put("eni:organo", "ORGAN_UNIC");

        DocumentMetadades result = invocarToDocumentMetadades(metadatas);

        assertThat(result.getOrgans()).containsExactly("ORGAN_UNIC");
    }

    @Test
    void toDocumentMetadadesAmbDataInvalidaLlancaArxiuException() {
        Map<String, Object> metadatas = new HashMap<>();
        metadatas.put("eni:fecha_inicio", "no-es-una-data-valida");

        assertThatThrownBy(() -> invocarToDocumentMetadades(metadatas))
                .isInstanceOf(ArxiuException.class);
    }

    @SuppressWarnings("unchecked")
    private DocumentMetadades invocarToDocumentMetadades(Map<String, Object> metadatas) throws Throwable {
        Method method = ArxiuPluginCaib.class.getDeclaredMethod("toDocumentMetadades", Map.class);
        method.setAccessible(true);
        try {
            return (DocumentMetadades) method.invoke(null, metadatas);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    // ------------------------- parseDateIso8601 (privat estàtic, per reflexió) -------------------------

    @Test
    void parseDateIso8601AmbNullRetornaNull() throws Throwable {
        assertThat(invocarParseDateIso8601(null)).isNull();
    }

    @Test
    void parseDateIso8601AmbDataValidaRetornaData() throws Throwable {
        assertThat(invocarParseDateIso8601("2024-01-15T10:30:00+01:00")).isInstanceOf(Date.class);
    }

    @Test
    void parseDateIso8601AmbDataInvalidaLlancaArxiuException() {
        assertThatThrownBy(() -> invocarParseDateIso8601("no-es-una-data"))
                .isInstanceOf(ArxiuException.class);
    }

    private Date invocarParseDateIso8601(String date) throws Throwable {
        Method method = ArxiuPluginCaib.class.getDeclaredMethod("parseDateIso8601", String.class);
        method.setAccessible(true);
        try {
            return (Date) method.invoke(null, date);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    // ------------------------- getMimeType / getMimeExtension (privats, per reflexió) -------------------------

    @Test
    void getMimeTypeDetectaPdf() throws Throwable {
        ArxiuPluginCaib plugin = crearPlugin();
        byte[] pdfBytes = "%PDF-1.4\n%contingut de prova".getBytes(StandardCharsets.UTF_8);

        String mime = invocarGetMimeType(plugin, pdfBytes);

        assertThat(mime).isEqualTo("application/pdf");
    }

    @Test
    void getMimeExtensionPerPdfRetornaExtensioPdf() throws Throwable {
        ArxiuPluginCaib plugin = crearPlugin();

        String extensio = invocarGetMimeExtension(plugin, "application/pdf");

        assertThat(extensio).isEqualTo(".pdf");
    }

    private String invocarGetMimeType(ArxiuPluginCaib plugin, byte[] contingut) throws Throwable {
        Method method = ArxiuPluginCaib.class.getDeclaredMethod("getMimeType", byte[].class);
        method.setAccessible(true);
        try {
            return (String) method.invoke(plugin, (Object) contingut);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private String invocarGetMimeExtension(ArxiuPluginCaib plugin, String mime) throws Throwable {
        Method method = ArxiuPluginCaib.class.getDeclaredMethod("getMimeExtension", String.class);
        method.setAccessible(true);
        try {
            return (String) method.invoke(plugin, mime);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    // ------------------------- documentOriginal (privat, per reflexió + servidor local) -------------------------

    @Test
    void documentOriginalRecuperaContingutIDetectaMime() throws Throwable {
        ArxiuPluginCaib plugin = crearPlugin();
        respon("/original/uuid/abc123", 200, "%PDF-1.4\ncontingut", "application/pdf");
        server.start();

        DocumentContingut contingut = invocarDocumentOriginal(plugin, "ARX:abc123");

        assertThat(contingut.getTipusMime()).isEqualTo("application/pdf");
        assertThat(contingut.getArxiuNom()).isEqualTo("versio_original.pdf");
        assertThat(contingut.getTamany()).isGreaterThan(0);

        // Una segona crida ha de reutilitzar el client Jersey ja creat (branca de cache).
        DocumentContingut segonaCrida = invocarDocumentOriginal(plugin, "ARX:abc123");
        assertThat(segonaCrida.getTipusMime()).isEqualTo("application/pdf");
    }

    @Test
    void documentOriginalAmbPropietatsAmbPrefixPluginsibRecuperaContingut() throws Throwable {
        ArxiuPluginCaib plugin = crearPlugin("pluginsib.arxiu.caib.");
        respon("/original/uuid/abc123", 200, "%PDF-1.4\ncontingut", "application/pdf");
        server.start();

        DocumentContingut contingut = invocarDocumentOriginal(plugin, "ARX:abc123");

        assertThat(contingut.getTipusMime()).isEqualTo("application/pdf");
    }

    @Test
    void documentOriginalSenseDospuntsEnIdentificadorUsaLIdentificadorSencer() throws Throwable {
        ArxiuPluginCaib plugin = crearPlugin();
        respon("/original/uuid/abc123", 200, "%PDF-1.4\ncontingut", "application/pdf");
        server.start();

        DocumentContingut contingut = invocarDocumentOriginal(plugin, "abc123");

        assertThat(contingut.getTipusMime()).isEqualTo("application/pdf");
    }

    @Test
    void documentOriginalAmbErrorDelServidorLlancaArxiuException() throws Throwable {
        ArxiuPluginCaib plugin = crearPlugin();
        respon("/original/uuid/abc123", 500, "error", "text/plain");
        server.start();

        assertThatThrownBy(() -> invocarDocumentOriginal(plugin, "ARX:abc123"))
                .isInstanceOf(ArxiuException.class);
    }

    private DocumentContingut invocarDocumentOriginal(ArxiuPluginCaib plugin, String identificador) throws Throwable {
        Method method = ArxiuPluginCaib.class.getDeclaredMethod("documentOriginal", String.class);
        method.setAccessible(true);
        try {
            return (DocumentContingut) method.invoke(plugin, identificador);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    // ------------------------- documentMetadades (privat, per reflexió + servidor local) -------------------------

    @Test
    void documentMetadadesRecuperaIParsejaJson() throws Throwable {
        ArxiuPluginCaib plugin = crearPlugin();
        respon("/metadata/uuid/abc123", 200, "{\"eni:id\":\"ID1\",\"eni:tipoFirma\":\"CADES\"}", "application/json");
        server.start();

        Map<String, Object> resultat = invocarDocumentMetadades(plugin, "abc123");

        assertThat(resultat).containsEntry("eni:id", "ID1").containsKey("eni:tipoFirma");
    }

    @Test
    void documentMetadadesAmbErrorDelServidorLlancaArxiuException() throws Throwable {
        ArxiuPluginCaib plugin = crearPlugin();
        respon("/metadata/uuid/abc123", 500, "error", "text/plain");
        server.start();

        assertThatThrownBy(() -> invocarDocumentMetadades(plugin, "abc123"))
                .isInstanceOf(ArxiuException.class);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> invocarDocumentMetadades(ArxiuPluginCaib plugin, String identificador) throws Throwable {
        Method method = ArxiuPluginCaib.class.getDeclaredMethod("documentMetadades", String.class);
        method.setAccessible(true);
        try {
            return (Map<String, Object>) method.invoke(plugin, identificador);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    // ------------------------- documentDetalls (públic, amb ambContingut=false) -------------------------

    @Test
    void documentDetallsSenseContingutAmbMetadadesTrobades() throws Exception {
        ArxiuPluginCaib plugin = crearPlugin();
        respon("/metadata/uuid/abc123", 200, "{\"eni:id\":\"ID1\",\"eni:tipoFirma\":\"CADES\"}", "application/json");
        server.start();

        Document document = plugin.documentDetalls("abc123", null, false);

        assertThat(document.getMetadades()).isNotNull();
        assertThat(document.getMetadades().getIdentificador()).isEqualTo("ID1");
        assertThat(document.getFirmes()).hasSize(1);
        assertThat(document.getContingut()).isNull();
    }

    @Test
    void documentDetallsSenseContingutAmbErrorDeMetadadesNoLlancaExcepcio() throws Exception {
        ArxiuPluginCaib plugin = crearPlugin();
        respon("/metadata/uuid/abc123", 500, "error", "text/plain");
        server.start();

        Document document = plugin.documentDetalls("abc123", null, false);

        assertThat(document.getMetadades()).isNull();
    }

    // ------------------------- documentDetalls (públic, amb ambContingut=true) -------------------------

    @Test
    void documentDetallsAmbContingutRetornaLaVersioOriginalINoLaImprimible() throws Exception {
        ArxiuPluginCaib plugin = crearPlugin();
        List<String> peticionsRebudes = new ArrayList<>();
        server.createContext("/", exchange -> {
            peticionsRebudes.add(exchange.getRequestURI().getPath());
            exchange.sendResponseHeaders(500, -1);
            exchange.close();
        });
        respon("/original/uuid/abc123", 200, "%PDF-1.4\noriginal", "application/pdf");
        respon("/metadata/uuid/abc123", 200, "{\"eni:id\":\"ID1\",\"eni:tipoFirma\":\"TF06\"}", "application/json");
        server.start();

        Document document = plugin.documentDetalls("abc123", null, true);

        assertThat(new String(document.getContingut().getContingut(), StandardCharsets.UTF_8)).isEqualTo("%PDF-1.4\noriginal");
        assertThat(document.getContingut().getArxiuNom()).isEqualTo("versio_original.pdf");
        assertThat(document.getIdentificador()).isEqualTo("abc123");
        assertThat(document.getFirmes()).hasSize(1);
        assertThat(document.getFirmes().get(0).getTipus()).isEqualTo(FirmaTipus.PADES);
        // No s'ha demanat cap versió imprimible
        assertThat(peticionsRebudes).isEmpty();
    }

    @Test
    void documentDetallsAmbContingutIErrorDelOriginalLlancaArxiuException() throws Exception {
        ArxiuPluginCaib plugin = crearPlugin();
        respon("/original/uuid/abc123", 500, "error", "text/plain");
        server.start();

        assertThatThrownBy(() -> plugin.documentDetalls("abc123", null, true))
                .isInstanceOf(ArxiuException.class);
    }

    @Test
    void toFirmaAmbTipusNoReconegutRetornaFirmaSenseTipus() {
        assertThat(ArxiuPluginCaib.toFirma("CADES").getTipus()).isNull();
        assertThat(ArxiuPluginCaib.toFirma(null).getTipus()).isNull();
        assertThat(ArxiuPluginCaib.toFirma("TF04").getTipus()).isEqualTo(FirmaTipus.CADES_DET);
    }

    // ------------------------- sense ConCSV configurat -------------------------

    private ArxiuPluginCaib crearPluginSenseConcsv(List<String> peticionsRebudes) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            peticionsRebudes.add(exchange.getRequestURI().getPath());
            byte[] bytes = "error".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
            exchange.close();
        });
        server.start();
        Properties properties = new Properties();
        properties.setProperty("es.caib.pinbal.plugin.arxiu.caib.base.url", "http://127.0.0.1:" + server.getAddress().getPort());
        properties.setProperty("es.caib.pinbal.plugin.arxiu.caib.aplicacio.codi", "PINBAL");
        properties.setProperty("es.caib.pinbal.plugin.arxiu.caib.usuari", "usuari");
        properties.setProperty("es.caib.pinbal.plugin.arxiu.caib.contrasenya", "contrasenya");
        properties.setProperty("es.caib.pinbal.plugin.arxiu.caib.timeout.connect", "1000");
        properties.setProperty("es.caib.pinbal.plugin.arxiu.caib.timeout.read", "1000");
        return new ArxiuPluginCaib("es.caib.pinbal.", properties);
    }

    @Test
    void documentDetallsSenseConcsvConsultaDirectamentLArxiu() throws Exception {
        List<String> peticionsRebudes = new ArrayList<>();
        ArxiuPluginCaib plugin = crearPluginSenseConcsv(peticionsRebudes);

        assertThatThrownBy(() -> plugin.documentDetalls("abc123", null, false))
                .isInstanceOf(ArxiuException.class);

        assertThat(peticionsRebudes).contains("/services/getDocument");
        assertThat(peticionsRebudes).noneMatch(path -> path.contains("/metadata/uuid/"));
    }

    @Test
    void documentMetadadesSenseConcsvLlancaErrorClar() throws Exception {
        ArxiuPluginCaib plugin = crearPluginSenseConcsv(new ArrayList<>());

        assertThatThrownBy(() -> invocarDocumentMetadades(plugin, "abc123"))
                .isInstanceOf(ArxiuException.class)
                .hasRootCauseMessage("No s'ha configurat la propietat pluginsib.arxiu.caib.concsv.base.url amb la URL base del servei ConCSV");
    }

    @Test
    void documentOriginalSenseConcsvLlancaErrorClar() throws Exception {
        ArxiuPluginCaib plugin = crearPluginSenseConcsv(new ArrayList<>());

        assertThatThrownBy(() -> invocarDocumentOriginal(plugin, "ARX:abc123"))
                .isInstanceOf(ArxiuException.class)
                .hasRootCauseMessage("No s'ha configurat la propietat pluginsib.arxiu.caib.concsv.base.url amb la URL base del servei ConCSV");
    }
}
