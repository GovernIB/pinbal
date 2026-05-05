/**
 *
 */
package es.caib.pinbal.plugin.usuari;

import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.pinbal.plugin.PluginMetricHelper;
import es.caib.pinbal.plugin.SistemaExternException;
import lombok.extern.slf4j.Slf4j;
import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.ldap.LdapUserInformationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant el plugin de LDAP. Les propietats necessàries són les següents a partir
 * de es.caib.distribucio.pluginib.dades.usuari.pluginsib.userinformation.ldap. :
 *
 * - serverurl: Url del servidor de keycloak
 * - realm: Realm del keycloak.7
 * - client_id: Client ID del keycloak.
 * - client_id_for_user_autentication: Client ID per autenticació del keycloak.
 * - password_secret: Secret del client de keycloak.
 * - mapping.administrationID: Mapeig del administrationID de keycloak.
 * - debug: Activar el debug del plugin de keycloak.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class DadesUsuariPluginLdapCaib extends LdapUserInformationPlugin implements DadesUsuariPlugin {

	public DadesUsuariPluginLdapCaib(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
		PluginMetricHelper.addEndpoint(IntegracioApp.USR, this.getProperty("pluginsib.userinformation.ldap.host_url"));
	}

	public DadesUsuariPluginLdapCaib(String propertyKeyBase) {
		super(propertyKeyBase);
		PluginMetricHelper.addEndpoint(IntegracioApp.USR, this.getProperty("pluginsib.userinformation.ldap.host_url"));
	}

	@Override
	public DadesUsuari consultarAmbUsuariCodi(String usuariCodi) throws SistemaExternException {

		logger.debug("Consulta de les dades de l'usuari LDAP CAIB (usuariCodi=" + usuariCodi + ")");
		try {
			long startTime = System.currentTimeMillis();
			UserInfo userInfo = getUserInfoByUserName(usuariCodi);
			var dadesUsuari = toDadesUsuari(userInfo, true);
			PluginMetricHelper.addSuccessOperation(IntegracioApp.USR, System.currentTimeMillis() - startTime);
			logger.debug("Dades d'usuari " + dadesUsuari);
			return dadesUsuari;
		} catch (Exception ex) {
			PluginMetricHelper.addErrorOperation(IntegracioApp.USR);
			throw new SistemaExternException("Error al consultar l'usuari amb codi " + usuariCodi, ex);
		}
	}

	@Override
	public DadesUsuari consultarAmbUsuariNif(String usuariNif) throws SistemaExternException {

		logger.debug("Consulta de les dades de l'usuari LDAP CAIB (usuariCodi=" + usuariNif + ")");
		try {
			long startTime = System.currentTimeMillis();
			UserInfo userInfo = getUserInfoByAdministrationID(usuariNif);
			var dadesUsuari = toDadesUsuari(userInfo, true);
			PluginMetricHelper.addSuccessOperation(IntegracioApp.USR, System.currentTimeMillis() - startTime);
			logger.debug("Dades d'usuari " + dadesUsuari);
			return dadesUsuari;
		} catch (Exception ex) {
			PluginMetricHelper.addErrorOperation(IntegracioApp.USR);
			throw new SistemaExternException("Error al consultar l'usuari amb codi " + usuariNif, ex);
		}
	}

	@Override
	public DadesUsuari consultarAmbUsuariNom(String usuariNom) throws SistemaExternException {

		logger.debug("Consulta de les dades de l'usuari LDAP CAIB (usuariNom=" + usuariNom + ")");
		try {
			long startTime = System.currentTimeMillis();
			SearchUsersResult result = getUsersByPartialNameOrPartialSurnames(toContainsLdapFilterValue(usuariNom));
			DadesUsuari dadesUsuari = firstDadesUsuari(result, true);
			PluginMetricHelper.addSuccessOperation(IntegracioApp.USR, System.currentTimeMillis() - startTime);
			logger.debug("Dades d'usuari " + dadesUsuari);
			return dadesUsuari;
		} catch (Exception ex) {
			PluginMetricHelper.addErrorOperation(IntegracioApp.USR);
			throw new SistemaExternException("Error al consultar l'usuari amb nom " + usuariNom, ex);
		}
	}

	@Override
	public List<DadesUsuari> consultarAmbUsuariAny(String text) throws SistemaExternException {

		logger.debug("Consulta de les dades dels usuaris LDAP CAIB (text=" + text + ")");
		try {
			long startTime = System.currentTimeMillis();
			String filtreContingut = toContainsLdapFilterValue(text);
			SearchUsersResult result = getUsersByPartialValuesOr(
					filtreContingut,
					filtreContingut,
					filtreContingut,
					null,
					filtreContingut);
			List<DadesUsuari> dadesUsuaris = toDadesUsuariList(result);
			PluginMetricHelper.addSuccessOperation(IntegracioApp.USR, System.currentTimeMillis() - startTime);
			return dadesUsuaris;
		} catch (Exception ex) {
			PluginMetricHelper.addErrorOperation(IntegracioApp.USR);
			throw new SistemaExternException("Error al consultar els usuaris amb text " + text, ex);
		}
	}

	private String[] findRolsAmbCodi(String usuariCodi) throws Exception {
		logger.debug("Consulta dels rols de l'usuari (usuariCodi=" + usuariCodi + ")");
		long startTime = System.currentTimeMillis();
		var info = getRolesByUsername(usuariCodi);
		return info != null && info.getRoles() != null ? info.getRoles() : new String[]{};
	}

	@Override
	public List<DadesUsuari> findAmbGrup(String grupCodi) throws SistemaExternException {

		logger.debug("Consulta dels usuaris del grup LDAP CAIB (grupCodi=" + grupCodi + ")");
		try {
            long startTime = System.currentTimeMillis();
			List<DadesUsuari> dadesUsuaris = new ArrayList<>();
			String [] codisUsuaris = this.getUsernamesByRol(grupCodi);
			PluginMetricHelper.addSuccessOperation(IntegracioApp.USR, System.currentTimeMillis() - startTime);
			if (codisUsuaris == null) {
				return dadesUsuaris;
			}
			String codiUsuari;
			DadesUsuari dadaUsuari;
			for (var i = 0; i < codisUsuaris.length; i++) {
				codiUsuari= codisUsuaris[i];
				try {
					dadaUsuari = toDadesUsuari(getUserInfoByUserName(codiUsuari));
					if (dadaUsuari != null) {
						dadesUsuaris.add(dadaUsuari);
					}
				} catch(Exception e) {
					logger.error("Error consultant l'usuari amb codi \"" + codiUsuari + "\" en la conslta per rol \"" + grupCodi + "\".", e);
				}
			}
			return dadesUsuaris;
		} catch (Exception ex) {
			PluginMetricHelper.addErrorOperation(IntegracioApp.USR);
			throw new SistemaExternException("Error al consultar els usuaris del grup LDAP CAIB " + grupCodi, ex);
		}
	}

	private DadesUsuari toDadesUsuari(UserInfo userInfo) throws Exception {
		return toDadesUsuari(userInfo, false);
	}

	private DadesUsuari toDadesUsuari(UserInfo userInfo, boolean ambRols) throws Exception {

		if (userInfo == null) {
			return null;
		}
		DadesUsuari dadesUsuari = new DadesUsuari();
		dadesUsuari.setCodi(userInfo.getUsername());
//		dadesUsuari.setNomSencer(userInfo.getFullName());
//		dadesUsuari.setNom(userInfo.getName());
		dadesUsuari.setNom(userInfo.getFullName());
//		dadesUsuari.setLlinatges(userInfo.getSurname1() + (userInfo.getSurname2() != null ? " " + userInfo.getSurname2() : ""));
		dadesUsuari.setNif(userInfo.getAdministrationID());
		dadesUsuari.setEmail(userInfo.getEmail());
//		dadesUsuari.setActiu(true);

		if (ambRols) {
			dadesUsuari.setRols(findRolsAmbCodi(userInfo.getUsername()));
		}
		return dadesUsuari;
	}

	private DadesUsuari firstDadesUsuari(SearchUsersResult result, boolean ambRols) throws Exception {

		if (result == null || result.getUsers() == null || result.getUsers().isEmpty()) {
			return null;
		}
		return toDadesUsuari(result.getUsers().get(0), ambRols);
	}

	private List<DadesUsuari> toDadesUsuariList(SearchUsersResult result) throws Exception {

		List<DadesUsuari> dadesUsuaris = new ArrayList<>();
		if (result == null || result.getUsers() == null) {
			return dadesUsuaris;
		}
		for (UserInfo userInfo : result.getUsers()) {
			DadesUsuari dadesUsuari = toDadesUsuari(userInfo);
			if (dadesUsuari != null) {
				dadesUsuaris.add(dadesUsuari);
			}
		}
		return dadesUsuaris;
	}

	private String toContainsLdapFilterValue(String text) {
		return "*" + escapeLdapFilterValue(text) + "*";
	}

	private String escapeLdapFilterValue(String text) {

		if (text == null) {
			return "";
		}
		StringBuilder escaped = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			switch (c) {
				case '\\':
					escaped.append("\\5c");
					break;
				case '*':
					escaped.append("\\2a");
					break;
				case '(':
					escaped.append("\\28");
					break;
				case ')':
					escaped.append("\\29");
					break;
				case '\0':
					escaped.append("\\00");
					break;
				default:
					escaped.append(c);
			}
		}
		return escaped.toString();
	}

	private static final Logger logger = LoggerFactory.getLogger(DadesUsuariPluginLdapCaib.class);
}
