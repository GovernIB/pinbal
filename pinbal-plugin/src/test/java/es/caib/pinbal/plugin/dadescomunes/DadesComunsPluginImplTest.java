package es.caib.pinbal.plugin.dadescomunes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import es.caib.pinbal.plugin.SistemaExternException;
import es.caib.pinbal.plugin.dadescomuns.Municipi;
import es.caib.pinbal.plugin.dadescomuns.Pais;
import es.caib.pinbal.plugin.dadescomuns.Provincia;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DadesComunsPluginImplTest {

    private HttpServer server;

    @AfterEach
    void aturarServidor() {
        if (server != null) {
            server.stop(0);
        }
    }

    private DadesComunsPluginImpl crearPlugin() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        return crearPlugin(server.getAddress().getPort());
    }

    private DadesComunsPluginImpl crearPlugin(int port) {
        Properties properties = new Properties();
        properties.setProperty("es.caib.pinbal.dadescomunes.base.url", "http://127.0.0.1:" + port);
        return new DadesComunsPluginImpl(properties);
    }

    private void respon(String path, int status, String cos) {
        server.createContext(path, exchange -> escriuResposta(exchange, status, cos));
    }

    private void escriuResposta(HttpExchange exchange, int status, String cos) throws IOException {
        byte[] bytes = cos.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
        exchange.close();
    }

    // ------------------------- findPaisos -------------------------

    @Test
    void findPaisosAmbIdiomaCatalaOrdenatsPelNom() throws Exception {
        DadesComunsPluginImpl plugin = crearPlugin();
        respon("/services/paisos/format/JSON/idioma/ca", 200,
                "[{\"codi_numeric\":\"724\",\"alpha2\":\"ES\",\"alpha3\":\"ESP\",\"nom_ca\":\"Espanya\",\"nom_es\":\"España\",\"nom\":\"Espanya\"},"
                        + "{\"codi_numeric\":\"250\",\"alpha2\":\"FR\",\"alpha3\":\"FRA\",\"nom_ca\":\"França\",\"nom_es\":\"Francia\",\"nom\":\"França\"}]");
        server.start();

        List<Pais> paisos = plugin.findPaisos("ca");

        assertThat(paisos).hasSize(2);
        // Ordenats alfabèticament: Espanya abans que França
        assertThat(paisos.get(0).getNom()).isEqualTo("Espanya");
        assertThat(paisos.get(1).getNom()).isEqualTo("França");
        assertThat(paisos.get(0).getAlpha2()).isEqualTo("ES");
    }

    @Test
    void findPaisosAmbIdiomaEspanyolFaServirNomEs() throws Exception {
        DadesComunsPluginImpl plugin = crearPlugin();
        respon("/services/paisos/format/JSON/idioma/es", 200,
                "[{\"codi_numeric\":\"724\",\"alpha2\":\"ES\",\"alpha3\":\"ESP\",\"nom_ca\":\"Espanya\",\"nom_es\":\"España\",\"nom\":\"Espanya\"}]");
        server.start();

        List<Pais> paisos = plugin.findPaisos("es");

        assertThat(paisos).hasSize(1);
        assertThat(paisos.get(0).getNom()).isEqualTo("España");
    }

    @Test
    void findPaisosAmbIdiomaEsPeroSenseNomEsFaServirNomPerDefecte() throws Exception {
        DadesComunsPluginImpl plugin = crearPlugin();
        respon("/services/paisos/format/JSON/idioma/es", 200,
                "[{\"codi_numeric\":\"724\",\"alpha2\":\"ES\",\"alpha3\":\"ESP\",\"nom\":\"Espanya\"}]");
        server.start();

        List<Pais> paisos = plugin.findPaisos("ES");

        assertThat(paisos.get(0).getNom()).isEqualTo("Espanya");
    }

    @Test
    void findPaisosAmbIdiomaNulODesconegutFaServirCatala() throws Exception {
        DadesComunsPluginImpl plugin = crearPlugin();
        respon("/services/paisos/format/JSON/idioma/ca", 200,
                "[{\"codi_numeric\":\"724\",\"nom\":\"Espanya\"}]");
        server.start();

        assertThat(plugin.findPaisos(null)).hasSize(1);
        assertThat(plugin.findPaisos("")).hasSize(1);
        assertThat(plugin.findPaisos("fr")).hasSize(1);
    }

    @Test
    void findPaisosAmbRespostaBuidaRetornaListaBuida() throws Exception {
        DadesComunsPluginImpl plugin = crearPlugin();
        respon("/services/paisos/format/JSON/idioma/ca", 200, "");
        server.start();

        assertThat(plugin.findPaisos("ca")).isEmpty();
    }

    @Test
    void findPaisosAmbErrorDelServidorLlancaSistemaExternException() throws Exception {
        DadesComunsPluginImpl plugin = crearPlugin();
        respon("/services/paisos/format/JSON/idioma/ca", 500, "error intern");
        server.start();

        assertThatThrownBy(() -> plugin.findPaisos("ca"))
                .isInstanceOf(SistemaExternException.class);
    }

    @Test
    void findPaisosAmbHostInabastableLlancaSistemaExternException() {
        // Port 1 en loopback: sense servei escoltant, falla de seguida amb "connection refused".
        DadesComunsPluginImpl plugin = crearPlugin(1);
        assertThatThrownBy(() -> plugin.findPaisos("ca"))
                .isInstanceOf(SistemaExternException.class);
    }

    // ------------------------- findProvincies -------------------------

    @Test
    void findProvinciesAmbIdiomaCatalaOrdenadesPelNom() throws Exception {
        DadesComunsPluginImpl plugin = crearPlugin();
        respon("/services/provincies/format/JSON/idioma/ca", 200,
                "[{\"codi\":\"07\",\"nom_ca\":\"Illes Balears\",\"nom_es\":\"Islas Baleares\",\"nom\":\"Illes Balears\"},"
                        + "{\"codi\":\"08\",\"nom_ca\":\"Barcelona\",\"nom_es\":\"Barcelona\",\"nom\":\"Barcelona\"}]");
        server.start();

        List<Provincia> provincies = plugin.findProvincies("ca");

        assertThat(provincies).hasSize(2);
        assertThat(provincies.get(0).getNom()).isEqualTo("Barcelona");
        assertThat(provincies.get(1).getNom()).isEqualTo("Illes Balears");
    }

    @Test
    void findProvinciesAmbIdiomaEspanyolFaServirNomEs() throws Exception {
        DadesComunsPluginImpl plugin = crearPlugin();
        respon("/services/provincies/format/JSON/idioma/es", 200,
                "[{\"codi\":\"07\",\"nom_ca\":\"Illes Balears\",\"nom_es\":\"Islas Baleares\",\"nom\":\"Illes Balears\"}]");
        server.start();

        List<Provincia> provincies = plugin.findProvincies("es");

        assertThat(provincies.get(0).getNom()).isEqualTo("Islas Baleares");
    }

    @Test
    void findProvinciesAmbRespostaBuidaRetornaListaBuida() throws Exception {
        DadesComunsPluginImpl plugin = crearPlugin();
        respon("/services/provincies/format/JSON/idioma/ca", 200, "");
        server.start();

        assertThat(plugin.findProvincies("ca")).isEmpty();
    }

    @Test
    void findProvinciesAmbErrorDelServidorLlancaSistemaExternException() throws Exception {
        DadesComunsPluginImpl plugin = crearPlugin();
        respon("/services/provincies/format/JSON/idioma/ca", 500, "error intern");
        server.start();

        assertThatThrownBy(() -> plugin.findProvincies("ca"))
                .isInstanceOf(SistemaExternException.class);
    }

    // ------------------------- findMunicipisPerProvincia -------------------------

    @Test
    void findMunicipisAmbCodiCurtAfegeixCodiProvincia() throws Exception {
        DadesComunsPluginImpl plugin = crearPlugin();
        respon("/services/municipis/07/format/JSON", 200,
                "[{\"codi\":\"1\",\"nom\":\"Palma\"},{\"codi\":\"2\",\"nom\":\"Calvia\"}]");
        server.start();

        List<Municipi> municipis = plugin.findMunicipisPerProvincia("07");

        assertThat(municipis).extracting(Municipi::getCodi).containsExactlyInAnyOrder("071", "072");
    }

    @Test
    void findMunicipisAmbCodiJaComplertNoModificaCodi() throws Exception {
        DadesComunsPluginImpl plugin = crearPlugin();
        respon("/services/municipis/07/format/JSON", 200,
                "[{\"codi\":\"07001\",\"nom\":\"Palma\"},{\"codi\":\"07002\",\"nom\":\"Calvia\"}]");
        server.start();

        List<Municipi> municipis = plugin.findMunicipisPerProvincia("07");

        assertThat(municipis).extracting(Municipi::getCodi).containsExactlyInAnyOrder("07001", "07002");
    }

    @Test
    void findMunicipisAmbRespostaBuidaRetornaListaBuida() throws Exception {
        DadesComunsPluginImpl plugin = crearPlugin();
        respon("/services/municipis/07/format/JSON", 200, "");
        server.start();

        assertThat(plugin.findMunicipisPerProvincia("07")).isEmpty();
    }

    @Test
    void findMunicipisAmbErrorDelServidorLlancaSistemaExternException() throws Exception {
        DadesComunsPluginImpl plugin = crearPlugin();
        respon("/services/municipis/07/format/JSON", 500, "error intern");
        server.start();

        assertThatThrownBy(() -> plugin.findMunicipisPerProvincia("07"))
                .isInstanceOf(SistemaExternException.class);
    }

    @Test
    void constructorAmbPropertyKeyBaseCustomAfegeixEndpoint() {
        Properties properties = new Properties();
        properties.setProperty("custom.dadescomunes.base.url", "http://127.0.0.1:1");
        DadesComunsPluginImpl plugin = new DadesComunsPluginImpl("custom.", properties);
        assertThat(plugin).isNotNull();
    }
}
