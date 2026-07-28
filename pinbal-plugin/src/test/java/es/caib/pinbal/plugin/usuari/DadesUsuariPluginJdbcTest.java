package es.caib.pinbal.plugin.usuari;

import es.caib.pinbal.plugin.SistemaExternException;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.Context;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DadesUsuariPluginJdbcTest {

    private static final String JNDI_NAME = "java:/comp/env/jdbc/PinbalTestDS";

    private JdbcDataSource dataSource;

    @BeforeEach
    void configurarJndi() throws Exception {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, FakeInitialContextFactory.class.getName());
        FakeInitialContextFactory.clear();

        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:pinbal_usr_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");

        try (Connection con = dataSource.getConnection(); Statement st = con.createStatement()) {
            st.execute("CREATE TABLE USUARIS (CODI VARCHAR(50), NOM VARCHAR(100), NIF VARCHAR(20), EMAIL VARCHAR(100))");
            st.execute("CREATE TABLE ROLS (USUARI_CODI VARCHAR(50), ROL VARCHAR(50))");
            st.execute("INSERT INTO USUARIS VALUES ('USR1', 'Joan March', '12345678A', 'joan@example.com')");
            st.execute("INSERT INTO USUARIS VALUES ('USR2', 'Maria Bonnin', '87654321B', 'maria@example.com')");
            st.execute("INSERT INTO ROLS VALUES ('USR1', 'ADMIN')");
            st.execute("INSERT INTO ROLS VALUES ('USR1', 'USER')");
        }

        FakeInitialContextFactory.bind(JNDI_NAME, dataSource);
    }

    @AfterEach
    void netejarJndi() {
        System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
        FakeInitialContextFactory.clear();
    }

    private DadesUsuariPluginJdbc crearPlugin(Properties extra) {
        Properties properties = new Properties();
        properties.setProperty("jdbc.datasource.jndi.name", JNDI_NAME);
        properties.putAll(extra);
        return new DadesUsuariPluginJdbc("", properties);
    }

    // ------------------------- consultarAmbUsuariCodi -------------------------

    @Test
    void consultarAmbUsuariCodiAmbInterrogantIAmbRolsConfigurats() throws Exception {
        Properties extra = new Properties();
        extra.setProperty("jdbc.query.codi", "SELECT CODI, NOM, NIF, EMAIL FROM USUARIS WHERE CODI = ?");
        extra.setProperty("jdbc.query.rols", "SELECT ROL FROM ROLS WHERE USUARI_CODI = ?");
        DadesUsuariPluginJdbc plugin = crearPlugin(extra);

        DadesUsuari usuari = plugin.consultarAmbUsuariCodi("USR1");

        assertThat(usuari).isNotNull();
        assertThat(usuari.getCodi()).isEqualTo("USR1");
        assertThat(usuari.getNom()).isEqualTo("Joan March");
        assertThat(usuari.getNif()).isEqualTo("12345678A");
        assertThat(usuari.getEmail()).isEqualTo("joan@example.com");
        assertThat(usuari.getRols()).containsExactlyInAnyOrder("ADMIN", "USER");
    }

    @Test
    void consultarAmbUsuariCodiAmbQueryGenericaSenseParametreExplicit() throws Exception {
        Properties extra = new Properties();
        // Consulta que no conté ni "?" ni ":codi": s'executa tal qual, ignorant el paràmetre.
        extra.setProperty("jdbc.query.codi", "SELECT CODI, NOM, NIF, EMAIL FROM USUARIS WHERE CODI = 'USR1'");
        DadesUsuariPluginJdbc plugin = crearPlugin(extra);

        DadesUsuari usuari = plugin.consultarAmbUsuariCodi("qualsevol-valor-ignorat");

        assertThat(usuari).isNotNull();
        assertThat(usuari.getCodi()).isEqualTo("USR1");
        // jdbc.query.rols no configurat: consultaRolsUsuari retorna null immediatament.
        assertThat(usuari.getRols()).isNull();
    }

    @Test
    void consultarAmbUsuariCodiFaServirQueryPerDefecteSiNoHiHaQueryCodiEspecific() throws Exception {
        Properties extra = new Properties();
        extra.setProperty("jdbc.query", "SELECT CODI, NOM, NIF, EMAIL FROM USUARIS WHERE CODI = ?");
        DadesUsuariPluginJdbc plugin = crearPlugin(extra);

        DadesUsuari usuari = plugin.consultarAmbUsuariCodi("USR1");

        assertThat(usuari.getCodi()).isEqualTo("USR1");
    }

    @Test
    void consultarAmbUsuariCodiNoTrobatRetornaNull() throws Exception {
        Properties extra = new Properties();
        extra.setProperty("jdbc.query.codi", "SELECT CODI, NOM, NIF, EMAIL FROM USUARIS WHERE CODI = ?");
        DadesUsuariPluginJdbc plugin = crearPlugin(extra);

        assertThat(plugin.consultarAmbUsuariCodi("NO-EXISTEIX")).isNull();
    }

    // ------------------------- consultarAmbUsuariNif (estil :param) -------------------------

    @Test
    void consultarAmbUsuariNifAmbEstilParametreAmbDosPunts() throws Exception {
        Properties extra = new Properties();
        extra.setProperty("jdbc.query.nif", "SELECT CODI, NOM, NIF, EMAIL FROM USUARIS WHERE NIF = :nif");
        DadesUsuariPluginJdbc plugin = crearPlugin(extra);

        DadesUsuari usuari = plugin.consultarAmbUsuariNif("87654321B");

        assertThat(usuari.getCodi()).isEqualTo("USR2");
        assertThat(usuari.getNom()).isEqualTo("Maria Bonnin");
    }

    @Test
    void consultarAmbUsuariCodiAmbRolsEnEstilParametreAmbDosPunts() throws Exception {
        Properties extra = new Properties();
        extra.setProperty("jdbc.query.codi", "SELECT CODI, NOM, NIF, EMAIL FROM USUARIS WHERE CODI = ?");
        extra.setProperty("jdbc.query.rols", "SELECT ROL FROM ROLS WHERE USUARI_CODI = :codi");
        DadesUsuariPluginJdbc plugin = crearPlugin(extra);

        DadesUsuari usuari = plugin.consultarAmbUsuariCodi("USR1");

        assertThat(usuari.getRols()).containsExactlyInAnyOrder("ADMIN", "USER");
    }

    @Test
    void consultarAmbUsuariCodiAmbRolsAmbQueryGenerica() throws Exception {
        Properties extra = new Properties();
        extra.setProperty("jdbc.query.codi", "SELECT CODI, NOM, NIF, EMAIL FROM USUARIS WHERE CODI = ?");
        extra.setProperty("jdbc.query.rols", "SELECT ROL FROM ROLS WHERE USUARI_CODI = 'USR1'");
        DadesUsuariPluginJdbc plugin = crearPlugin(extra);

        DadesUsuari usuari = plugin.consultarAmbUsuariCodi("USR1");

        assertThat(usuari.getRols()).containsExactlyInAnyOrder("ADMIN", "USER");
    }

    // ------------------------- consultarAmbUsuariNom -------------------------

    @Test
    void consultarAmbUsuariNom() throws Exception {
        Properties extra = new Properties();
        extra.setProperty("jdbc.query.nom", "SELECT CODI, NOM, NIF, EMAIL FROM USUARIS WHERE NOM = ?");
        DadesUsuariPluginJdbc plugin = crearPlugin(extra);

        DadesUsuari usuari = plugin.consultarAmbUsuariNom("Joan March");

        assertThat(usuari.getCodi()).isEqualTo("USR1");
    }

    // ------------------------- consultarAmbUsuariAny (llista) -------------------------

    @Test
    void consultarAmbUsuariAnyRetornaTotsElsQueCoincideixen() throws Exception {
        Properties extra = new Properties();
        extra.setProperty("jdbc.query.any", "SELECT CODI, NOM, NIF, EMAIL FROM USUARIS WHERE NOM LIKE '%' || ? || '%'");
        DadesUsuariPluginJdbc plugin = crearPlugin(extra);

        List<DadesUsuari> usuaris = plugin.consultarAmbUsuariAny("a");

        assertThat(usuaris).extracting(DadesUsuari::getCodi).containsExactlyInAnyOrder("USR1", "USR2");
    }

    @Test
    void consultarAmbUsuariAnySenseCoincidenciesRetornaListaBuida() throws Exception {
        Properties extra = new Properties();
        extra.setProperty("jdbc.query.any", "SELECT CODI, NOM, NIF, EMAIL FROM USUARIS WHERE NOM = ?");
        DadesUsuariPluginJdbc plugin = crearPlugin(extra);

        assertThat(plugin.consultarAmbUsuariAny("ningu")).isEmpty();
    }

    @Test
    void consultarAmbUsuariAnyAmbEstilParametreAmbDosPunts() throws Exception {
        Properties extra = new Properties();
        extra.setProperty("jdbc.query.any", "SELECT CODI, NOM, NIF, EMAIL FROM USUARIS WHERE NOM = :text");
        DadesUsuariPluginJdbc plugin = crearPlugin(extra);

        List<DadesUsuari> usuaris = plugin.consultarAmbUsuariAny("Joan March");

        assertThat(usuaris).extracting(DadesUsuari::getCodi).containsExactly("USR1");
    }

    @Test
    void consultarAmbUsuariAnyAmbSqlInvalidLlancaSistemaExternException() {
        Properties extra = new Properties();
        extra.setProperty("jdbc.query.any", "SELECT * FROM TAULA_QUE_NO_EXISTEIX WHERE NOM = ?");
        DadesUsuariPluginJdbc plugin = crearPlugin(extra);

        assertThatThrownBy(() -> plugin.consultarAmbUsuariAny("a"))
                .isInstanceOf(SistemaExternException.class);
    }

    // ------------------------- findAmbGrup (llista) -------------------------

    @Test
    void findAmbGrupAmbQueryGenerica() throws Exception {
        Properties extra = new Properties();
        extra.setProperty("jdbc.query.grup", "SELECT CODI, NOM, NIF, EMAIL FROM USUARIS");
        DadesUsuariPluginJdbc plugin = crearPlugin(extra);

        List<DadesUsuari> usuaris = plugin.findAmbGrup("qualsevol-grup");

        assertThat(usuaris).hasSize(2);
    }

    // ------------------------- errors -------------------------

    @Test
    void consultaAmbSqlInvalidLlancaSistemaExternException() {
        Properties extra = new Properties();
        extra.setProperty("jdbc.query.codi", "SELECT * FROM TAULA_QUE_NO_EXISTEIX WHERE CODI = ?");
        DadesUsuariPluginJdbc plugin = crearPlugin(extra);

        assertThatThrownBy(() -> plugin.consultarAmbUsuariCodi("USR1"))
                .isInstanceOf(SistemaExternException.class);
    }

    @Test
    void consultaAmbJndiNoRegistratLlancaSistemaExternException() {
        Properties properties = new Properties();
        properties.setProperty("jdbc.datasource.jndi.name", "java:/comp/env/jdbc/NoExisteix");
        properties.setProperty("jdbc.query.codi", "SELECT CODI, NOM, NIF, EMAIL FROM USUARIS WHERE CODI = ?");
        DadesUsuariPluginJdbc plugin = new DadesUsuariPluginJdbc("", properties);

        assertThatThrownBy(() -> plugin.consultarAmbUsuariCodi("USR1"))
                .isInstanceOf(SistemaExternException.class);
    }
}
