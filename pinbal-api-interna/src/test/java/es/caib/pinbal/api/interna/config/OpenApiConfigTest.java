package es.caib.pinbal.api.interna.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OpenApiConfigTest {

    @Test
    public void testInstantiation() {
        assertNotNull(new OpenApiConfig());
    }

    @Test
    public void testServersUsenElContextPathDeLaAplicacio() {
        MockServletContext servletContext = new MockServletContext();
        servletContext.setContextPath("/pinbal2api/interna");

        OpenAPI openApi = new OpenApiConfig().openApi(servletContext);

        List<String> urls = openApi.getServers().stream().map(Server::getUrl).collect(Collectors.toList());
        assertEquals(List.of(
                "/pinbal2api/interna",
                "https://proves.caib.es/pinbal2api/interna",
                "https://dev.caib.es/pinbal2api/interna",
                "https://se.caib.es/pinbal2api/interna"), urls);
        assertEquals("Servidor per defecte", openApi.getServers().get(0).getDescription());
    }
}
