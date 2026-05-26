/**
 * 
 */
package es.caib.pinbal.api.interna.config;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAttributes2GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleMappableAttributesRetriever;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.j2ee.J2eePreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuració de seguretat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Value("${es.caib.pinbal.security.mappableRoles:PBL_WS,PBL_REPORT,PBL_REPRES,PBL_ADMIN,PBL_COM}")
	private String mappableRoles;
	@Value("${es.caib.pinbal.security.useResourceRoleMappings:false}")
	private boolean useResourceRoleMappings;

	private static final String ROLE_PREFIX = "";

	private static final String[] AUTH_WHITELIST = {
			"/webjars/**",
			"/rest",
			"/api/rest",
			"/swagger-resources/**",
			"/swagger-ui/**",
			"/api-docs",
			"/api-docs/**",
			"/**/api-docs",
			"/salut/v1"
	};

	private static final String[] WS_RECOBRIMENT_PATHS = {
			"/ws/recobriment",
			"/ws/recobriment/**",
			"/pinbal/ws/recobriment",
			"/pinbal/ws/recobriment/**"
	};

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.cors(withDefaults())
				.csrf(AbstractHttpConfigurer::disable)
//				.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
				.addFilterBefore(preAuthenticatedProcessingFilter(), BasicAuthenticationFilter.class)
				.authenticationProvider(preauthAuthProvider())
				.logout(lo -> lo.addLogoutHandler(getLogoutHandler())
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.invalidateHttpSession(true).logoutSuccessUrl("/")
						.permitAll(false))
				.authorizeRequests(authz -> authz.antMatchers(AUTH_WHITELIST)
						.permitAll()
						.antMatchers(WS_RECOBRIMENT_PATHS).hasAnyAuthority("PBL_WS", "ROLE_WS", "ROLE_PBL_WS")
						.anyRequest().authenticated())
				.headers(hd -> hd.frameOptions().disable())
				.build();
	}

	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		final List<AuthenticationProvider> providers = new ArrayList<>(1);
		providers.add(preauthAuthProvider());
		return new ProviderManager(providers);
	}

	@Bean
	public PreAuthenticatedAuthenticationProvider preauthAuthProvider() {
		var preauthAuthProvider = new PreAuthenticatedAuthenticationProvider();
		preauthAuthProvider.setPreAuthenticatedUserDetailsService(preAuthenticatedGrantedAuthoritiesUserDetailsService());
		return preauthAuthProvider;
	}

	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults(ROLE_PREFIX);
	}

	@Bean
	public PreAuthenticatedGrantedAuthoritiesUserDetailsService preAuthenticatedGrantedAuthoritiesUserDetailsService() {
		return new PreAuthenticatedGrantedAuthoritiesUserDetailsService() {
			protected UserDetails createUserDetails(Authentication token, Collection<? extends GrantedAuthority> authorities) {
				if (token.getDetails() instanceof KeycloakWebAuthenticationDetails) {
					KeycloakWebAuthenticationDetails keycloakWebAuthenticationDetails = (KeycloakWebAuthenticationDetails)token.getDetails();
					return new PreauthKeycloakUserDetails(token.getName(), "N/A", true, true, true, true,
							authorities, keycloakWebAuthenticationDetails.getKeycloakPrincipal());
				}
				return new User(token.getName(), "N/A", true, true, true, true, authorities);
			}
		};
	}

	@Bean
	public J2eePreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter() throws Exception {
		var preAuthenticatedProcessingFilter = new J2eePreAuthenticatedProcessingFilter();
		preAuthenticatedProcessingFilter.setAuthenticationDetailsSource(authenticationDetailsSource());
		preAuthenticatedProcessingFilter.setAuthenticationManager(authenticationManager());
		preAuthenticatedProcessingFilter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
		return preAuthenticatedProcessingFilter;
	}

	@Bean
	public AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> authenticationDetailsSource() {
		var authenticationDetailsSource = new J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource() {
			@Override
			public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails buildDetails(HttpServletRequest context) {
				var j2eeUserRoles = getUserRoles(context);
				if (!j2eeUserRoles.contains("tothom")) {
					j2eeUserRoles.add("tothom");
				}
				logger.debug("Roles from ServletRequest for " + context.getUserPrincipal().getName() + ": " + j2eeUserRoles);
				PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails result;
				if (context.getUserPrincipal() instanceof KeycloakPrincipal) {
					var keycloakPrincipal = ((KeycloakPrincipal<?>)context.getUserPrincipal());
					Set<String> roles = new HashSet<>();
					roles.addAll(j2eeUserRoles);
					var realmAccess = keycloakPrincipal.getKeycloakSecurityContext().getToken().getRealmAccess();
					if (realmAccess != null && realmAccess.getRoles() != null) {
						logger.debug("Keycloak token realm roles: " + realmAccess.getRoles());
						roles.addAll(realmAccess.getRoles());
					}
					if (useResourceRoleMappings) {
						var resourceAccess = keycloakPrincipal.getKeycloakSecurityContext().getToken().getResourceAccess(
								keycloakPrincipal.getKeycloakSecurityContext().getToken().getIssuedFor());
						if (resourceAccess != null && resourceAccess.getRoles() != null) {
							logger.debug("Keycloak token resource roles: " + resourceAccess.getRoles());
							roles.addAll(resourceAccess.getRoles());
						}
					}
					logger.debug("Creating WebAuthenticationDetails for " + keycloakPrincipal.getName() + " with roles " + roles);
					result = new KeycloakWebAuthenticationDetails(context, j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(roles), keycloakPrincipal);
				} else {
					logger.debug("Creating WebAuthenticationDetails for " + context.getUserPrincipal().getName() + " with roles " + j2eeUserRoles);
					result = new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(context, j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(j2eeUserRoles));
				}
				return result;
			}
		};
		var mappableAttributesRetriever = new SimpleMappableAttributesRetriever();
		mappableAttributesRetriever.setMappableAttributes(new HashSet<>(Arrays.asList(mappableRoles.split(","))));
		authenticationDetailsSource.setMappableRolesRetriever(mappableAttributesRetriever);
		var attributes2GrantedAuthoritiesMapper = new SimpleAttributes2GrantedAuthoritiesMapper();
		attributes2GrantedAuthoritiesMapper.setAttributePrefix(ROLE_PREFIX);
		authenticationDetailsSource.setUserRoles2GrantedAuthoritiesMapper(attributes2GrantedAuthoritiesMapper);
		return authenticationDetailsSource;
	}

	@Bean
	public LogoutHandler getLogoutHandler() {
		return (request, response, authentication) -> {
			try {
				request.logout();
			} catch (ServletException ex) {
				log.error("Error al sortir de l'aplicació", ex);
			}
		};
	}

	@SuppressWarnings("serial")
	public static class KeycloakWebAuthenticationDetails extends PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails {
		private KeycloakPrincipal<?> keycloakPrincipal;
		public KeycloakWebAuthenticationDetails(HttpServletRequest request, Collection<? extends GrantedAuthority> authorities, KeycloakPrincipal<?> keycloakPrincipal) {

			super(request, authorities);
			this.keycloakPrincipal = keycloakPrincipal;
		}
		public KeycloakPrincipal<?> getKeycloakPrincipal() {
			return keycloakPrincipal;
		}
	}

	@SuppressWarnings("serial")
	public static class PreauthKeycloakUserDetails extends User implements es.caib.pinbal.logic.intf.keycloak.KeycloakUserDetails {
		private KeycloakPrincipal<?> keycloakPrincipal;
		public PreauthKeycloakUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
											boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, KeycloakPrincipal<?> keycloakPrincipal) {

			super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
			this.keycloakPrincipal = keycloakPrincipal;
		}
		public KeycloakPrincipal<?> getKeycloakPrincipal() {
			return keycloakPrincipal;
		}

		public String getGivenName() {
			return keycloakPrincipal instanceof KeycloakPrincipal ? keycloakPrincipal.getKeycloakSecurityContext().getToken().getGivenName() : null;
		}

		public String getFamilyName() {
			return keycloakPrincipal instanceof KeycloakPrincipal ? keycloakPrincipal.getKeycloakSecurityContext().getToken().getFamilyName() : null;
		}

		public String getFullName() {
			return keycloakPrincipal instanceof KeycloakPrincipal ? keycloakPrincipal.getKeycloakSecurityContext().getToken().getName() : null;
		}

		public String getEmail() {
			return keycloakPrincipal instanceof KeycloakPrincipal ? keycloakPrincipal.getKeycloakSecurityContext().getToken().getEmail() : null;
		}
	}

}
