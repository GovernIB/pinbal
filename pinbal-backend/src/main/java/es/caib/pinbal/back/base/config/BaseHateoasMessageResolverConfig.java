package es.caib.pinbal.back.base.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.mediatype.MessageResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Configuració del MessageResolver per a spring-hateoas.
 * 
 * @author Límit Tecnologies
 */
public abstract class BaseHateoasMessageResolverConfig {

	protected static final String BASE_BOOT_REST_MESSAGES = "base-boot-rest-messages";

	@Autowired
	private ApplicationContext context;

	@Bean
	@Primary
	public MessageResolver customMessageResolver() {
		return MessageResolver.of(lookupMessageSource());
	}

	protected String getCommonBasename() {
		return BASE_BOOT_REST_MESSAGES;
	}

	protected String getBasename() {
		return null;
	}

	protected String[] getBasenames() {
		return null;
	}

	protected abstract Locale getDefaultLocale();

	private AbstractMessageSource lookupMessageSource() {
		String commonBasename = getCommonBasename();
		String basename = getBasename();
		String[] basenames = getBasenames();
		List<Resource> candidates = loadResourceBundleResources(
				new String[] { getBasename() },
				false);
		if (commonBasename != null || basename != null || basenames != null) {
			ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
			messageSource.setDefaultLocale(getDefaultLocale());
			messageSource.setFallbackToSystemLocale(false);
			messageSource.setResourceLoader(context);
			if (basename != null) {
				String basenameWithClasspath = "classpath:" + basename;
				messageSource.setBasenames(basenameWithClasspath);
			}
			if (basenames != null) {
				String[] baseNamesWithClasspath = Arrays.stream(getBasenames()).map("classpath:"::concat).toArray(String[]::new);
				messageSource.setBasenames(baseNamesWithClasspath);
			}
			messageSource.setDefaultEncoding(StandardCharsets.UTF_8.toString());
			if (commonBasename != null) {
				List<Resource> commonBasenameResources = loadResourceBundleResources(
						new String[] { commonBasename },
						false);
				messageSource.setCommonMessages(loadProperties(commonBasenameResources));
			}
			return messageSource;
		} else {
			return null;
		}
	}

	private Properties loadProperties(List<Resource> sources) {
		PropertiesFactoryBean factory = new PropertiesFactoryBean();
		factory.setLocations(sources.toArray(new Resource[0]));
		factory.setFileEncoding(StandardCharsets.UTF_8.toString());
		try {
			factory.afterPropertiesSet();
			return factory.getObject();
		} catch (IOException ex) {
			throw new IllegalStateException("Could not load default properties from resources!", ex);
		}
	}

	private List<Resource> loadResourceBundleResources(String[] baseNames, boolean withWildcard) {
		try {
			List<Resource> resources = new ArrayList<>();
			for (String baseName: baseNames) {
				List<Resource> baseNameResources = Arrays.
						stream(context.getResources(String.format("classpath:%s%s.properties", baseName, withWildcard ? "*" : ""))).
						filter(Resource::exists).
						collect(Collectors.toList());
				resources.addAll(baseNameResources);
			}
			return resources;
		} catch (IOException ex) {
			return Collections.emptyList();
		}
	}

}
