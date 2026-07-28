package es.caib.pinbal.plugin.usuari;

import es.caib.pinbal.plugin.SistemaExternException;
import org.fundaciobit.pluginsib.userinformation.SearchStatus;
import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Amb el host LDAP apuntant a un port tancat en loopback la connexió falla de seguida (connexió
 * rebutjada), sense necessitat d'un servidor LDAP real i sense risc de bloquejar el test.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
class DadesUsuariPluginLdapCaibTest {

    private DadesUsuariPluginLdapCaib crearPlugin() {
        Properties properties = new Properties();
        properties.setProperty("pluginsib.userinformation.ldap.host_url", "ldap://127.0.0.1:1");
        return new DadesUsuariPluginLdapCaib("", properties);
    }

    // ------------------------- mètodes públics (camí d'error, sense LDAP real) -------------------------

    @Test
    void consultarAmbUsuariCodiAmbLdapInabastableLlancaSistemaExternException() {
        assertThatThrownBy(() -> crearPlugin().consultarAmbUsuariCodi("usuari1"))
                .isInstanceOf(SistemaExternException.class);
    }

    @Test
    void consultarAmbUsuariNifAmbLdapInabastableLlancaSistemaExternException() {
        assertThatThrownBy(() -> crearPlugin().consultarAmbUsuariNif("12345678A"))
                .isInstanceOf(SistemaExternException.class);
    }

    @Test
    void consultarAmbUsuariNomAmbLdapInabastableRetornaNull() throws Exception {
        // La cerca subjacent (getUsersByPartialNameOrPartialSurnames) captura internament els
        // errors de connexió i retorna un resultat buit en lloc de llançar excepció.
        assertThat(crearPlugin().consultarAmbUsuariNom("Joan")).isNull();
    }

    @Test
    void consultarAmbUsuariAnyAmbLdapInabastableRetornaListaBuida() throws Exception {
        assertThat(crearPlugin().consultarAmbUsuariAny("text")).isEmpty();
    }

    @Test
    void findAmbGrupAmbLdapInabastableLlancaSistemaExternException() {
        assertThatThrownBy(() -> crearPlugin().findAmbGrup("grup1"))
                .isInstanceOf(SistemaExternException.class);
    }

    // ------------------------- toDadesUsuari (privat, per reflexió) -------------------------

    @Test
    void toDadesUsuariAmbUserInfoNulRetornaNull() throws Exception {
        assertThat(invocarToDadesUsuari(null, false)).isNull();
    }

    @Test
    void toDadesUsuariSenseRolsMapejaCampsBasics() throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("usuari1");
        userInfo.setName("Joan March");
        userInfo.setAdministrationID("12345678A");
        userInfo.setEmail("joan@example.com");

        DadesUsuari dadesUsuari = invocarToDadesUsuari(userInfo, false);

