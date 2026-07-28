package es.caib.pinbal.logic.intf.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtClaimsHelperTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private String bearerToken(Map<String, Object> payload) throws Exception {
        String payloadJson = mapper.writeValueAsString(payload);
        String payloadB64 = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        return "Bearer capçalera." + payloadB64 + ".signatura";
    }

    private Map<String, Object> payloadComplet() {
        Map<String, Object> realmAccess = new LinkedHashMap<>();
        realmAccess.put("roles", List.of("realm-role-1", "realm-role-2"));

        Map<String, Object> clientAccess = new LinkedHashMap<>();
        clientAccess.put("roles", List.of("client-role-1"));
        Map<String, Object> resourceAccess = new LinkedHashMap<>();
        resourceAccess.put("client1", clientAccess);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", "usuari1");
        payload.put("exp", 1234567890);
        payload.put("realm_access", realmAccess);
        payload.put("resource_access", resourceAccess);
        return payload;
    }

    // ------------------------- parseBearerToken -------------------------

    @Test
    void parseBearerTokenAmbCapceleraNullRetornaNull() {
        assertThat(JwtClaimsHelper.parseBearerToken(null)).isNull();
    }

    @Test
    void parseBearerTokenSenseBearerRetornaNull() {
        assertThat(JwtClaimsHelper.parseBearerToken("Basic abc123")).isNull();
    }

    @Test
    void parseBearerTokenAmbTokenSensePartsRetornaNull() {
        assertThat(JwtClaimsHelper.parseBearerToken("Bearer nomesunapart")).isNull();
    }

    @Test
    void parseBearerTokenAmbPayloadInvalidRetornaNull() {
        assertThat(JwtClaimsHelper.parseBearerToken("Bearer capçalera.###no-es-base64###.signatura")).isNull();
    }

    @Test
    void parseBearerTokenAmbTokenValidExtreuElsClaims() throws Exception {
        String token = bearerToken(payloadComplet());

        Map<String, Object> claims = JwtClaimsHelper.parseBearerToken(token);

        assertThat(claims).isNotNull();
        assertThat(claims.get("sub")).isEqualTo("usuari1");
    }

    @Test
    void parseBearerTokenAmbPayloadsDeLongitudsDiferentsExerceixElReompliment() throws Exception {
        // Provoca diferents longituds de base64url (sense padding) per exercitar padBase64
        // amb els diferents residus mod 4 (0, 2, 3 són els habituals).
        for (int i = 0; i < 4; i++) {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("v", "x".repeat(i));
            String token = bearerToken(payload);
            Map<String, Object> claims = JwtClaimsHelper.parseBearerToken(token);
            assertThat(claims).as("longitud extra %s", i).isNotNull();
        }
    }

    // ------------------------- getStringClaim -------------------------

    @Test
    void getStringClaimAmbClaimsNullRetornaNull() {
        assertThat(JwtClaimsHelper.getStringClaim(null, "sub")).isNull();
    }

    @Test
    void getStringClaimAmbClauInexistentRetornaNull() {
        assertThat(JwtClaimsHelper.getStringClaim(Map.of("altre", "valor"), "sub")).isNull();
    }

    @Test
    void getStringClaimAmbValorNoStringElConverteix() {
        assertThat(JwtClaimsHelper.getStringClaim(Map.of("exp", 12345), "exp")).isEqualTo("12345");
    }

    @Test
    void getStringClaimAmbValorStringElRetorna() {
        assertThat(JwtClaimsHelper.getStringClaim(Map.of("sub", "usuari1"), "sub")).isEqualTo("usuari1");
    }

    // ------------------------- getRealmRoles -------------------------

    @Test
    void getRealmRolesAmbClaimsNullRetornaListaBuida() {
        assertThat(JwtClaimsHelper.getRealmRoles(null)).isEmpty();
    }

    @Test
    void getRealmRolesSenseRealmAccessRetornaListaBuida() {
        assertThat(JwtClaimsHelper.getRealmRoles(Map.of("sub", "usuari1"))).isEmpty();
    }

    @Test
    void getRealmRolesAmbRealmAccessSenseRolesRetornaListaBuida() {
        Map<String, Object> claims = Map.of("realm_access", Map.of());
        assertThat(JwtClaimsHelper.getRealmRoles(claims)).isEmpty();
    }

    @Test
    void getRealmRolesAmbRolsElsRetorna() {
        Map<String, Object> claims = Map.of("realm_access", Map.of("roles", List.of("role1", "role2")));
        assertThat(JwtClaimsHelper.getRealmRoles(claims)).containsExactly("role1", "role2");
    }

    // ------------------------- getResourceRoles -------------------------

    @Test
    void getResourceRolesAmbClaimsONomDeClientNullRetornaListaBuida() {
        assertThat(JwtClaimsHelper.getResourceRoles(null, "client1")).isEmpty();
        assertThat(JwtClaimsHelper.getResourceRoles(Map.of("sub", "u"), null)).isEmpty();
    }

    @Test
    void getResourceRolesSenseResourceAccessRetornaListaBuida() {
        assertThat(JwtClaimsHelper.getResourceRoles(Map.of("sub", "u"), "client1")).isEmpty();
    }

    @Test
    void getResourceRolesAmbClientDesconegutRetornaListaBuida() {
        Map<String, Object> claims = Map.of("resource_access", Map.of("altre-client", Map.of("roles", List.of("r1"))));
        assertThat(JwtClaimsHelper.getResourceRoles(claims, "client1")).isEmpty();
    }

    @Test
    void getResourceRolesAmbClientSenseRolesRetornaListaBuida() {
        Map<String, Object> claims = Map.of("resource_access", Map.of("client1", Map.of()));
        assertThat(JwtClaimsHelper.getResourceRoles(claims, "client1")).isEmpty();
    }

    @Test
    void getResourceRolesAmbRolsElsRetorna() {
        Map<String, Object> claims = Map.of("resource_access", Map.of("client1", Map.of("roles", List.of("r1", "r2"))));
        assertThat(JwtClaimsHelper.getResourceRoles(claims, "client1")).containsExactly("r1", "r2");
    }
}
