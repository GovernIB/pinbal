package es.caib.pinbal.logic.intf.base.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.caib.pinbal.logic.intf.base.exception.ResourceFieldNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonUtilTest {

    public static class Fixture {
        public String id;
        public String nom;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private JsonUtil jsonUtil;

    @BeforeEach
    void configurar() {
        jsonUtil = new JsonUtil();
        ReflectionTestUtils.setField(jsonUtil, "objectMapper", objectMapper);
    }

    @Test
    void fromJsonToObjectWithTypeParsejaElNode() throws Exception {
        JsonNode node = objectMapper.readTree("{\"id\":\"1\",\"nom\":\"prova\"}");

        Fixture fixture = jsonUtil.fromJsonToObjectWithType(node, Fixture.class);

        assertThat(fixture.id).isEqualTo("1");
        assertThat(fixture.nom).isEqualTo("prova");
    }

    @Test
    void fromJsonToObjectWithTypeAmbNodeNullFaServirObjecteBuit() throws Exception {
        Fixture fixture = jsonUtil.fromJsonToObjectWithType(null, Fixture.class);
        assertThat(fixture).isNotNull();
        assertThat(fixture.id).isNull();
    }

    @Test
    void fromJsonToMapCopiaElCampIdDesDelJsonIElsAltresPerReflexio() throws Exception {
        JsonNode node = objectMapper.readTree("{\"id\":\"1\",\"nom\":\"prova\"}");

        Map<String, Object> resultat = jsonUtil.fromJsonToMap(node, Fixture.class);

        assertThat(resultat).containsEntry("id", "1").containsEntry("nom", "prova");
    }

    @Test
    void fromJsonToMapAmbNodeNullRetornaNull() throws Exception {
        assertThat(jsonUtil.fromJsonToMap(null, Fixture.class)).isNull();
    }

    @Test
    void fillResourceWithFieldsMapAssignaCadaCampDelMapa() {
        Fixture resource = new Fixture();
        Map<String, Object> fields = new HashMap<>();
        fields.put("nom", "assignat-per-mapa");

        Object result = jsonUtil.fillResourceWithFieldsMap(resource, fields, null, null);

        assertThat(resource.nom).isEqualTo("assignat-per-mapa");
        assertThat(result).isNull();
    }

    @Test
    void fillResourceWithFieldsMapAmbCampDesconegutDelMapaSIgnora() {
        Fixture resource = new Fixture();
        Map<String, Object> fields = new HashMap<>();
        fields.put("campDesconegut", "valor");

        jsonUtil.fillResourceWithFieldsMap(resource, fields, null, null);
        // No llança excepció, simplement s'ignora el camp inexistent.
        assertThat(resource.nom).isNull();
    }

    @Test
    void fillResourceWithFieldsMapAmbFieldNameExtreuElValorParsejat() throws Exception {
        Fixture resource = new Fixture();
        ObjectNode valorJson = objectMapper.createObjectNode();
        JsonNode fieldValue = objectMapper.getNodeFactory().textNode("valor-de-camp");

        Object resultat = jsonUtil.fillResourceWithFieldsMap(resource, null, "nom", fieldValue);

        assertThat(resultat).isEqualTo("valor-de-camp");
    }

    @Test
    void fillResourceWithFieldsMapAmbFieldNameInexistentLlancaResourceFieldNotFoundException() {
        Fixture resource = new Fixture();
        JsonNode fieldValue = objectMapper.getNodeFactory().textNode("valor");

        assertThatThrownBy(() -> jsonUtil.fillResourceWithFieldsMap(resource, null, "campDesconegut", fieldValue))
                .isInstanceOf(ResourceFieldNotFoundException.class);
    }

    @Test
    void getInstanceDelegaEnElContextDaplicacio() {
        ApplicationContext context = Mockito.mock(ApplicationContext.class);
        Mockito.when(context.getBean(JsonUtil.class)).thenReturn(jsonUtil);

        jsonUtil.setApplicationContext(context);

        assertThat(JsonUtil.getInstance()).isSameAs(jsonUtil);
    }
}