        assertThat(dadesUsuari.getCodi()).isEqualTo("usuari1");
        assertThat(dadesUsuari.getNom()).isEqualTo("Joan March");
        assertThat(dadesUsuari.getNif()).isEqualTo("12345678A");
        assertThat(dadesUsuari.getEmail()).isEqualTo("joan@example.com");
        assertThat(dadesUsuari.getRols()).isNull();
    }

    @Test
    void toDadesUsuariAmbRolsIAmbLdapInabastableLlancaExcepcio() throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("usuari1");

        Method method = DadesUsuariPluginLdapCaib.class.getDeclaredMethod("toDadesUsuari", UserInfo.class, boolean.class);
        method.setAccessible(true);
        DadesUsuariPluginLdapCaib plugin = crearPlugin();

        assertThatThrownBy(() -> method.invoke(plugin, userInfo, true))
                .hasCauseInstanceOf(Exception.class);
    }

    private DadesUsuari invocarToDadesUsuari(UserInfo userInfo, boolean ambRols) throws Exception {
        Method method = DadesUsuariPluginLdapCaib.class.getDeclaredMethod("toDadesUsuari", UserInfo.class, boolean.class);
        method.setAccessible(true);
        return (DadesUsuari) method.invoke(crearPlugin(), userInfo, ambRols);
    }

    // ------------------------- firstDadesUsuari (privat, per reflexió) -------------------------

    @Test
    void firstDadesUsuariAmbResultatNulRetornaNull() throws Exception {
        assertThat(invocarFirstDadesUsuari(null)).isNull();
    }

    @Test
    void firstDadesUsuariAmbLlistaBuidaRetornaNull() throws Exception {
        assertThat(invocarFirstDadesUsuari(new SearchUsersResult(Collections.emptyList()))).isNull();
    }

    @Test
    void firstDadesUsuariAmbUsuarisRetornaElPrimer() throws Exception {
        UserInfo u1 = new UserInfo();
        u1.setUsername("primer");
        UserInfo u2 = new UserInfo();
        u2.setUsername("segon");

        DadesUsuari dadesUsuari = invocarFirstDadesUsuari(new SearchUsersResult(List.of(u1, u2)));

        assertThat(dadesUsuari.getCodi()).isEqualTo("primer");
    }

    private DadesUsuari invocarFirstDadesUsuari(SearchUsersResult result) throws Exception {
        Method method = DadesUsuariPluginLdapCaib.class.getDeclaredMethod("firstDadesUsuari", SearchUsersResult.class, boolean.class);
        method.setAccessible(true);
        return (DadesUsuari) method.invoke(crearPlugin(), result, false);
    }

    // ------------------------- toDadesUsuariList (privat, per reflexió) -------------------------

    @Test
    void toDadesUsuariListAmbResultatNulRetornaListaBuida() throws Exception {
        assertThat(invocarToDadesUsuariList(null)).isEmpty();
    }

    @Test
    void toDadesUsuariListMapejaTotsElsUsuaris() throws Exception {
        UserInfo u1 = new UserInfo();
        u1.setUsername("u1");
        UserInfo u2 = new UserInfo();
        u2.setUsername("u2");

        List<DadesUsuari> resultat = invocarToDadesUsuariList(new SearchUsersResult(List.of(u1, u2)));

        assertThat(resultat).extracting(DadesUsuari::getCodi).containsExactly("u1", "u2");
    }

    @SuppressWarnings("unchecked")
    private List<DadesUsuari> invocarToDadesUsuariList(SearchUsersResult result) throws Exception {
        Method method = DadesUsuariPluginLdapCaib.class.getDeclaredMethod("toDadesUsuariList", SearchUsersResult.class);
        method.setAccessible(true);
        return (List<DadesUsuari>) method.invoke(crearPlugin(), result);
    }

    // ------------------------- filtre LDAP (privat, per reflexió) -------------------------

    @Test
    void escapeLdapFilterValueEscapaCaractersEspecials() throws Exception {
        assertThat(invocarEscapeLdapFilterValue("a\\b*c(d)e f"))
                .isEqualTo("a\\5cb\\2ac\\28d\\29e f");
    }

    @Test
    void escapeLdapFilterValueEscapaCaracterNul() throws Exception {
        assertThat(invocarEscapeLdapFilterValue("a\u0000b")).isEqualTo("a\\00b");
    }

    @Test
    void escapeLdapFilterValueAmbNullRetornaCadenaBuida() throws Exception {
        assertThat(invocarEscapeLdapFilterValue(null)).isEmpty();
    }

    @Test
    void escapeLdapFilterValueSenseCaractersEspecialsNoModifica() throws Exception {
        assertThat(invocarEscapeLdapFilterValue("textNormal123")).isEqualTo("textNormal123");
    }

    @Test
    void toContainsLdapFilterValueEnvoltaAmbAsteriscs() throws Exception {
        Method method = DadesUsuariPluginLdapCaib.class.getDeclaredMethod("toContainsLdapFilterValue", String.class);
        method.setAccessible(true);
        assertThat(method.invoke(crearPlugin(), "text")).isEqualTo("*text*");
    }

    private String invocarEscapeLdapFilterValue(String text) throws Exception {
        Method method = DadesUsuariPluginLdapCaib.class.getDeclaredMethod("escapeLdapFilterValue", String.class);
        method.setAccessible(true);
        return (String) method.invoke(crearPlugin(), text);
    }
}
