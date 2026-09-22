package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.config.WebSecurityConfig;
import es.caib.pinbal.back.helper.OidcDiscoveryHelper;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controlador d'autenticació/sessió independent de la interfície (JSP o React): el tancament de
 * sessió és lògica de sessió/OIDC pura (no renderitza cap vista), de manera que viu en un
 * controlador propi en lloc de {@link UsuariController} per a no dependre d'aquest quan
 * s'eliminin la interfície JSP i els seus controladors.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class AuthController {

	@Value("${jboss.home.dir:#{null}}")
	private String jbossHomeDir;
	@Value("${es.caib.pinbal.auth.url:#{null}}")
	private String authUrl;
	@Value("${es.caib.pinbal.auth.realm:#{null}}")
	private String authRealm;

	private static final String ORIGEN_REACT = "react";
	private static final String REDIRECT = "redirect:";
	private static final String PATH_DELIMITER = "/";

	@GetMapping("/usuari/logout")
	public String logout(
			HttpServletRequest request,
			@RequestParam(value = "origen", required = false) String origen) {

		if (jbossHomeDir == null) {
			// Spring Boot: delega en el logout de Spring Security (WebSecurityConfig.LOGOUT_URL), que ja fa
			// el flux OIDC complet (end_session_endpoint de Keycloak) i neteja les cookies corresponents.
			return REDIRECT + WebSecurityConfig.LOGOUT_URL;
		}

		// Cal llegir el KeycloakSecurityContext ABANS d'invalidar la sessió, perquè Keycloak >= 18
		// exigeix "id_token_hint" per fer el logout sense demanar confirmació a l'usuari.
		KeycloakSecurityContext keycloakSecurityContext = getKeycloakSecurityContext(request);
		String idTokenHint = keycloakSecurityContext != null ? keycloakSecurityContext.getIdTokenString() : null;

		// Destí on ha d'aterrar el navegador un cop Keycloak acaba el logout: l'arrel de la interfície
		// JSP (comportament per defecte) o l'arrel de la SPA React si el logout s'ha iniciat des d'allà
		// (identificat pel paràmetre "origen", que ContainerAuthProvider.tsx afegeix a signOutUrl). Així
		// el següent login torna a la mateixa interfície des d'on s'ha fet el "Desconnectar".
		String postLogoutPath = ORIGEN_REACT.equals(origen) ? BaseConfig.REACT_APP_PATH + PATH_DELIMITER : PATH_DELIMITER;


		// NO cridis request.logout() aquí:
		// a l'adaptador Keycloak d'Undertow/WildFly, request.logout() no es limita a
		// netejar l'estat local, sinó quefa una petició backchannel REAL a Keycloak (amb el refresh_token)
		// i tanca la sessió SSO immediatament -- abans que el redirect explícit de sota hi arribi mai.
		// Com que aquesta petició mai interactua amb el navegador, les cookies de sessió SSO
		// de Keycloak (al domini de l'IdP) no s'arriben a esborrar mai per aquesta via: el redirect
		// posterior a l'"end session endpoint" amb l'"id_token_hint" ja capturat rep "Session not active"
		// (Keycloak ja no troba la sessió, l'acaba de matar request.logout()) en lloc d'executar el camí
		// d'èxit -- l'únic que sí esborraria aquestes cookies. Resultat: l'usuari veu l'error de Keycloak
		// i, en tornar a entrar a l'aplicació, hi torna a entrar silenciosament (la cookie SSO de Keycloak
		// encara és vàlida). La sessió SSO de Keycloak l'ha de tancar únicament el redirect explícit de
		// sota, executant-se contra una sessió que encara és viva.
		// NO facis tampoc un bucle genèric que reenviï totes les cookies de la petició amb valor buit:
		// request.getCookies() no exposa el path/domain amb què cada cookie es va crear
		// originalment (només ho sap el navegador), així que reenviar-les totes amb
		// path=contextPath ("/pinbalback") no esborra les que l'adaptador de Keycloak crea a
		// path "/" (p.ex. OAuth_Token_Request_State) ni les del domini de Keycloak -- en lloc
		// d'esborrar-les, crea una SEGONA cookie fantasma amb el mateix nom i valor buit a
		// "/pinbalback". El navegador envia totes dues al següent login; l'adaptador llegeix la
		// fantasma (buida) en lloc de la real que ell mateix acaba de crear a "/", i rebutja el
		// callback amb "state parameter invalid" -> "Bad Request" (vegeu e2e/BUGS_APLICACIO.md).
		// Només cal invalidar la sessió HTTP local (per forçar un JSESSIONID nou al pròxim
		// login); el tancament de la sessió SSO de Keycloak ja el fa el redirect de sota.
		HttpSession session = request.getSession(false);
		if (session != null) {
			try {
				session.invalidate();
			} catch (IllegalStateException e) {
				// Ja invalidada; res a fer.
			}
		}
		if (authUrl != null && authRealm != null) {
			String baseUrl = getBaseUrl(request, postLogoutPath);
			String issuerUrl = getIssuerUrl();

			// No es pot assumir que l'"end session endpoint" viu sempre a "/protocol/openid-connect/logout":
			// és el path de Keycloak, però als entorns de producció l'IdP darrere de l'adaptador pot ser
			// Soffid (que emula el protocol de Keycloak per a login/token, però no necessàriament exposa el
			// logout al mateix path). Es llegeix del document de descobriment OIDC i només es cau al path de
			// Keycloak com a fallback si la descoberta no és accessible.
			StringBuilder logoutUrl = getLogoutUrl(issuerUrl, baseUrl);
			// S'envien tots dos paràmetres (no és excloent): alguns IdP OIDC exigeixen "client_id" encara que
			// hi hagi "id_token_hint", i l'especificació RP-Initiated Logout permet enviar-los junts.
			//
			// El "client_id" s'obté del claim "azp" del mateix id_token (amb quin client s'ha autenticat
			// l'usuari), NO d'una propietat de configuració a part (com es feia abans amb
			// "es.caib.pinbal.auth.clientid"): aquella propietat s'havia de mantenir sincronitzada a mà amb
			// el "resource" del "pinbal-back.war" al subsistema keycloak de standalone-openshift.xml
			// (JBOSS_AUTH_CLIENTID), i es va desincronitzar en un entorn (s'hi va posar el client REST
			// "goib-ws" en lloc del client de navegador "goib-default-des"). Com que Keycloak/Soffid
			// indexen la sessió SSO pel client que la va crear, un "client_id" que no és el propietari de
			// la sessió identificada per "id_token_hint" fa que l'"end_session_endpoint" respongui "Session
			// not active": no tanca la sessió SSO i l'usuari hi torna a entrar silenciosament. Llegint-lo
			// sempre de l'"azp" del mateix token és impossible que quedi desincronitzat.
			String clientId = keycloakSecurityContext != null && keycloakSecurityContext.getIdToken() != null
					? keycloakSecurityContext.getIdToken().getIssuedFor()
					: null;
			if (idTokenHint != null) {
				logoutUrl.append("&id_token_hint=").append(URLEncoder.encode(idTokenHint, StandardCharsets.UTF_8));
			}
			if (clientId != null) {
				logoutUrl.append("&client_id=").append(URLEncoder.encode(clientId, StandardCharsets.UTF_8));
			}
			return REDIRECT + logoutUrl;
		}
		return REDIRECT + postLogoutPath;
	}

	@NotNull
	private static String getBaseUrl(HttpServletRequest request, String postLogoutPath) {
		String serverPath = getServerPath(request);
		return serverPath + request.getContextPath() + postLogoutPath;
	}

	@NotNull
	private static String getServerPath(HttpServletRequest request) {
		int port = request.getServerPort();
		String portSuffix = isDefaultHttpPort(port) ? "" : ":" + port;
		return request.getScheme() + "://" + request.getServerName() + portSuffix;
	}

	private static boolean isDefaultHttpPort(int port) {
		return port == 80 || port == 443;
	}

	@NotNull
	private String getIssuerUrl() {
		String authUrlSensePrefix = authUrl.endsWith(PATH_DELIMITER) ? authUrl.substring(0, authUrl.length() - 1) : authUrl;
		return authUrlSensePrefix + "/realms/" + authRealm;
	}

	@NotNull
	private static StringBuilder getLogoutUrl(String issuerUrl, String baseUrl) {
		String endSessionEndpoint = OidcDiscoveryHelper.getEndSessionEndpoint(issuerUrl);
		if (endSessionEndpoint == null) {
			endSessionEndpoint = issuerUrl + "/protocol/openid-connect/logout";
		}
		return new StringBuilder(endSessionEndpoint)
			.append("?post_logout_redirect_uri=")
			.append(URLEncoder.encode(baseUrl, StandardCharsets.UTF_8));
	}

	@Nullable
	private static KeycloakSecurityContext getKeycloakSecurityContext(HttpServletRequest request) {
		Object keycloakSecurityContext = request.getAttribute(KeycloakSecurityContext.class.getName());
		return keycloakSecurityContext instanceof KeycloakSecurityContext
				? (KeycloakSecurityContext) keycloakSecurityContext
				: null;
	}

}
