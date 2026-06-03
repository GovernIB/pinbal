package es.caib.pinbal.back.base.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Configuració de Springdoc OpenAPI.
 * 
 * @author Límit Tecnologies
 */
@Slf4j
public abstract class BaseOpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		String version = "Unknown";
		try {
			Manifest manifest = new Manifest(getClass().getResourceAsStream("/META-INF/MANIFEST.MF"));
			Attributes attributes = manifest.getMainAttributes();
			version = attributes.getValue("Implementation-Version");
		} catch (IOException ex) {
			log.error("No s'ha pogut obtenir la versió del fitxer MANIFEST.MF", ex);
		}
		OpenAPI openapi = new OpenAPI().info(new Info().title(getTitle()).version(version));
		// TODO l'ordenació d'aquesta forma no acaba d'anar be...
		openapi.setTags(tags());
		if (enableAuthComponent()) {
			openapi.
					components(
							new Components().addSecuritySchemes(
									"Bearer token",
									new SecurityScheme().
									type(SecurityScheme.Type.HTTP).
									scheme("bearer").
									bearerFormat("JWT").
									in(SecurityScheme.In.HEADER).
									name("Authorization"))).
					addSecurityItem(
							new SecurityRequirement().addList(
									"Bearer token",
									Arrays.asList("read", "write")));
		}
		return openapi;
	}

	protected List<Tag> tags() {
		return null;
	}

	protected abstract String getTitle();

	protected abstract boolean enableAuthComponent();

}
