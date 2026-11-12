package es.caib.pinbal.plugin.usuari;

import es.caib.pinbal.plugin.SistemaExternException;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * El plugin de Keycloak sense les propietats obligatòries (serverurl, realm, client_id, ...)
 * falla de seguida amb una excepció (getPropertyRequired) sense arribar a fer cap crida de xarxa,
 * per la qual cosa aquests tests es poden executar sense un servidor Keycloak real.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
class DadesUsuariPluginKeycloakTest {

    private DadesUsuariPluginKeycloak crearPlugin() {
        return new DadesUsuariPluginKeycloak("", new Properties());
    }

    @Test
    void consultarAmbUsuariCodiSenseConfiguracioLlancaSistemaExternException() {
        assertThatThrownBy(() -> crearPlugin().consultarAmbUsuariCodi("usuari1"))
                .isInstanceOf(SistemaExternException.class);
    }

    @Test
    void consultarAmbUsuariNifSenseConfiguracioLlancaSistemaExternException() {
        assertThatThrownBy(() -> crearPlugin().consultarAmbUsuariNif("12345678A"))
                .isInstanceOf(SistemaExternException.class);
    }

    @Test
    void consultarAmbUsuariNomSenseConfiguracioLlancaSistemaExternException() {
        assertThatThrownBy(() -> crearPlugin().consultarAmbUsuariNom("Joan"))
                .isInstanceOf(SistemaExternException.class);
    }

    @Test
    void consultarAmbUsuariAnySenseConfiguracioLlancaSistemaExternException() {
        assertThatThrownBy(() -> crearPlugin().consultarAmbUsuariAny(" text "))
                .isInstanceOf(SistemaExternException.class);
    }

    @Test
    void findAmbGrupSenseConfiguracioRetornaListaBuidaSenseLlancarExcepcio() throws Exception {
        // getUsernamesByRol intercepta internament els errors de cada font (client app, client
        // persones, realm) i només llança excepció si totes tres fonts fallen sense capturar-ho;
        // com que el propi mètode ho captura sempre, findAmbGrup no llança excepció.
        List<DadesUsuari> resultat = crearPlugin().findAmbGrup("grup1");
        assertThat(resultat).isEmpty();
    }

    @Test
    void getNomCompletAmbNomICognomsRetornaNomComplet() throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("Joan");
        userInfo.setSurname1("March");
        userInfo.setSurname2("Bonnin");

