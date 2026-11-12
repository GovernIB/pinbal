package es.caib.pinbal.plugin.usuari;

import es.caib.comanda.ms.salut.helper.IntegracioApp;
import es.caib.pinbal.plugin.PluginMetricHelper;
import es.caib.pinbal.plugin.SistemaExternException;
import lombok.extern.slf4j.Slf4j;
import org.fundaciobit.pluginsib.userinformation.IUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.keycloak.KeyCloakUserInformationPlugin;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant JDBC.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class DadesUsuariPluginKeycloak extends KeyCloakUserInformationPlugin implements DadesUsuariPlugin, IUserInformationPlugin {


	public DadesUsuariPluginKeycloak(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
		PluginMetricHelper.addEndpoint(IntegracioApp.USR, this.getProperty(SERVER_URL_PROPERTY));
	}


	@Override
	public DadesUsuari consultarAmbUsuariCodi(String usuariCodi) throws SistemaExternException {

		log.info("[Keycloak] Consulta de les dades de l'usuari (usuariCodi=" + usuariCodi + ")");
		try {
            long startTime = System.currentTimeMillis();
			var userInfo = getUserInfoByUserName(usuariCodi);
			log.info("[Keycloak] Dades de l'usuari" + usuariCodi + " " + userInfo);
			PluginMetricHelper.addSuccessOperation(IntegracioApp.USR, System.currentTimeMillis() - startTime);
			if (userInfo == null) {
				return null;
			}
			return DadesUsuari.builder()
					.codi(userInfo.getUsername())
					.nom(getNomComplet(userInfo))
					.nif(userInfo.getAdministrationID())
					.email(userInfo.getEmail())
					.rols(getRolesByUsername(usuariCodi).getRoles())
					.build();
		} catch (Exception ex) {
			PluginMetricHelper.addErrorOperation(IntegracioApp.USR);
			throw new SistemaExternException("Error al consultar les dades de l'usuari (usuariCodi=" + usuariCodi + ")", ex);
		}
	}

	@Override
	public DadesUsuari consultarAmbUsuariNif(String usuariNif) throws SistemaExternException {

		log.info("[Keycloak] Consulta de les dades de l'usuari (usuariNif=" + usuariNif + ")");
		try {
			long startTime = System.currentTimeMillis();
			// No s'empra getUserInfoByAdministrationID de la llibreria: la seva cache interna, un cop
			// "completa", no troba els NIFs d'usuaris donats d'alta després (durant 1 hora). El NIF es
			// resol amb l'índex propi i les dades es consulten sempre a Keycloak pel username.
			String username = resoldreUsernamePerNif(usuariNif);
			UserInfo userInfo = username != null ? getUserInfoByUserName(username) : null;
			log.info("[Keycloak] Dades de l'usuari" + usuariNif + " " + userInfo);
			PluginMetricHelper.addSuccessOperation(IntegracioApp.USR, System.currentTimeMillis() - startTime);
			if (userInfo == null) {
				return null;
			}
			return DadesUsuari.builder()
					.codi(userInfo.getUsername())
					.nom(getNomComplet(userInfo))
					.nif(userInfo.getAdministrationID())
					.email(userInfo.getEmail())
					.rols(getRolesByUsername(userInfo.getUsername()).getRoles())
					.build();
		} catch (Exception ex) {
			PluginMetricHelper.addErrorOperation(IntegracioApp.USR);
			throw new SistemaExternException("Error al consultar les dades de l'usuari (usuariNif=" + usuariNif + ")", ex);
		}
	}

	@Override
	public DadesUsuari consultarAmbUsuariNom(String usuariNom) throws SistemaExternException {

		log.info("[Keycloak] Consulta de les dades de l'usuari (usuariNom={})", usuariNom);
		try {
			long startTime = System.currentTimeMillis();
			SearchUsersResult result = getUsersByPartialNameOrPartialSurnames(usuariNom);
			PluginMetricHelper.addSuccessOperation(IntegracioApp.USR, System.currentTimeMillis() - startTime);
			if (result == null || result.getUsers() == null || result.getUsers().isEmpty()) {
				return null;
			}
			UserInfo userInfo = result.getUsers().get(0);
			return DadesUsuari.builder()
					.codi(userInfo.getUsername())
					.nom(getNomComplet(userInfo))
					.nif(userInfo.getAdministrationID())
					.email(userInfo.getEmail())
					.rols(getRolesByUsername(userInfo.getUsername()).getRoles())
					.build();
		} catch (Exception ex) {
			PluginMetricHelper.addErrorOperation(IntegracioApp.USR);
			throw new SistemaExternException("Error al consultar les dades de l'usuari (usuariNom=" + usuariNom + ")", ex);
		}
	}

	/**
	 * Cerca d'usuaris per codi, nom, cognoms, email o NIF (OR).
	 *
	 * No s'empra getUsersByPartialValuesOr de la llibreria pluginsib perquè, un cop la seva cache interna
	 * NIF→username està "completa", cerca els usernames només dins aquesta cache, que no conté els usuaris
	 * sense NIF (p.ex. usuaris d'aplicació) ni els donats d'alta després de construir-la.
	 *
	 * - Codi, nom, cognoms i email: es cerca directament a Keycloak (paràmetre "search"), sempre al dia.
	 * - NIF: Keycloak no permet cerques parcials per atribut, així que es cerca dins un índex propi
	 *   NIF→usuaris ({@link NifIndex}) que es reconstrueix quan caduca (TTL configurable) o quan es
	 *   reinicialitza el plugin (en buidar les caches usuariAmbCodi/usuariAmbNif de PINBAL).
	 */
	@Override
	public List<DadesUsuari> consultarAmbUsuariAny(String text) throws SistemaExternException {

		String searchText = text != null ? text.trim() : null;
		log.info("[Keycloak] Consulta de les dades dels usuaris (text={})", searchText);
		try {
			long startTime = System.currentTimeMillis();
			List<DadesUsuari> resultat = cercarUsuarisAny(searchText);
			PluginMetricHelper.addSuccessOperation(IntegracioApp.USR, System.currentTimeMillis() - startTime);
			return resultat;
		} catch (Exception ex) {
			PluginMetricHelper.addErrorOperation(IntegracioApp.USR);
			throw new SistemaExternException("Error al consultar les dades dels usuaris (text=" + searchText + ")", ex);
		}
	}

	List<DadesUsuari> cercarUsuarisAny(String searchText) throws Exception {

		if (checkMinimumPartialString(searchText, "text") != null) {
			return new ArrayList<>();
		}
		int maxAllowed = getMaxAllowedNumberOfResults();
		// Clau: username, per evitar duplicats entre la cerca a Keycloak i la cerca per NIF
		Map<String, DadesUsuari> usuaris = new LinkedHashMap<>();

		UsersResource usersResource = getKeyCloakConnectionForUsers();
		int first = 0;
		List<UserRepresentation> pagina;
		while ((pagina = usersResource.search(searchText, first, SEARCH_PAGE_SIZE, false)) != null && !pagina.isEmpty()) {
			for (UserRepresentation user : pagina) {
				if (user.getUsername() != null) {
					usuaris.putIfAbsent(user.getUsername(), toDadesUsuari(userRepresentationToUserInfo(user)));
				}
			}
			if (usuaris.size() > maxAllowed) {
				return massaResultats(searchText, maxAllowed);
			}
			if (pagina.size() < SEARCH_PAGE_SIZE) {
				break;
			}
			first += SEARCH_PAGE_SIZE;
		}

		for (DadesUsuari usuari : getNifIndex().cercar(searchText)) {
			usuaris.putIfAbsent(usuari.getCodi(), usuari);
			if (usuaris.size() > maxAllowed) {
				return massaResultats(searchText, maxAllowed);
			}
		}
		return new ArrayList<>(usuaris.values());
	}

	private List<DadesUsuari> massaResultats(String searchText, int maxAllowed) {
		// Mateix comportament que la cerca de la llibreria: si hi ha massa resultats no se'n retorna cap
		log.warn("[Keycloak] La cerca d'usuaris (text={}) supera el màxim de {} resultats", searchText, maxAllowed);
		return new ArrayList<>();
	}

	private DadesUsuari toDadesUsuari(UserInfo userInfo) {
		return DadesUsuari.builder()
				.codi(userInfo.getUsername())
				.nom(getNomComplet(userInfo))
				.nif(userInfo.getAdministrationID())
				.email(userInfo.getEmail())
				.build();
	}

	// ---- Índex NIF → usuaris ----

	private static final String PROPERTY_MAPPING_NIF = "pluginsib.userinformation.keycloak.mapping.administrationID";
	static final String PROPERTY_NIF_INDEX_TTL_MINUTS = "pluginsib.userinformation.keycloak.nifindex.ttlminuts";
	private static final long NIF_INDEX_TTL_MINUTS_DEFAULT = 60;
	private static final int SEARCH_PAGE_SIZE = 30;
	private static final int LIST_PAGE_SIZE = 500;

	private volatile NifIndex nifIndex;

	/**
	 * Temps mínim entre reconstruccions de l'índex provocades per un NIF que no s'hi troba. Evita que
	 * consultes repetides de NIFs inexistents recorrin tots els usuaris de Keycloak cada vegada.
	 */
	long nifIndexRefreshMinMillis = 60_000L;

	/**
	 * Retorna el username de l'usuari amb el NIF indicat (comparació exacta, sense distingir majúscules).
	 * Si el NIF no és a l'índex, el reconstrueix (com a màxim un cop cada {@link #nifIndexRefreshMinMillis})
	 * per trobar usuaris donats d'alta després de construir-lo.
	 */
	String resoldreUsernamePerNif(String nif) throws Exception {

		if (nif == null || nif.trim().isEmpty()) {
			return null;
		}
		// Sense l'atribut de NIF configurat és un error de configuració, no un "usuari no trobat"
		getPropertyRequired(PROPERTY_MAPPING_NIF);
		List<DadesUsuari> usuaris = getNifIndex().get(nif);
		if (usuaris.isEmpty()) {
			usuaris = getNifIndex(nifIndexRefreshMinMillis).get(nif);
		}
		if (usuaris.isEmpty()) {
			return null;
		}
		if (usuaris.size() > 1) {
			log.warn("[Keycloak] Hi ha {} usuaris amb el NIF {} ({}); s'agafa el primer",
					usuaris.size(), nif, usuaris.stream().map(DadesUsuari::getCodi).collect(Collectors.joining(", ")));
		}
		return usuaris.get(0).getCodi();
	}

	NifIndex getNifIndex() throws Exception {
		return getNifIndex(getNifIndexTtlMillis());
	}

	/** Retorna l'índex, reconstruint-lo si no existeix o és més antic que edatMaximaMillis. */
	synchronized NifIndex getNifIndex(long edatMaximaMillis) throws Exception {
		NifIndex actual = nifIndex;
		if (actual == null || actual.isCaducat(edatMaximaMillis)) {
			actual = construirNifIndex();
			nifIndex = actual;
		}
		return actual;
	}

	private NifIndex construirNifIndex() throws Exception {

		String atributNif = getProperty(PROPERTY_MAPPING_NIF);
		if (atributNif == null || atributNif.trim().isEmpty()) {
			log.warn("[Keycloak] No s'ha definit l'atribut de NIF (mapping.administrationID): no es podrà cercar per NIF");
			return new NifIndex(new HashMap<>());
		}
		long t0 = System.currentTimeMillis();
		Map<String, List<DadesUsuari>> usuarisPerNif = new HashMap<>();
		UsersResource usersResource = getKeyCloakConnectionForUsers();
		int first = 0;
		int total = 0;
		List<UserRepresentation> pagina;
		while ((pagina = usersResource.list(first, LIST_PAGE_SIZE)) != null && !pagina.isEmpty()) {
			total += pagina.size();
			for (UserRepresentation user : pagina) {
				String nif = getPrimerValorAtribut(user, atributNif);
				if (nif != null && user.getUsername() != null) {
					usuarisPerNif.computeIfAbsent(nif.toLowerCase(), k -> new ArrayList<>()).add(
							DadesUsuari.builder()
									.codi(user.getUsername())
									.nom(getNomComplet(user.getFirstName(), user.getLastName()))
									.nif(nif)
									.email(user.getEmail())
									.build());
				}
			}
			if (pagina.size() < LIST_PAGE_SIZE) {
				break;
			}
			first += LIST_PAGE_SIZE;
		}
		log.info("[Keycloak] Índex de NIFs construït: {} usuaris recorreguts, {} NIFs, {} ms",
				total, usuarisPerNif.size(), System.currentTimeMillis() - t0);
		return new NifIndex(usuarisPerNif);
	}

	private long getNifIndexTtlMillis() {
		String ttl = getProperty(PROPERTY_NIF_INDEX_TTL_MINUTS);
		long minuts = NIF_INDEX_TTL_MINUTS_DEFAULT;
		if (ttl != null && !ttl.trim().isEmpty()) {
			try {
				minuts = Long.parseLong(ttl.trim());
			} catch (NumberFormatException ex) {
				log.warn("[Keycloak] Valor incorrecte per a la propietat {}: {}", PROPERTY_NIF_INDEX_TTL_MINUTS, ttl);
			}
		}
		return minuts * 60_000L;
	}

	private static String getPrimerValorAtribut(UserRepresentation user, String atribut) {
		if (user.getAttributes() == null) {
			return null;
		}
		List<String> valors = user.getAttributes().get(atribut);
		if (valors == null || valors.isEmpty() || valors.get(0) == null || valors.get(0).trim().isEmpty()) {
			return null;
		}
		return valors.get(0).trim();
	}

	/** Índex immutable NIF (en minúscules) → usuaris amb aquest NIF. */
	static final class NifIndex {
		private final Map<String, List<DadesUsuari>> usuarisPerNif;
		private final long dataCreacio = System.currentTimeMillis();

		NifIndex(Map<String, List<DadesUsuari>> usuarisPerNif) {
			this.usuarisPerNif = usuarisPerNif;
		}

		boolean isCaducat(long ttlMillis) {
			return System.currentTimeMillis() - dataCreacio >= ttlMillis;
		}

		List<DadesUsuari> get(String nif) {
			List<DadesUsuari> usuaris = usuarisPerNif.get(nif.trim().toLowerCase());
			return usuaris != null ? usuaris : new ArrayList<>();
		}

		List<DadesUsuari> cercar(String nifParcial) {
			String cerca = nifParcial.toLowerCase();
			List<DadesUsuari> resultat = new ArrayList<>();
			for (Map.Entry<String, List<DadesUsuari>> entry : usuarisPerNif.entrySet()) {
				if (entry.getKey().contains(cerca)) {
					resultat.addAll(entry.getValue());
				}
			}
			return resultat;
		}
	}

	@Override
	public List<DadesUsuari> findAmbGrup(String grupCodi) throws SistemaExternException {

		log.info("[Keycloak] Consulta dels usuaris del grup (grupCodi=" + grupCodi + ")");
		try {
            long startTime = System.currentTimeMillis();
			var usuariCodis = getUsernamesByRol(grupCodi);
			log.info("[Keycloak] Usuaris del grup " + grupCodi + " " + Arrays.toString(usuariCodis));
			PluginMetricHelper.addSuccessOperation(IntegracioApp.USR, System.currentTimeMillis() - startTime);
			if (usuariCodis == null || usuariCodis.length == 0) {
				return new ArrayList<>();
			}
			return Arrays.stream(usuariCodis).map(u -> DadesUsuari.builder().codi(u).build()).collect(Collectors.toList());
		} catch (Exception ex) {
			PluginMetricHelper.addErrorOperation(IntegracioApp.USR);
			throw new SistemaExternException("[Keycloak] Error al consultar les dades dels usuaris amb grup (grup=" + grupCodi + ")", ex);
		}
	}

	private String[] getUsuarisByRol(String rol) throws Exception {

		Keycloak keycloak = this.getKeyCloakConnection();
		var rrs = keycloak.realm(this.getPropertyRequired("pluginsib.userinformation.keycloak.realm")).roles();
		try {
			Set<String> users = new HashSet();
			RoleResource rr = rrs.get(rol);
			Set<UserRepresentation> userRep = rr.getRoleUserMembers();
			Iterator var11 = userRep.iterator();

			while(var11.hasNext()) {
				UserRepresentation ur = (UserRepresentation)var11.next();
				users.add(ur.getUsername());
			}
			return !users.isEmpty() ? users.toArray(new String[users.size()]) : null;
		} catch (Exception var13) {
			return null;
		}
	}

	@Override
	public String[] getUsernamesByRol(String rol) throws Exception {

		Set<String> usernamesClientApp = null;
		Set<String> usernamesClientPersons = null;
		Set<String> usersRealm = null;
		int numExcepcions = 0;

        long startTime = System.currentTimeMillis();
		try {
			String appClient = this.getPropertyRequired("pluginsib.userinformation.keycloak.client_id");
			usernamesClientApp = this.getUsernamesByRolOfClient(rol, appClient);
			log.info("[Keycloak] Usuaris pel rol " + rol + " amb el client d'aplicacio " + appClient + " : " + usernamesClientApp);
		} catch (Exception ex) {
			numExcepcions++;
			log.error("[Keycloak] No s'han obtingut usuaris per client d'aplicació amb el rol " + rol, ex);
		}
		try {
			String personsClient = this.getPropertyRequired("pluginsib.userinformation.keycloak.client_id_for_user_autentication");
			usernamesClientPersons = this.getUsernamesByRolOfClient(rol, personsClient);
			log.info("[Keycloak] Usuaris pel rol " + rol + " amb el client de persones " + personsClient + " : " + usernamesClientPersons);
		} catch (Exception ex) {
			numExcepcions++;
			log.error("No s'han obtingut usuaris per client de persones amb el rol " + rol, ex);
		}
		try {
			usersRealm = this.getUsernamesByRolOfRealm(rol);
			log.info("[Keycloak] Usuaris del realm pel rol " + rol + " : " + usersRealm);
		} catch (Exception ex) {
			numExcepcions++;
			log.error("[Keycloak] No s'han obtingut usuaris per realm", ex);
		}

        if (numExcepcions > 0) {
			PluginMetricHelper.addErrorOperation(IntegracioApp.USR);
        } else {
			PluginMetricHelper.addSuccessOperation(IntegracioApp.USR, System.currentTimeMillis() - startTime);
        }

        if (usernamesClientApp == null && usernamesClientPersons == null && usersRealm == null) {
			return null;
		}
		Set<String> users = new TreeSet();
		if (usernamesClientApp != null) {
			users.addAll(usernamesClientApp);
		}

		if (usernamesClientPersons != null) {
			users.addAll(usernamesClientPersons);
		}

		if (usersRealm != null) {
			users.addAll(usersRealm);
		}

		return users.toArray(new String[users.size()]);
	}

	private Set<String> getUsernamesByRolOfRealm(String rol) throws Exception {

		RolesResource roleres = this.getKeyCloakConnectionForRoles();
		try {
			Set<UserRepresentation> userRep = roleres.get(rol).getRoleUserMembers();
			Set<String> users = new HashSet();
			Iterator var5 = userRep.iterator();

			while(var5.hasNext()) {
				UserRepresentation ur = (UserRepresentation)var5.next();
				users.add(ur.getUsername());
			}

			return users;
		} catch (Exception var7) {
			return null;
		}
	}

	private Set<String> getUsernamesByRolOfClient(String rol, String client) throws Exception {

		Keycloak keycloak = this.getKeyCloakConnection();
		ClientsResource clientsApi = keycloak.realm(this.getPropertyRequired("pluginsib.userinformation.keycloak.realm")).clients();
		List<ClientRepresentation> crList = clientsApi.findByClientId(client);
		if (crList == null || crList.isEmpty()) {
			return null;
		}
		ClientResource c = clientsApi.get((crList.get(0)).getId());
		RolesResource rrs = c.roles();

		try {
			Set<String> users = new HashSet();
			RoleResource rr = rrs.get(rol);
			Set<UserRepresentation> userRep = rr.getRoleUserMembers();
			Iterator var11 = userRep.iterator();

			while(var11.hasNext()) {
				UserRepresentation ur = (UserRepresentation)var11.next();
				users.add(ur.getUsername());
			}

			return users;
		} catch (Exception var13) {
			return null;
		}
	}

	private String getNomComplet(UserInfo user) {
		return user.getName() +
				(user.getSurname1() != null ? " " + user.getSurname1() : "") +
				(user.getSurname2() != null ? " " + user.getSurname2() : "");
	}

	/** Equivalent a {@link #getNomComplet(UserInfo)} per a una UserRepresentation (surname2 sempre és null). */
	private String getNomComplet(String firstName, String lastName) {
		return firstName + (lastName != null ? " " + lastName : "");
	}
}
