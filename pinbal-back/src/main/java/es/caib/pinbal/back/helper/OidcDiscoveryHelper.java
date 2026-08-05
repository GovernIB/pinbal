package es.caib.pinbal.back.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resol i cache els metadades del document de descobriment OIDC ("/.well-known/openid-configuration") d'un
 * issuer. Necessari perquè, tot i que l'adaptador Keycloak de JBoss es configura sempre amb l'esquema d'URL
 * propi de Keycloak (auth-server-url + "/realms/" + realm + ...), als entorns de producció l'IdP real al
 * darrere d'aquest adaptador no sempre és un Keycloak: Soffid s'hi posa al davant emulant el protocol
 * (login/token funcionen igual), però no es pot donar per fet que el seu "end_session_endpoint" visqui
 * exactament a "/protocol/openid-connect/logout" ni que accepti els mateixos paràmetres -- per això cal
 * llegir-lo del document de descobriment en lloc d'assumir el path de Keycloak.
 */
@Slf4j
public class OidcDiscoveryHelper {

	private static final Duration CACHE_TTL = Duration.ofHours(6);
	private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(5);
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(HTTP_TIMEOUT).build();
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final ConcurrentHashMap<String, CachedValue> CACHE = new ConcurrentHashMap<>();

	private OidcDiscoveryHelper() {
	}

	/**
	 * @param issuerUrl arrel de l'issuer OIDC (p.ex. "https://host/auth/realms/pinbal"), sense
	 *                  "/.well-known/openid-configuration" al final.
	 * @return l'"end_session_endpoint" publicat, o null si el document de descobriment no és accessible i no
	 * hi ha cap valor cachejat anteriorment que es pugui reaprofitar.
	 */
	public static String getEndSessionEndpoint(String issuerUrl) {
		CachedValue cached = CACHE.get(issuerUrl);
		if (cached != null && cached.expiresAt.isAfter(Instant.now())) {
			return cached.endSessionEndpoint;
		}
		String discoveryUrl = (issuerUrl.endsWith("/") ? issuerUrl.substring(0, issuerUrl.length() - 1) : issuerUrl) +
				"/.well-known/openid-configuration";
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(discoveryUrl))
					.timeout(HTTP_TIMEOUT)
					.GET()
					.build();
			HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				JsonNode endSessionNode = OBJECT_MAPPER.readTree(response.body()).get("end_session_endpoint");
				String endSessionEndpoint = endSessionNode != null ? endSessionNode.asText() : null;
				CACHE.put(issuerUrl, new CachedValue(endSessionEndpoint, Instant.now().plus(CACHE_TTL)));
				return endSessionEndpoint;
			}
			log.warn("No s'ha pogut obtenir el document de descobriment OIDC de {} (HTTP {})", discoveryUrl, response.statusCode());
		} catch (Exception e) {
			log.warn("Error obtenint el document de descobriment OIDC de {}: {}", discoveryUrl, e.getMessage());
		}
		// Si la descoberta falla momentàniament (p.ex. l'IdP no respon a temps) es reaprofita l'últim valor
		// conegut en lloc de forçar sempre el fallback a l'esquema de Keycloak.
		return cached != null ? cached.endSessionEndpoint : null;
	}

	private static class CachedValue {
		private final String endSessionEndpoint;
		private final Instant expiresAt;

		private CachedValue(String endSessionEndpoint, Instant expiresAt) {
			this.endSessionEndpoint = endSessionEndpoint;
			this.expiresAt = expiresAt;
		}
	}

}