        assertThat(invocarGetNomComplet(userInfo)).isEqualTo("Joan March Bonnin");
    }

    @Test
    void getNomCompletNomesAmbNomRetornaNomes() throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("Joan");

        assertThat(invocarGetNomComplet(userInfo)).isEqualTo("Joan");
    }

    @Test
    void getNomCompletAmbNomIUnCognomRetornaNomICognom() throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("Joan");
        userInfo.setSurname1("March");

        assertThat(invocarGetNomComplet(userInfo)).isEqualTo("Joan March");
    }

    private String invocarGetNomComplet(UserInfo userInfo) throws Exception {
        Method method = DadesUsuariPluginKeycloak.class.getDeclaredMethod("getNomComplet", UserInfo.class);
        method.setAccessible(true);
        return (String) method.invoke(crearPlugin(), userInfo);
    }


    // ---- consultarAmbUsuariCodi (amb espia de Keycloak) ----

    @Test
    void consultarAmbUsuariCodiAmbUsuariTrobatRetornaDadesUsuariAmbRols() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin());
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("usuari1");
        userInfo.setName("Joan");
        userInfo.setSurname1("March");
        userInfo.setAdministrationID("12345678A");
        userInfo.setEmail("joan@test.com");
        doReturn(userInfo).when(spy).getUserInfoByUserName("usuari1");
        doReturn(new RolesInfo("usuari1", new String[]{"ROL1", "ROL2"})).when(spy).getRolesByUsername("usuari1");

        DadesUsuari resultat = spy.consultarAmbUsuariCodi("usuari1");

        assertThat(resultat.getCodi()).isEqualTo("usuari1");
        assertThat(resultat.getNom()).isEqualTo("Joan March");
        assertThat(resultat.getNif()).isEqualTo("12345678A");
        assertThat(resultat.getEmail()).isEqualTo("joan@test.com");
        assertThat(resultat.getRols()).containsExactly("ROL1", "ROL2");
    }

    @Test
    void consultarAmbUsuariCodiUsuariNoTrobatRetornaNullSenseConsultarRols() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin());
        doReturn(null).when(spy).getUserInfoByUserName("inexistent");

        assertThat(spy.consultarAmbUsuariCodi("inexistent")).isNull();
        verify(spy, never()).getRolesByUsername(any());
    }

    @Test
    void consultarAmbUsuariCodiAmbExcepcioLlancaSistemaExternException() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin());
        doThrow(new RuntimeException("boom")).when(spy).getUserInfoByUserName("usuari1");

        assertThatThrownBy(() -> spy.consultarAmbUsuariCodi("usuari1"))
                .isInstanceOf(SistemaExternException.class)
                .hasCauseInstanceOf(RuntimeException.class);
    }


    // ---- consultarAmbUsuariNif ----

    @Test
    void consultarAmbUsuariNifAmbUsuariTrobatConsultaRolsAmbElUsernameResolt() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(propietatsCerca()));
        UsersResource usersResource = stubUsersResource(spy);
        when(usersResource.list(eq(0), eq(500)))
                .thenReturn(Collections.singletonList(userRep("usuari-resolt", "Maria", "Bonet", null, "12345678A")));
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("usuari-resolt");
        userInfo.setName("Maria");
        userInfo.setSurname1("Bonet");
        userInfo.setAdministrationID("12345678A");
        userInfo.setEmail("maria@test.com");
        doReturn(userInfo).when(spy).getUserInfoByUserName("usuari-resolt");
        doReturn(new RolesInfo("usuari-resolt", new String[]{"ROL1"})).when(spy).getRolesByUsername("usuari-resolt");

        DadesUsuari resultat = spy.consultarAmbUsuariNif("12345678a");

        assertThat(resultat.getCodi()).isEqualTo("usuari-resolt");
        // El nom ha de ser el nom complet (nom + llinatges), no només el nom de pila
        assertThat(resultat.getNom()).isEqualTo("Maria Bonet");
        assertThat(resultat.getEmail()).isEqualTo("maria@test.com");
        assertThat(resultat.getRols()).containsExactly("ROL1");
        // Les rols es consulten amb l'username resolt, no amb el NIF cercat
        verify(spy).getRolesByUsername("usuari-resolt");
        verify(spy, never()).getRolesByUsername("12345678A");
        // No s'empra la consulta per NIF de la llibreria (cache interna)
        verify(spy, never()).getUserInfoByAdministrationID(any());
    }

    @Test
    void consultarAmbUsuariNifNoEsTrobaPerCoincidenciaParcial() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(propietatsCerca()));
        UsersResource usersResource = stubUsersResource(spy);
        when(usersResource.list(eq(0), eq(500)))
                .thenReturn(Collections.singletonList(userRep("u1", "A", null, null, "12345678A")));

        assertThat(spy.consultarAmbUsuariNif("2345678A")).isNull();
    }

    @Test
    void consultarAmbUsuariNifUsuariNoTrobatRetornaNull() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(propietatsCerca()));
        UsersResource usersResource = stubUsersResource(spy);
        when(usersResource.list(eq(0), eq(500))).thenReturn(Collections.emptyList());

        assertThat(spy.consultarAmbUsuariNif("00000000Z")).isNull();
        verify(spy, never()).getUserInfoByUserName(any());
    }

    @Test
    void consultarAmbUsuariNifNoTrobatAmbIndexRecentNoReconstrueixIndex() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(propietatsCerca()));
        UsersResource usersResource = stubUsersResource(spy);
        when(usersResource.list(eq(0), eq(500))).thenReturn(Collections.emptyList());

        spy.consultarAmbUsuariNif("00000000Z");
        spy.consultarAmbUsuariNif("11111111H");

        // Només la construcció inicial: l'índex és més recent que l'interval mínim de reconstrucció
        verify(usersResource, org.mockito.Mockito.times(1)).list(any(Integer.class), any(Integer.class));
    }

    @Test
    void consultarAmbUsuariNifNoTrobatReconstrueixIndexITrobaUsuariNou() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(propietatsCerca()));
        spy.nifIndexRefreshMinMillis = 0;
        UsersResource usersResource = stubUsersResource(spy);
        // Primera construcció: l'usuari encara no existeix; segona: ja s'ha donat d'alta
        when(usersResource.list(eq(0), eq(500)))
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.singletonList(userRep("nou", "Nou", null, null, "43123456X")));
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("nou");
        userInfo.setName("Nou");
        doReturn(userInfo).when(spy).getUserInfoByUserName("nou");
        doReturn(new RolesInfo("nou", new String[]{})).when(spy).getRolesByUsername("nou");

        assertThat(spy.consultarAmbUsuariNif("43123456X").getCodi()).isEqualTo("nou");
        verify(usersResource, org.mockito.Mockito.times(2)).list(any(Integer.class), any(Integer.class));
    }

    @Test
    void consultarAmbUsuariNifAmbExcepcioLlancaSistemaExternException() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(propietatsCerca()));
        UsersResource usersResource = stubUsersResource(spy);
        when(usersResource.list(any(Integer.class), any(Integer.class))).thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> spy.consultarAmbUsuariNif("12345678A"))
                .isInstanceOf(SistemaExternException.class);
    }


    // ---- consultarAmbUsuariNom ----

    @Test
    void consultarAmbUsuariNomRetornaPrimerUsuariTrobat() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin());
        UserInfo primer = new UserInfo();
        primer.setUsername("usuari1");
        primer.setName("Joan");
        primer.setAdministrationID("12345678A");
        primer.setEmail("joan@test.com");
        UserInfo segon = new UserInfo();
        segon.setUsername("usuari2");
        doReturn(new SearchUsersResult(Arrays.asList(primer, segon))).when(spy).getUsersByPartialNameOrPartialSurnames("Joan");
        doReturn(new RolesInfo("usuari1", new String[]{"ROL1"})).when(spy).getRolesByUsername("usuari1");

        DadesUsuari resultat = spy.consultarAmbUsuariNom("Joan");

        assertThat(resultat.getCodi()).isEqualTo("usuari1");
        assertThat(resultat.getRols()).containsExactly("ROL1");
    }

    @Test
    void consultarAmbUsuariNomResultatNullRetornaNull() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin());
        doReturn(null).when(spy).getUsersByPartialNameOrPartialSurnames("Joan");

        assertThat(spy.consultarAmbUsuariNom("Joan")).isNull();
    }

    @Test
    void consultarAmbUsuariNomAmbLlistaBuidaRetornaNull() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin());
        doReturn(new SearchUsersResult(Collections.emptyList())).when(spy).getUsersByPartialNameOrPartialSurnames("Joan");

        assertThat(spy.consultarAmbUsuariNom("Joan")).isNull();
    }

    @Test
    void consultarAmbUsuariNomAmbExcepcioLlancaSistemaExternException() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin());
        doThrow(new RuntimeException("boom")).when(spy).getUsersByPartialNameOrPartialSurnames("Joan");

        assertThatThrownBy(() -> spy.consultarAmbUsuariNom("Joan"))
                .isInstanceOf(SistemaExternException.class);
    }


    // ---- consultarAmbUsuariAny ----

    @Test
    void consultarAmbUsuariAnyCombinaCercaKeycloakIIndexNifSenseDuplicats() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(propietatsCerca()));
        UsersResource usersResource = stubUsersResource(spy);
        // Usuari d'aplicació sense NIF: abans deixava de trobar-se un cop la cache de la llibreria era completa
        UserRepresentation app = userRep("$app_text", null, null, null, null);
        UserRepresentation persona = userRep("ptext", "Joan", "March", "joan@test.com", "11111111H");
        when(usersResource.search(eq("text"), eq(0), eq(30), eq(false))).thenReturn(Arrays.asList(app, persona));
        // L'índex de NIFs conté la mateixa persona (no s'ha de duplicar) i una altra amb NIF que conté "text"
        UserRepresentation perNif = userRep("maria", "Maria", "Bonet", "maria@test.com", "TEXT1234");
        when(usersResource.list(eq(0), eq(500))).thenReturn(Arrays.asList(persona, perNif, app));

        List<DadesUsuari> resultat = spy.consultarAmbUsuariAny(" text ");

        assertThat(resultat).extracting(DadesUsuari::getCodi).containsExactly("$app_text", "ptext", "maria");
        DadesUsuari joan = resultat.get(1);
        assertThat(joan.getNom()).isEqualTo("Joan March");
        assertThat(joan.getNif()).isEqualTo("11111111H");
        assertThat(joan.getEmail()).isEqualTo("joan@test.com");
        assertThat(joan.getRols()).isNull();
        assertThat(resultat.get(2).getNif()).isEqualTo("TEXT1234");
    }

    @Test
    void consultarAmbUsuariAnyTrobaPerNifParcialSenseDistingirMajuscules() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(propietatsCerca()));
        UsersResource usersResource = stubUsersResource(spy);
        when(usersResource.search(any(String.class), any(Integer.class), any(Integer.class), any(Boolean.class)))
                .thenReturn(Collections.emptyList());
        when(usersResource.list(eq(0), eq(500))).thenReturn(Arrays.asList(
                userRep("u1", "A", null, null, "43123456X"),
                userRep("u2", "B", null, null, "99999999R")));

        assertThat(spy.consultarAmbUsuariAny("3456x")).extracting(DadesUsuari::getCodi).containsExactly("u1");
    }

    @Test
    void consultarAmbUsuariAnyReutilitzaIndexNifEntreCerques() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(propietatsCerca()));
        UsersResource usersResource = stubUsersResource(spy);
        when(usersResource.search(any(String.class), any(Integer.class), any(Integer.class), any(Boolean.class)))
                .thenReturn(Collections.emptyList());
        when(usersResource.list(eq(0), eq(500))).thenReturn(Collections.singletonList(userRep("u1", "A", null, null, "43123456X")));

        spy.consultarAmbUsuariAny("4312");
        spy.consultarAmbUsuariAny("3456");

        verify(usersResource, org.mockito.Mockito.times(1)).list(any(Integer.class), any(Integer.class));
    }

    @Test
    void consultarAmbUsuariAnyReconstrueixIndexNifQuanCaduca() throws Exception {
        Properties props = propietatsCerca();
        props.setProperty(DadesUsuariPluginKeycloak.PROPERTY_NIF_INDEX_TTL_MINUTS, "0");
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(props));
        UsersResource usersResource = stubUsersResource(spy);
        when(usersResource.search(any(String.class), any(Integer.class), any(Integer.class), any(Boolean.class)))
                .thenReturn(Collections.emptyList());
        when(usersResource.list(eq(0), eq(500)))
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.singletonList(userRep("nou", "Nou", null, null, "43123456X")));

        assertThat(spy.consultarAmbUsuariAny("4312")).isEmpty();
        assertThat(spy.consultarAmbUsuariAny("4312")).extracting(DadesUsuari::getCodi).containsExactly("nou");
    }

    @Test
    void consultarAmbUsuariAnyPaginaElLlistatPerConstruirIndexNif() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(propietatsCerca()));
        UsersResource usersResource = stubUsersResource(spy);
        when(usersResource.search(any(String.class), any(Integer.class), any(Integer.class), any(Boolean.class)))
                .thenReturn(Collections.emptyList());
        List<UserRepresentation> primeraPagina = new java.util.ArrayList<>();
        for (int i = 0; i < 500; i++) {
            primeraPagina.add(userRep("u" + i, "N", null, null, "0000" + i + "A"));
        }
        when(usersResource.list(eq(0), eq(500))).thenReturn(primeraPagina);
        when(usersResource.list(eq(500), eq(500))).thenReturn(Collections.singletonList(userRep("darrer", "N", null, null, "77777777B")));

        assertThat(spy.consultarAmbUsuariAny("77777777")).extracting(DadesUsuari::getCodi).containsExactly("darrer");
    }

    @Test
    void consultarAmbUsuariAnySenseAtributNifConfiguratNoRecorreElsUsuaris() throws Exception {
        Properties props = propietatsCerca();
        props.remove(PROP_MAPPING_NIF);
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(props));
        UsersResource usersResource = stubUsersResource(spy);
        when(usersResource.search(eq("text"), eq(0), eq(30), eq(false)))
                .thenReturn(Collections.singletonList(userRep("ptext", "Joan", null, null, null)));

        assertThat(spy.consultarAmbUsuariAny("text")).extracting(DadesUsuari::getCodi).containsExactly("ptext");
        verify(usersResource, never()).list(any(Integer.class), any(Integer.class));
    }

    @Test
    void consultarAmbUsuariAnyAmbMassaResultatsRetornaLlistaBuida() throws Exception {
        Properties props = propietatsCerca();
        props.setProperty("pluginsib.userinformation.keycloak.maxallowednumberofresultsinpartialsearches", "1");
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(props));
        UsersResource usersResource = stubUsersResource(spy);
        when(usersResource.search(eq("text"), eq(0), eq(30), eq(false)))
                .thenReturn(Arrays.asList(userRep("a_text", "A", null, null, null), userRep("b_text", "B", null, null, null)));

        assertThat(spy.consultarAmbUsuariAny("text")).isEmpty();
    }

    @Test
    void consultarAmbUsuariAnyAmbTextMassaCurtORetornaLlistaBuidaSenseConsultarKeycloak() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(propietatsCerca()));
        UsersResource usersResource = stubUsersResource(spy);

        assertThat(spy.consultarAmbUsuariAny(null)).isEmpty();
        assertThat(spy.consultarAmbUsuariAny("ab")).isEmpty();
        verify(usersResource, never()).search(any(String.class), any(Integer.class), any(Integer.class), any(Boolean.class));
        verify(usersResource, never()).list(any(Integer.class), any(Integer.class));
    }

    @Test
    void consultarAmbUsuariAnyAmbExcepcioLlancaSistemaExternException() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(propietatsCerca()));
        UsersResource usersResource = stubUsersResource(spy);
        when(usersResource.search(any(String.class), any(Integer.class), any(Integer.class), any(Boolean.class)))
                .thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> spy.consultarAmbUsuariAny("text"))
                .isInstanceOf(SistemaExternException.class);
    }

    private static final String PROP_MAPPING_NIF = "pluginsib.userinformation.keycloak.mapping.administrationID";

    private Properties propietatsCerca() {
        Properties props = new Properties();
        props.setProperty(PROP_MAPPING_NIF, "nif");
        return props;
    }

    private UsersResource stubUsersResource(DadesUsuariPluginKeycloak spy) throws Exception {
        UsersResource usersResource = mock(UsersResource.class);
        metodePare("getKeyCloakConnectionForUsers").invoke(doReturn(usersResource).when(spy));
        return usersResource;
    }

    private UserRepresentation userRep(String username, String nom, String llinatges, String email, String nif) {
        UserRepresentation ur = new UserRepresentation();
        ur.setUsername(username);
        ur.setFirstName(nom);
        ur.setLastName(llinatges);
        ur.setEmail(email);
        if (nif != null) {
            java.util.Map<String, List<String>> attrs = new java.util.HashMap<>();
            attrs.put("nif", Collections.singletonList(nif));
            ur.setAttributes(attrs);
        }
        return ur;
    }


    // ---- findAmbGrup ----

    @Test
    void findAmbGrupAmbUsuarisElsMapejaPerCodi() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin());
        doReturn(new String[]{"usuari1", "usuari2"}).when(spy).getUsernamesByRol("grup1");

        List<DadesUsuari> resultat = spy.findAmbGrup("grup1");

        assertThat(resultat.stream().map(DadesUsuari::getCodi).collect(Collectors.toList()))
                .containsExactly("usuari1", "usuari2");
    }

    @Test
    void findAmbGrupSenseUsuarisRetornaLlistaBuida() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin());
        doReturn(null).when(spy).getUsernamesByRol("grup1");

        assertThat(spy.findAmbGrup("grup1")).isEmpty();
    }

    @Test
    void findAmbGrupAmbArrayBuitRetornaLlistaBuida() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin());
        doReturn(new String[0]).when(spy).getUsernamesByRol("grup1");

        assertThat(spy.findAmbGrup("grup1")).isEmpty();
    }

    @Test
    void findAmbGrupAmbExcepcioLlancaSistemaExternException() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin());
        doThrow(new RuntimeException("boom")).when(spy).getUsernamesByRol("grup1");

        assertThatThrownBy(() -> spy.findAmbGrup("grup1"))
                .isInstanceOf(SistemaExternException.class);
    }


    // ---- getUsernamesByRol (unió de les 3 fonts: client app, client persones, realm) ----

    private static final String PROP_CLIENT_APP = "pluginsib.userinformation.keycloak.client_id";
    private static final String PROP_CLIENT_PERSONES = "pluginsib.userinformation.keycloak.client_id_for_user_autentication";
    private static final String PROP_REALM = "pluginsib.userinformation.keycloak.realm";

    private Properties propietatsAmbClients() {
        Properties props = new Properties();
        props.setProperty(PROP_CLIENT_APP, "app-client");
        props.setProperty(PROP_CLIENT_PERSONES, "persons-client");
        props.setProperty(PROP_REALM, "realm1");
        return props;
    }

    private DadesUsuariPluginKeycloak crearPlugin(Properties props) {
        return new DadesUsuariPluginKeycloak("", props);
    }

    private Set<UserRepresentation> userReps(String... usernames) {
        Set<UserRepresentation> reps = new HashSet<>();
        for (String username : usernames) {
            UserRepresentation ur = new UserRepresentation();
            ur.setUsername(username);
            reps.add(ur);
        }
        return reps;
    }

    /**
     * Configura la cadena de mocks Keycloak necessària per a que getUsernamesByRolOfClient(rol, clientIdValue)
     * retorni els usernames indicats via keycloak.realm(realm).clients().findByClientId(...).get(...).roles().get(rol).
     */
    private void mockClientAmbUsuaris(
            Keycloak keycloakMock,
            ClientsResource clientsResourceMock,
            String clientIdValue,
            String clientDbId,
            String rol,
            String... usernames) throws Exception {
        ClientRepresentation clientRep = new ClientRepresentation();
        clientRep.setId(clientDbId);
        when(clientsResourceMock.findByClientId(clientIdValue)).thenReturn(Collections.singletonList(clientRep));

        ClientResource clientResourceMock = mock(ClientResource.class);
        when(clientsResourceMock.get(clientDbId)).thenReturn(clientResourceMock);
        RolesResource rolesResourceMock = mock(RolesResource.class);
        when(clientResourceMock.roles()).thenReturn(rolesResourceMock);
        RoleResource roleResourceMock = mock(RoleResource.class);
        when(rolesResourceMock.get(rol)).thenReturn(roleResourceMock);
        when(roleResourceMock.getRoleUserMembers()).thenReturn(userReps(usernames));
    }

    private void mockRealmAmbUsuaris(DadesUsuariPluginKeycloak spy, String rol, String... usernames) throws Exception {
        RolesResource realmRolesResourceMock = mock(RolesResource.class);
        stubGetKeyCloakConnectionForRoles(spy, realmRolesResourceMock);
        RoleResource realmRoleResourceMock = mock(RoleResource.class);
        when(realmRolesResourceMock.get(rol)).thenReturn(realmRoleResourceMock);
        when(realmRoleResourceMock.getRoleUserMembers()).thenReturn(userReps(usernames));
    }

    /*
     * getKeyCloakConnection()/getKeyCloakConnectionForRoles() són mètodes "protected" heretats de
     * KeyCloakUserInformationPlugin (paquet org.fundaciobit.pluginsib.userinformation.keycloak), per la
     * qual cosa no es poden invocar en codi font des d'aquest test (paquet diferent i no som subclasse).
     * S'empra reflexió per registrar l'stub de Mockito sobre l'objecte "en mode gravació" que retorna
     * doReturn/doThrow.
     */
    private static Method metodePare(String nom) throws NoSuchMethodException {
        Method m = org.fundaciobit.pluginsib.userinformation.keycloak.KeyCloakUserInformationPlugin.class.getDeclaredMethod(nom);
        m.setAccessible(true);
        return m;
    }

    private void stubGetKeyCloakConnection(DadesUsuariPluginKeycloak spy, Keycloak value) throws Exception {
        metodePare("getKeyCloakConnection").invoke(doReturn(value).when(spy));
    }

    private void stubGetKeyCloakConnectionThrows(DadesUsuariPluginKeycloak spy, Throwable ex) throws Exception {
        metodePare("getKeyCloakConnection").invoke(doThrow(ex).when(spy));
    }

    private void stubGetKeyCloakConnectionForRoles(DadesUsuariPluginKeycloak spy, RolesResource value) throws Exception {
        metodePare("getKeyCloakConnectionForRoles").invoke(doReturn(value).when(spy));
    }

    private void stubGetKeyCloakConnectionForRolesThrows(DadesUsuariPluginKeycloak spy, Throwable ex) throws Exception {
        metodePare("getKeyCloakConnectionForRoles").invoke(doThrow(ex).when(spy));
    }

    @Test
    void getUsernamesByRolUneixUsuarisDeLesTresFontsOrdenatsSenseDuplicats() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(propietatsAmbClients()));

        Keycloak keycloakMock = mock(Keycloak.class);
        stubGetKeyCloakConnection(spy, keycloakMock);
        RealmResource realmResourceMock = mock(RealmResource.class);
        when(keycloakMock.realm("realm1")).thenReturn(realmResourceMock);
        ClientsResource clientsResourceMock = mock(ClientsResource.class);
        when(realmResourceMock.clients()).thenReturn(clientsResourceMock);

        mockClientAmbUsuaris(keycloakMock, clientsResourceMock, "app-client", "app-id", "grup1", "userB", "userA");
        mockClientAmbUsuaris(keycloakMock, clientsResourceMock, "persons-client", "persons-id", "grup1", "userB", "userC");
        mockRealmAmbUsuaris(spy, "grup1", "userC", "userD");

        String[] resultat = spy.getUsernamesByRol("grup1");

        assertThat(resultat).containsExactly("userA", "userB", "userC", "userD");
    }

    @Test
    void getUsernamesByRolUnaFontFallaLesAltresFuncionenIRetornenUnio() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(propietatsAmbClients()));

        Keycloak keycloakMock = mock(Keycloak.class);
        stubGetKeyCloakConnection(spy, keycloakMock);
        RealmResource realmResourceMock = mock(RealmResource.class);
        when(keycloakMock.realm("realm1")).thenReturn(realmResourceMock);
        ClientsResource clientsResourceMock = mock(ClientsResource.class);
        when(realmResourceMock.clients()).thenReturn(clientsResourceMock);

        // Client app: la consulta de membres de rol falla (es captura internament i retorna null)
        ClientRepresentation clientRepApp = new ClientRepresentation();
        clientRepApp.setId("app-id");
        when(clientsResourceMock.findByClientId("app-client")).thenReturn(Collections.singletonList(clientRepApp));
        ClientResource clientResourceAppMock = mock(ClientResource.class);
        when(clientsResourceMock.get("app-id")).thenReturn(clientResourceAppMock);
        RolesResource rolesResourceAppMock = mock(RolesResource.class);
        when(clientResourceAppMock.roles()).thenReturn(rolesResourceAppMock);
        RoleResource roleResourceAppMock = mock(RoleResource.class);
        when(rolesResourceAppMock.get("grup1")).thenReturn(roleResourceAppMock);
        when(roleResourceAppMock.getRoleUserMembers()).thenThrow(new RuntimeException("kaboom"));

        mockClientAmbUsuaris(keycloakMock, clientsResourceMock, "persons-client", "persons-id", "grup1", "userB");
        mockRealmAmbUsuaris(spy, "grup1", "userC");

        String[] resultat = spy.getUsernamesByRol("grup1");

        assertThat(resultat).containsExactly("userB", "userC");
    }

    @Test
    void getUsernamesByRolTotesFontsFallenRetornaNull() throws Exception {
        DadesUsuariPluginKeycloak spy = spy(crearPlugin(propietatsAmbClients()));
        stubGetKeyCloakConnectionThrows(spy, new RuntimeException("sense connexio"));
        stubGetKeyCloakConnectionForRolesThrows(spy, new RuntimeException("sense connexio"));

        assertThat(spy.getUsernamesByRol("grup1")).isNull();
    }
}
