package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.base.controller.BaseUtilsController;
import es.caib.pinbal.back.config.WebSecurityConfig;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.base.config.PropertyConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ReactController extends BaseUtilsController {

	private final ServletContext servletContext;

	@RequestMapping({BaseConfig.REACT_APP_PATH, BaseConfig.REACT_APP_PATH + "/**"})
	public ResponseEntity<?> serveReact(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getRequestURI().replaceFirst(request.getContextPath(), "");
		try {
			InputStream resource = servletContext.getResourceAsStream(path);
			if (resource != null) {
				String mimeType = servletContext.getMimeType(path);
				MediaType mediaType = mimeType != null ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM;
				return ResponseEntity.ok().contentType(mediaType).body(new InputStreamResource(resource));
			}
			String uri = request.getRequestURI();
			if (uri.matches(".*\\.(js|css|ico|png|jpg|svg|woff2?|map)$") || uri.endsWith("index.html")) {
				return ResponseEntity.notFound().build();
			}
			InputStream indexHtml = servletContext.getResourceAsStream(BaseConfig.REACT_APP_PATH + "/index.html");
			return ResponseEntity
					.ok()
					.contentType(MediaType.TEXT_HTML)
					.body(new InputStreamResource(Objects.requireNonNull(indexHtml)));
		} catch (Exception ex) {
			log.error("Error carregant recurs de reactapp", ex);
			return ResponseEntity.internalServerError().body("Error carregant recurs");
		}
	}

	@Override
	protected String getAuthToken() {
		// Cas 1: Spring Security pre-auth amb Bearer header (crides API)
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof PreAuthenticatedAuthenticationToken) {
			PreAuthenticatedAuthenticationToken token = (PreAuthenticatedAuthenticationToken) authentication;
			if (token.getDetails() instanceof WebSecurityConfig.PreauthWebAuthenticationDetails) {
				String jwt = ((WebSecurityConfig.PreauthWebAuthenticationDetails) token.getDetails()).getJwtToken();
				if (jwt != null) {
					return jwt;
				}
			}
		}
		// Cas 2: Petició de navegador a JBoss — el JWT és a l'atribut KeycloakSecurityContext
		// que el subsistema JBoss/Keycloak afegeix a cada request (sense dependència de compilació)
		try {
			ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (attrs != null) {
				Object keycloakCtx = attrs.getRequest().getAttribute("org.keycloak.KeycloakSecurityContext");
				if (keycloakCtx != null) {
					return (String) keycloakCtx.getClass().getMethod("getTokenString").invoke(keycloakCtx);
				}
			}
		} catch (Exception e) {
			log.debug("No s'ha pogut obtenir el token de Keycloak per reflexió", e);
		}
		return null;
	}

	@Override
	protected String[] getAuthRoles() {
		return new String[] {
				BaseConfig.ROLE_ADMIN,
				BaseConfig.ROLE_REPRES,
				BaseConfig.ROLE_WS,
				BaseConfig.ROLE_REPORT,
				BaseConfig.ROLE_COM,
				BaseConfig.ROLE_AUDIT,
				BaseConfig.ROLE_SUPERAUDIT,
				BaseConfig.ROLE_USER,
		};
	}

	@Override
	protected boolean isReactAppMappedFrontProperty(String propertyName) {
		return propertyName.startsWith(PropertyConfig.PROPERTY_PREFIX_FRONT)
				&& PropertyConfig.REACT_APP_PROPS_MAP.containsKey(propertyName);
	}

	@Override
	protected String getReactAppMappedFrontProperty(String propertyName) {
		return PropertyConfig.REACT_APP_PROPS_MAP.get(propertyName);
	}

	@Override
	protected boolean isViteMappedFrontProperty(String propertyName) {
		return propertyName.startsWith(PropertyConfig.PROPERTY_PREFIX_FRONT)
				&& PropertyConfig.VITE_PROPS_MAP.containsKey(propertyName);
	}

	@Override
	protected String getViteMappedFrontProperty(String propertyName) {
		return PropertyConfig.VITE_PROPS_MAP.get(propertyName);
	}

}
