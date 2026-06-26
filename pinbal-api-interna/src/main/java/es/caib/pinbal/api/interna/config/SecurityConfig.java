package es.caib.pinbal.api.interna.config;

import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.keycloak.JwtClaimsHelper;
import es.caib.pinbal.logic.intf.keycloak.KeycloakUserDetails;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Value("${es.caib.pinbal.security.mappableRoles:" +
            BaseConfig.ROLE_ADMIN + "," +
            BaseConfig.ROLE_REPRES + "," +
            BaseConfig.ROLE_WS + "," +
            BaseConfig.ROLE_REPORT + "," +
            BaseConfig.ROLE_COM + "," +
            BaseConfig.ROLE_USER + "}")
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
            "/salut/v1",
            "/recobriment/test"
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
                .addFilterBefore(
                        preAuthenticatedProcessingFilter(),
                        BasicAuthenticationFilter.class)
                .authenticationProvider(preauthAuthProvider())
                .logout(lo -> lo.addLogoutHandler(getLogoutHandler())
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .invalidateHttpSession(true).logoutSuccessUrl("/")
                        .permitAll(false))
                .authorizeRequests(authz -> authz.antMatchers(AUTH_WHITELIST)
                        .permitAll()
                        .antMatchers(WS_RECOBRIMENT_PATHS).hasAnyAuthority("PBL_WS")
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
                if (token.getDetails() instanceof JwtAuthenticationDetails) {
                    Map<String, Object> claims = ((JwtAuthenticationDetails) token.getDetails()).getClaims();
                    return new JwtUserDetails(token.getName(), "N/A", true, true, true, true,
                            authorities,
                            JwtClaimsHelper.getStringClaim(claims, "given_name"),
                            JwtClaimsHelper.getStringClaim(claims, "family_name"),
                            JwtClaimsHelper.getStringClaim(claims, "name"),
                            JwtClaimsHelper.getStringClaim(claims, "email"));
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
                Map<String, Object> claims = JwtClaimsHelper.parseBearerToken(context.getHeader("Authorization"));
                if (claims != null) {
                    Set<String> roles = new HashSet<>(j2eeUserRoles);
                    List<String> realmRoles = JwtClaimsHelper.getRealmRoles(claims);
                    if (!realmRoles.isEmpty()) {
                        logger.debug("JWT token realm roles: " + realmRoles);
                        roles.addAll(realmRoles);
                    }
                    if (useResourceRoleMappings) {
                        String issuedFor = JwtClaimsHelper.getStringClaim(claims, "azp");
                        if (issuedFor != null) {
                            List<String> resourceRoles = JwtClaimsHelper.getResourceRoles(claims, issuedFor);
                            if (!resourceRoles.isEmpty()) {
                                logger.debug("JWT token resource roles: " + resourceRoles);
                                roles.addAll(resourceRoles);
                            }
                        }
                    }
                    logger.debug("Creating WebAuthenticationDetails for " + context.getUserPrincipal().getName() + " with roles " + roles);
                    return new JwtAuthenticationDetails(context, j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(roles), claims);
                } else {
                    logger.debug("Creating WebAuthenticationDetails for " + context.getUserPrincipal().getName() + " with roles " + j2eeUserRoles);
                    return new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(context, j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(j2eeUserRoles));
                }
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
    public static class JwtAuthenticationDetails extends PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails {
        private final Map<String, Object> claims;
        public JwtAuthenticationDetails(HttpServletRequest request, Collection<? extends GrantedAuthority> authorities, Map<String, Object> claims) {
            super(request, authorities);
            this.claims = claims;
        }
        public Map<String, Object> getClaims() {
            return claims;
        }
    }

    @SuppressWarnings("serial")
    public static class JwtUserDetails extends User implements KeycloakUserDetails {
        private final String givenName;
        private final String familyName;
        private final String fullName;
        private final String email;
        public JwtUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
                              boolean credentialsNonExpired, boolean accountNonLocked,
                              Collection<? extends GrantedAuthority> authorities,
                              String givenName, String familyName, String fullName, String email) {
            super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
            this.givenName = givenName;
            this.familyName = familyName;
            this.fullName = fullName;
            this.email = email;
        }
        public String getGivenName() { return givenName; }
        public String getFamilyName() { return familyName; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public Object getKeycloakPrincipal() { return null; }
    }

}
