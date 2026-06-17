package es.caib.pinbal.logic.intf.keycloak;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Utilitat per a extreure claims d'un JWT Bearer token sense dependre de cap
 * llibreria específica de Keycloak. La validació del token ja ha estat feta
 * pel contenidor JBoss EAP.
 */
@Slf4j
public class JwtClaimsHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {};

    /**
     * Parseja el payload del token JWT a partir de la capçalera Authorization Bearer.
     * No valida la signatura (ja ho fa JBoss EAP).
     */
    public static Map<String, Object> parseBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        try {
            String token = authorizationHeader.substring(7);
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return null;
            }
            byte[] decoded = Base64.getUrlDecoder().decode(padBase64(parts[1]));
            return MAPPER.readValue(decoded, MAP_TYPE);
        } catch (Exception e) {
            log.warn("No s'ha pogut parsejar el token JWT Bearer", e);
            return null;
        }
    }

    public static String getStringClaim(Map<String, Object> claims, String key) {
        if (claims == null) return null;
        Object value = claims.get(key);
        return value != null ? String.valueOf(value) : null;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getRealmRoles(Map<String, Object> claims) {
        if (claims == null) return Collections.emptyList();
        Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
        if (realmAccess == null) return Collections.emptyList();
        List<String> roles = (List<String>) realmAccess.get("roles");
        return roles != null ? roles : Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public static List<String> getResourceRoles(Map<String, Object> claims, String clientId) {
        if (claims == null || clientId == null) return Collections.emptyList();
        Map<String, Object> resourceAccess = (Map<String, Object>) claims.get("resource_access");
        if (resourceAccess == null) return Collections.emptyList();
        Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(clientId);
        if (clientAccess == null) return Collections.emptyList();
        List<String> roles = (List<String>) clientAccess.get("roles");
        return roles != null ? roles : Collections.emptyList();
    }

    private static String padBase64(String base64) {
        int mod4 = base64.length() % 4;
        return mod4 == 0 ? base64 : base64 + "====".substring(mod4);
    }

}
