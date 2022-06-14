package es.caib.pinbal.api.config;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

@Configuration
@EnableSwagger
@ComponentScan(basePackages = {"es.caib.pinbal.api.interna.controller"})
public class SwaggerConfig {

	private SpringSwaggerConfig springSwaggerConfig;
	 
    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }
 
    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiInfo(apiInfo())
                .apiVersion("1.0")
                .ignoredParameterTypes(ResponseEntity.class)
				.includePatterns(".*/recobriment/*.*");
    }
 
	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo(
				"API Consulta PINBAL",
				"API INTERNA de PINBAL",
				"", 	// URL de temes de servei
				"limit@limit.es",
				"",		// Llicència
				""); 	// URL Llicència
				
		return apiInfo;
	}
	
	/**
	 * Class that provides your applications url context path
	 */
	@Bean
	public ApiPathProvider apiPathProvider() {
		ApiPathProvider apiPathProvider = new ApiPathProvider(); //"http://localhost:8080");
		apiPathProvider.setDefaultSwaggerPathProvider(springSwaggerConfig.defaultSwaggerPathProvider());
		return apiPathProvider;
	}
}