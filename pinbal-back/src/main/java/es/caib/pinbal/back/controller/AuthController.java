package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.config.WebSecurityConfig;
import es.caib.pinbal.back.helper.OidcDiscoveryHelper;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
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
@Slf4j
@Controller
public class AuthController {

	@Value("${jboss.home.dir:#{null}}")
	private String jbossHomeDir;
	@Value("${es.caib.pinbal.auth.url:#{null}}")
	private String authUrl;
	@Value("${es.caib.pinbal.auth.realm:#{null}}")
	private String authRealm;
	@Value("${es.caib.pinbal.auth.clientid:#{null}}")
	private String authClientId;

	private static final String ORIGEN_REACT = "react";

	@GetMapping("/usuari/logout")
	public String logout(
			HttpServletRequest request,
			@RequestParam(value = "origen", required = false) String origen) {
		// Destí on ha d'aterrar el navegador un cop Keycloak acaba el logout: l'arrel de la interfície
		// JSP (comportament per defecte) o l'arrel de la SPA React si el logout s'ha iniciat des d'allà
		// (identificat pel paràmetre "origen", que ContainerAuthProvider.tsx afegeix a signOutUrl). Així
		// el següent login torna a la mateixa interfície des d'on s'ha fet el "Desconnectar".
		String postLogoutPath = ORIGEN_REACT.equals(origen) ? BaseConfig.REACT_APP_PATH + "/" : "/";
		if (jbossHomeDir == null) {
			// Spring Boot: delega en el logout de Spring Security (WebSecurityConfig.LOGOUT_URL), que ja fa
			// el flux OIDC complet (end_session_endpoint de Keycloak) i neteja les cookies corresponents.
			return "redirect:" + WebSecurityConfig.LOGOUT_URL;
		}
		// JBoss: request.logout() tanca la sessió local a l'adaptador servlet de Keycloak, però NO tanca la
		// sessió SSO al propi Keycloak. Sense el redirect explícit de sota a l'"end session endpoint", la
		// següent visita hi torna a entrar silenciosament amb l'usuari anterior (sessió SSO encara viva) en
		// lloc de mostrar el formulari de login, i a més l'"state" OAuth de la petició anterior ja no és
		// vàlid per aquest nou intent, provocant un "Bad Request" a Keycloak.
		// Cal obtenir l'id_token ABANS de request.logout() (que invalida el KeycloakSecurityContext), perquè
		// Keycloak >= 18 exigeix "id_token_hint" per fer el logout sense demanar confirmació a l'usuari.
		String idTokenHint = null;
		Object keycloakSecurityContext = request.getAttribute(KeycloakSecurityContext.class.getName());
		if (keycloakSecurityContext instanceof KeycloakSecurityContext) {
			idTokenHint = ((KeycloakSecurityContext) keycloakSecurityContext).getIdTokenString();
		}
		try {
			request.logout();
		} catch (ServletException e) {
			log.warn("Error fent logout", e);
		}
		// NO facis un bucle genèric que reenviï totes les cookies de la petició amb valor buit:
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
				// Ja invalidada (p.ex. per request.logout()); res a fer.
			}
		}
		if (authUrl != null && authRealm != null) {
			String baseUrl = request.getScheme() + "://" + request.getServerName() +
					((request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : ":" + request.getServerPort()) +
					request.getContextPath() + postLogoutPath;
			String authUrlSensePrefix = authUrl.endsWith("/") ? authUrl.substring(0, authUrl.length() - 1) : authUrl;
			String issuerUrl = authUrlSensePrefix + "/realms/" + authRealm;
			// No es pot assumir que l'"end session endpoint" viu sempre a "/protocol/openid-connect/logout":
			// és el path de Keycloak, però als entorns de producció l'IdP darrere de l'adaptador pot ser
			// Soffid (que emula el protocol de Keycloak per a login/token, però no necessàriament exposa el
			// logout al mateix path). Es llegeix del document de descobriment OIDC i només es cau al path de
			// Keycloak com a fallback si la descoberta no és accessible.
			String endSessionEndpoint = OidcDiscoveryHelper.getEndSessionEndpoint(issuerUrl);
			if (endSessionEndpoint == null) {
				endSessionEndpoint = issuerUrl + "/protocol/openid-connect/logout";
			}
			StringBuilder logoutUrl = new StringBuilder(endSessionEndpoint)
					.append("?post_logout_redirect_uri=").append(URLEncoder.encode(baseUrl, StandardCharsets.UTF_8));
			// S'envien tots dos paràmetres (no és excloent): alguns IdP OIDC exigeixen "client_id" encara que
			// hi hagi "id_token_hint", i l'especificació RP-Initiated Logout permet enviar-los junts.
			if (idTokenHint != null) {
				logoutUrl.append("&id_token_hint=").append(URLEncoder.encode(idTokenHint, StandardCharsets.UTF_8));
			}
			if (authClientId != null) {
				logoutUrl.append("&client_id=").append(URLEncoder.encode(authClientId, StandardCharsets.UTF_8));
			}
			return "redirect:" + logoutUrl;
		}
		return "redirect:" + postLogoutPath;
	}

}
