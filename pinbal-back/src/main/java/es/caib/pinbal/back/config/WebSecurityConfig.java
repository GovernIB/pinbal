package es.caib.pinbal.back.config;

import es.caib.pinbal.back.base.config.BaseWebSecurityConfig;
import es.caib.pinbal.back.base.config.MethodSecurityConfig;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.base.util.HttpRequestUtil;
import es.caib.pinbal.logic.intf.keycloak.JwtClaimsHelper;
import es.caib.pinbal.logic.intf.model.auth.PinbalAuthenticationDetails;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAttributes2GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleMappableAttributesRetriever;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Configuració de Spring Security per a executar l'aplicació amb Spring Boot.
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Configuration
public class WebSecurityConfig extends BaseWebSecurityConfig {

	public static final String LOGOUT_URL = "/logout";

	@Value("${es.caib.pinbal.security.mappableRoles:" +
			BaseConfig.ROLE_ADMIN + "," +
			BaseConfig.ROLE_REPRES + "," +
			BaseConfig.ROLE_WS + "," +
			BaseConfig.ROLE_REPORT + "," +
			BaseConfig.ROLE_COM + "," +
			BaseConfig.ROLE_AUDIT + "," +
			BaseConfig.ROLE_SUPERAUDIT + "," +
			BaseConfig.ROLE_USER + "}")
	protected String mappableRoles;
	@Value("${" + BaseConfig.PROP_SECURITY_ROLE_HTTP_HEADER + ":X-App-Role}")
	private String selectedRoleHttpHeader;
	@Value("${" + BaseConfig.PROP_SECURITY_NAME_ATTRIBUTE_KEY + ":preferred_username}")
	private String nameAttributeKey;

	@Autowired(required = false)
	private ClientRegistrationRepository clientRegistrationRepository;

