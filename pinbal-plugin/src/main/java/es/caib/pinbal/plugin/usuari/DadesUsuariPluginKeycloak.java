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
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
					.nom(userInfo.getName())
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
			var userInfo = getUserInfoByAdministrationID(usuariNif);
			log.info("[Keycloak] Dades de l'usuari" + usuariNif + " " + userInfo);
			PluginMetricHelper.addSuccessOperation(IntegracioApp.USR, System.currentTimeMillis() - startTime);
			if (userInfo == null) {
				return null;
			}
			return DadesUsuari.builder()
					.codi(userInfo.getUsername())
					.nom(getNomComplet(userInfo))
					.nom(userInfo.getName())
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

	@Override
	public List<DadesUsuari> consultarAmbUsuariAny(String text) throws SistemaExternException {

		String searchText = text != null ? text.trim() : null;
		log.info("[Keycloak] Consulta de les dades dels usuaris (text={})", searchText);
		try {
			long startTime = System.currentTimeMillis();
			// Cerca per username (codi), nom i NIF simultàniament (OR)
			SearchUsersResult result = getUsersByPartialValuesOr(searchText, searchText, searchText, null, searchText);
			PluginMetricHelper.addSuccessOperation(IntegracioApp.USR, System.currentTimeMillis() - startTime);
			if (result == null || result.getUsers() == null) {
				return new ArrayList<>();
			}
			return result.getUsers().stream()
					.map(u -> DadesUsuari.builder()
							.codi(u.getUsername())
							.nom(getNomComplet(u))
							.nif(u.getAdministrationID())
							.email(u.getEmail())
							.build())
					.collect(Collectors.toList());
		} catch (Exception ex) {
			PluginMetricHelper.addErrorOperation(IntegracioApp.USR);
			throw new SistemaExternException("Error al consultar les dades dels usuaris (text=" + searchText + ")", ex);
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
}
