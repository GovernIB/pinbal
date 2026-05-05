/**
 * 
 */
package es.caib.pinbal.api.interna.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import java.util.List;

/**
 * Configuració de Spring web MVC.
 * 
 * @author Limit Tecnologies
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Bean
	public PageableHandlerMethodArgumentResolver pageableResolver() {
		PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
		resolver.setFallbackPageable(PageRequest.of(0, 10));
		return resolver;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(pageableResolver());
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUrlPathHelper(urlPathHelper());
	}

	@Bean
	public UrlPathHelper urlPathHelper() {
		UrlPathHelper urlPathHelper = new UrlPathHelper();
		urlPathHelper.setUrlDecode(true);
		urlPathHelper.setAlwaysUseFullPath(false);
		urlPathHelper.setRemoveSemicolonContent(false);
		return urlPathHelper;
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
		return builder -> builder
				.modulesToInstall(JavaTimeModule.class)
				.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

}