	@Override
	protected void customHttpSecurityConfiguration(HttpSecurity http) throws Exception {
		if (!isJboss()) {
			LogoutHandler deleteCookiesLogoutHandler = (request, response, authentication) -> {
				try {
					log.info("Logout called");
					Cookie[] cookies = request.getCookies();
					if (cookies != null) {
						for (Cookie cookie : cookies) {
							Cookie deletedCookie = new Cookie(cookie.getName(), "");
							deletedCookie.setPath(cookie.getPath() != null ? cookie.getPath() : "/");
							deletedCookie.setMaxAge(0);
							deletedCookie.setHttpOnly(cookie.isHttpOnly());
							deletedCookie.setSecure(cookie.getSecure());
							response.addCookie(deletedCookie);
						}
					}
					request.logout();
				} catch (ServletException ex) {
					log.error("Error en el logout", ex);
				}
			};
			OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(
				clientRegistrationRepository);
			oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");
			http.logout(lo -> lo.
				addLogoutHandler(deleteCookiesLogoutHandler).
				logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URL)).
				invalidateHttpSession(true).
				clearAuthentication(true).
				deleteCookies("OAuth_Token_Request_State", "JSESSIONID").
				logoutSuccessHandler(oidcLogoutSuccessHandler).
				logoutSuccessUrl("/"));
		}
		http.authorizeHttpRequests().
				requestMatchers(publicRequestMatchers()).permitAll();
		if (!isJboss()) {
			http.sessionManagement(session -> session
							.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
							.invalidSessionUrl("/"));
		}
		http.servletApi(api -> api.rolePrefix(""));
		super.customHttpSecurityConfiguration(http);
	}

	protected RequestMatcher[] publicRequestMatchers() {
		return new RequestMatcher[] {
				new AntPathRequestMatcher("/swagger-resources/**"),
				new AntPathRequestMatcher("/swagger-ui/**"),
				new AntPathRequestMatcher("/api/rest"),
				new AntPathRequestMatcher("/api/rest/**/*"),
				new AntPathRequestMatcher("/api-docs"),
				new AntPathRequestMatcher("/api-docs/**/*"),
				new AntPathRequestMatcher("/css/**/*"),
				new AntPathRequestMatcher("/fonts/**/*"),
				new AntPathRequestMatcher("/img/**/*"),
				new AntPathRequestMatcher("/js/**/*"),
				new AntPathRequestMatcher("/webjars/**"),
		};
	}

	@Override
	protected boolean isWebContainerAuthActive() {
		return isJboss();
	}
	@Override
	protected boolean isOauth2ResourceServerActive() {
		return !isJboss();
	}
	@Override
	protected boolean isOidcClientActive() {
		return !isJboss();
	}

	@Override
	protected Set<String> getAllowedRoles() {
		Optional<HttpServletRequest> optionalRequest = HttpRequestUtil.getCurrentHttpRequest();
		Set<String> allowedRoles = Set.of(mappableRoles.split(","));
		if (optionalRequest.isPresent()) {
			// Si la petició HTTP conté la capçalera amb el rol seleccionat retorna únicament aquest rol en la llista
			// de rols permesos.
			HttpServletRequest request = optionalRequest.get();
			String selectedRole = request.getHeader(selectedRoleHttpHeader);
			if (selectedRole != null) {
				HashSet<String> editableAllowedRoles = new HashSet<>(allowedRoles);
				editableAllowedRoles.removeIf(s -> !s.equals(selectedRole));
				return editableAllowedRoles;
			}
		}
		return allowedRoles;
	}

	@Value("${jboss.home.dir:#{null}}")
	private String jbossHomeDir;
	private boolean isJboss() {
		return jbossHomeDir != null;
	}

	@Override
	protected AuthenticationDetailsSource<HttpServletRequest, ?> getPreauthFilterAuthenticationDetailsSource() {
		J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource authenticationDetailsSource = new J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource() {
			@Override
			public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails buildDetails(HttpServletRequest context) {
				Collection<String> j2eeUserRoles = getUserRoles(context);
				// El rol "tothom" s'assigna automàticament a tots els usuaris autenticats: no és un rol de
				// Keycloak (no arriba ni al JWT ni del contenidor), però és la base sobre la qual es deriven
				// rols com el de delegat (vegeu RolHelper.getRolsUsuariActual).
				if (!j2eeUserRoles.contains(BaseConfig.ROLE_USER)) {
					j2eeUserRoles.add(BaseConfig.ROLE_USER);
				}
				logger.debug("Roles from ServletRequest for " + context.getUserPrincipal().getName() + ": " + j2eeUserRoles);
				PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails result;
				Map<String, Object> claims = JwtClaimsHelper.parseBearerToken(context.getHeader("Authorization"));
				if (claims != null) {
					Set<String> roles = new HashSet<>(j2eeUserRoles);
					List<String> realmRoles = JwtClaimsHelper.getRealmRoles(claims);
					if (!realmRoles.isEmpty()) {
						logger.debug("JWT token realm roles: " + realmRoles);
						realmRoles.stream().
								map(r -> MethodSecurityConfig.DEFAULT_ROLE_PREFIX + r).
								forEach(roles::add);
					}
					Collection<? extends GrantedAuthority> grantedAuthorities = j2eeUserRoles2GrantedAuthoritiesMapper.
							getGrantedAuthorities(roles);
					filterAllowedGrantedAuthorities(new HashSet<>(grantedAuthorities));
					String preferredUsername = nameAttributeKey.equals("preferred_username") ?
							JwtClaimsHelper.getStringClaim(claims, "preferred_username") :
							JwtClaimsHelper.getStringClaim(claims, nameAttributeKey);
					result = new PreauthWebAuthenticationDetails(
							context,
							j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(roles),
							context.getHeader("Authorization") != null ?
									context.getHeader("Authorization").substring(7) : null,
							preferredUsername,
							JwtClaimsHelper.getStringClaim(claims, "name"),
							JwtClaimsHelper.getStringClaim(claims, "email"),
							JwtClaimsHelper.getStringClaim(claims, "nif"),
							roles.toArray(new String[0]));
				} else {
					Collection<? extends GrantedAuthority> grantedAuthorities = j2eeUserRoles2GrantedAuthoritiesMapper.
							getGrantedAuthorities(j2eeUserRoles);
					filterAllowedGrantedAuthorities(new HashSet<>(grantedAuthorities));
					result = new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(
							context,
							grantedAuthorities);
				}
				log.debug("Created WebAuthenticationDetails for {} with roles {}",
						context.getUserPrincipal().getName(),
						result.getGrantedAuthorities());
				return result;
			}
		};
		SimpleMappableAttributesRetriever mappableAttributesRetriever = new SimpleMappableAttributesRetriever();
		mappableAttributesRetriever.setMappableAttributes(getAllowedRoles());
		authenticationDetailsSource.setMappableRolesRetriever(mappableAttributesRetriever);
		SimpleAttributes2GrantedAuthoritiesMapper attributes2GrantedAuthoritiesMapper = new SimpleAttributes2GrantedAuthoritiesMapper();
		attributes2GrantedAuthoritiesMapper.setAttributePrefix(MethodSecurityConfig.DEFAULT_ROLE_PREFIX);
		authenticationDetailsSource.setUserRoles2GrantedAuthoritiesMapper(attributes2GrantedAuthoritiesMapper);
		return authenticationDetailsSource;
	}

	@Getter
	public static class PreauthWebAuthenticationDetails
		extends PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails
		implements PinbalAuthenticationDetails {
		private final String jwtToken;
		private final String preferredUsername;
		private final String name;
		private final String email;
		private final String nif;
		private final String[] originalRoles;
		public PreauthWebAuthenticationDetails(
				HttpServletRequest request,
				Collection<? extends GrantedAuthority> authorities,
				String jwtToken,
				String preferredUsername,
				String name,
				String email,
				String nif,
				String[] originalRoles) {
			super(request, authorities);
			this.jwtToken = jwtToken;
			this.preferredUsername = preferredUsername;
			this.name = name;
			this.email = email;
			this.nif = nif;
			this.originalRoles = originalRoles;
		}
	}

}
