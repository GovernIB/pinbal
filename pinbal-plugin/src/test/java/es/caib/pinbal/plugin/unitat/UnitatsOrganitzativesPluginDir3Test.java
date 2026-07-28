package es.caib.pinbal.plugin.unitat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import es.caib.pinbal.plugin.SistemaExternException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UnitatsOrganitzativesPluginDir3Test {

    private HttpServer server;

    @AfterEach
    void aturarServidor() {
        if (server != null) {
            server.stop(0);
        }
    }

    private UnitatsOrganitzativesPluginDir3 crearPlugin() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        Properties properties = new Properties();
        properties.setProperty("plugin.unitats.organitzatives.dir3.service.url", "http://127.0.0.1:" + server.getAddress().getPort());
        properties.setProperty("plugin.unitats.organitzatives.dir3.service.username", "usuari");
        properties.setProperty("plugin.unitats.organitzatives.dir3.service.password", "contrasenya");
        return new UnitatsOrganitzativesPluginDir3("", properties);
    }

    private void respon(String path, int status, String cos) {
        server.createContext(path, exchange -> escriuResposta(exchange, status, cos));
    }

    private void respon(String path, int status, String cos, AtomicReference<String> authCapturat) {
        server.createContext(path, exchange -> {
            authCapturat.set(exchange.getRequestHeaders().getFirst("Authorization"));
            escriuResposta(exchange, status, cos);
        });
    }

    private void escriuResposta(HttpExchange exchange, int status, String cos) throws IOException {
        byte[] bytes = cos.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
        exchange.close();
    }

    // ------------------------- organigrama -------------------------

    @Test
    void organigramaAmbNodesVigentsITransitorisRecorreTotElArbre() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        respon("/rest/organigrama/", 200,
                "{\"codigo\":\"A1\",\"denominacion\":\"Arrel\",\"descripcionEstado\":\"Vigente\",\"hijos\":["
                        + "{\"codigo\":\"A1.1\",\"denominacion\":\"Fill vigent\",\"descripcionEstado\":\"Vigente\"},"
                        + "{\"codigo\":\"A1.2\",\"denominacion\":\"Fill transitori\",\"descripcionEstado\":\"Transitorio\"}"
                        + "]}");
        server.start();

        Map<String, NodeDir3> organigrama = plugin.organigrama("A1");

        assertThat(organigrama).containsOnlyKeys("A1", "A1.1", "A1.2");
    }

    @Test
    void organigramaAmbArrelNoVigentNoRecorreFills() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        respon("/rest/organigrama/", 200,
                "{\"codigo\":\"A1\",\"denominacion\":\"Arrel\",\"descripcionEstado\":\"Extinguida\",\"hijos\":["
                        + "{\"codigo\":\"A1.1\",\"denominacion\":\"Fill vigent\",\"descripcionEstado\":\"Vigente\"}"
                        + "]}");
        server.start();

        Map<String, NodeDir3> organigrama = plugin.organigrama("A1");

        assertThat(organigrama).isEmpty();
    }

    @Test
    void organigramaAmbErrorDelServidorLlancaSistemaExternException() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        respon("/rest/organigrama/", 500, "error");
        server.start();

        assertThatThrownBy(() -> plugin.organigrama("A1"))
                .isInstanceOf(SistemaExternException.class);
    }

    // ------------------------- findAmbPare -------------------------

    @Test
    void findAmbPareFiltraNomesUnitatsVigents() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        AtomicReference<String> auth = new AtomicReference<>();
        respon("/rest/unidades/obtenerArbolUnidades", 200,
                "[{\"codigo\":\"U1\",\"denominacion\":\"Unitat 1\",\"codigoEstadoEntidad\":\"V\"},"
                        + "{\"codigo\":\"U2\",\"denominacion\":\"Unitat 2\",\"codigoEstadoEntidad\":\"E\"}]",
                auth);
        server.start();

        List<UnitatOrganitzativa> unitats = plugin.findAmbPare("PARE");

        assertThat(unitats).extracting(UnitatOrganitzativa::getCodi).containsExactly("U1");
        assertThat(auth.get()).isEqualTo("Basic " + Base64.getEncoder().encodeToString("usuari:contrasenya".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void findAmbPareAmbDatesAfegeixParametresALaUrl() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        respon("/rest/unidades/obtenerArbolUnidades", 200,
                "[{\"codigo\":\"U1\",\"denominacion\":\"Unitat 1\",\"codigoEstadoEntidad\":\"V\"}]");
        server.start();

        List<UnitatOrganitzativa> unitats = plugin.findAmbPare("PARE", new java.util.Date(), new java.util.Date());

        assertThat(unitats).hasSize(1);
    }

    @Test
    void findAmbPareAmbDenominacioCooficialFaServirAquestaDenominacio() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        respon("/rest/unidades/obtenerArbolUnidades", 200,
                "[{\"codigo\":\"U1\",\"denominacion\":\"Unitat 1\",\"denominacionCooficial\":\"Unitat U1 Cooficial\",\"codigoEstadoEntidad\":\"V\"}]");
        server.start();

        List<UnitatOrganitzativa> unitats = plugin.findAmbPare("PARE");

        assertThat(unitats.get(0).getDenominacio()).isEqualTo("Unitat U1 Cooficial");
    }

    @Test
    void findAmbPareAmbRespostaBuidaRetornaListaBuida() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        respon("/rest/unidades/obtenerArbolUnidades", 200, "");
        server.start();

        assertThat(plugin.findAmbPare("PARE")).isEmpty();
    }

    @Test
    void findAmbPareAmbErrorDelServidorLlancaSistemaExternException() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        respon("/rest/unidades/obtenerArbolUnidades", 500, "error");
        server.start();

        assertThatThrownBy(() -> plugin.findAmbPare("PARE"))
                .isInstanceOf(SistemaExternException.class);
    }

    // ------------------------- findAmbCodi -------------------------

    @Test
    void findAmbCodiRetornaUnitatTrobada() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        respon("/rest/unidades/obtenerUnidad", 200,
                "{\"codigo\":\"U1\",\"denominacion\":\"Unitat 1\",\"codigoEstadoEntidad\":\"V\"}");
        server.start();

        UnitatOrganitzativa unitat = plugin.findAmbCodi("U1");

        assertThat(unitat.getCodi()).isEqualTo("U1");
        assertThat(unitat.getDenominacio()).isEqualTo("Unitat 1");
    }

    @Test
    void findAmbCodiAmbDatesRetornaUnitatTrobada() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        respon("/rest/unidades/obtenerUnidad", 200,
                "{\"codigo\":\"U1\",\"denominacion\":\"Unitat 1\"}");
        server.start();

        UnitatOrganitzativa unitat = plugin.findAmbCodi("U1", new java.util.Date(), new java.util.Date());

        assertThat(unitat.getCodi()).isEqualTo("U1");
    }

    @Test
    void findAmbCodiAmbRespostaBuidaRetornaNull() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        respon("/rest/unidades/obtenerUnidad", 200, "");
        server.start();

        assertThat(plugin.findAmbCodi("U1")).isNull();
    }

    @Test
    void findAmbCodiAmbErrorDelServidorLlancaSistemaExternException() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        respon("/rest/unidades/obtenerUnidad", 500, "error");
        server.start();

        assertThatThrownBy(() -> plugin.findAmbCodi("U1"))
                .isInstanceOf(SistemaExternException.class);
    }

    // ------------------------- cercaUnitats -------------------------

    @Test
    void cercaUnitatsAmbTotsElsParametresInformats() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        respon("/rest/busqueda/organismos", 200,
                "[{\"codigo\":\"U2\",\"denominacion\":\"Bbb\",\"descripcionEstado\":\"Vigente\"},"
                        + "{\"codigo\":\"U1\",\"denominacion\":\"Aaa\",\"descripcionEstado\":\"Vigente\"}]");
        server.start();

        List<UnitatOrganitzativa> unitats = plugin.cercaUnitats("U", "denom", 1L, 2L, true, true, 3L, "07001");

        // Ordenat pel nom: Aaa abans que Bbb
        assertThat(unitats).extracting(UnitatOrganitzativa::getCodi).containsExactly("U1", "U2");
    }

    @Test
    void cercaUnitatsAmbParametresNullsUtilitzaValorsPerDefecte() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        respon("/rest/busqueda/organismos", 200,
                "[{\"codigo\":\"U1\",\"denominacion\":\"Unitat 1\",\"descripcionEstado\":\"Vigente\"}]");
        server.start();

        List<UnitatOrganitzativa> unitats = plugin.cercaUnitats(null, null, null, null, null, null, null, null);

        assertThat(unitats).hasSize(1);
    }

    @Test
    void cercaUnitatsAmbRespostaBuidaRetornaListaBuida() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        respon("/rest/busqueda/organismos", 200, "");
        server.start();

        assertThat(plugin.cercaUnitats(null, null, null, null, null, null, null, null)).isEmpty();
    }

    @Test
    void cercaUnitatsAmbErrorDelServidorLlancaSistemaExternException() throws Exception {
        UnitatsOrganitzativesPluginDir3 plugin = crearPlugin();
        respon("/rest/busqueda/organismos", 500, "error");
        server.start();

        assertThatThrownBy(() -> plugin.cercaUnitats("U", null, null, null, null, null, null, null))
                .isInstanceOf(SistemaExternException.class);
    }
}
