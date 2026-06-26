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
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.List;

/**
 * Configuració de Spring web MVC.
 * 
 * @author Limit Tecnologies
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	// Accepta offsets amb i sense dos punts: +02:00 i +0200
	private static final DateTimeFormatter LENIENT_DATE_TIME = new DateTimeFormatterBuilder()
			.append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
			.optionalStart().appendOffsetId().optionalEnd()
			.optionalStart().appendOffset("+HHmm", "Z").optionalEnd()
			.toFormatter();

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

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(String.class, Date.class, source -> {
			// Normalitza les tres formes en que pot arribar el signe '+' de l'offset:
			//   %2B / %2b → no decodificat per JBoss/proxy
			//   ' '       → '+' no percent-encodat, decodificat com a espai
			String normalized = source.trim()
					.replace("%2B", "+")
					.replace("%2b", "+")
					.replace(" ", "+");
			OffsetDateTime odt = OffsetDateTime.parse(normalized, LENIENT_DATE_TIME);
			return Date.from(odt.toInstant());
		});
	}
}
