package es.caib.pinbal.api.externa.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Externa PINBAL",
                description = "API Externa de PINBAL",
                version = "v2.0",
                contact = @Contact(name = "Limit Tecnologies", url = "http://limit.es", email = "limit@limit.es")
        ),
        security = @SecurityRequirement(name = "basic")
)
@SecurityScheme(name = "basic", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {

    // Els servidors es construeixen amb el context path real de l'aplicació
    // (configurable en temps de compilació amb la propietat Maven pinbal.context.prefix)
    @Bean
    public OpenAPI openApi(ServletContext servletContext) {
        String contextPath = servletContext.getContextPath();
        return new OpenAPI()
                .addServersItem(new Server().url(contextPath).description("Servidor per defecte"))
                .addServersItem(new Server().url("https://proves.caib.es" + contextPath).description("Servidor de l'entorn de PROVES"))
                .addServersItem(new Server().url("https://dev.caib.es" + contextPath).description("Servidor de l'entorn de DESENVOLUPAMENT"))
                .addServersItem(new Server().url("https://se.caib.es" + contextPath).description("Servidor de l'entorn de SERVEIS ESTABLES"));
    }

}
