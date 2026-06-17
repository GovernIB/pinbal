package es.caib.pinbal.logic.config;

import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.scsp.common.utils.ScspPropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.stream.Stream;

/**
 * Configuració dels components de les llibreries SCSP.
 *
 * S'importa el fitxer XML original de la llibreria SCSP (v5.11) en lloc de
 * declarar els beans en Java: la configuració inclou un Hibernate sessionFactory
 * específic per SCSP, àlies sobre beans descoberts via component-scan
 * (clienteUnico, jaxbMarshaller) i propietats amb el prefix custom ${config:
 * resoltes per ScspPropertyPlaceholderConfigurer.
 *
 * @author Limit Tecnologies
 */
@Configuration
@ComponentScan(
		basePackages = {"es.scsp", BaseConfig.BASE_PACKAGE + ".scsp"},
		excludeFilters = {
				@Filter(type = FilterType.REGEX, pattern = "es\\.scsp\\.config\\..*"),
				// FOP (Apache XSL-FO) no és al classpath; evitem que Spring introspeccioni
				// FopFactorySingleton/HandlerPdfXsltTransform i llanci ClassNotFoundException.
				// Usem el subpaquet 'handler' per no excloure JustificanteMaker (es.scsp.common.pdf.*).
				@Filter(type = FilterType.REGEX, pattern = "es\\.scsp\\.common\\.pdf\\.handler\\..*")
		}
)
@ImportResource("classpath:application-context-scsp.xml")
public class ScspConfig {

	/**
	 * Substitut del PropertyPlaceholderConfigurer estàndard.
	 * Configurem el prefix "${config:" per mantenir la retrocompatibilitat amb el teu XML.
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		configurer.setPlaceholderPrefix("${config:");
		configurer.setIgnoreResourceNotFound(true);
		configurer.setIgnoreUnresolvablePlaceholders(true);
		return configurer;
	}

	/**
	 * Substitut / Instanciació del configurador específic de la llibreria SCSP.
	 * Alerta: Com que és una llibreria antiga de l'SCSP, li passem manualment el recurs
	 * externalitzat tal com feia l'XML.
	 */
	// BeanFactoryPostProcessor ha de ser static. Es llegeix la ruta del fitxer
	// directament de les system properties (no podem usar @Value en un @Bean static).
	@Bean
	public static ScspPropertyPlaceholderConfigurer sPropertyPlaceholderConfigurer() {
		ScspPropertyPlaceholderConfigurer scspConfigurer = new ScspPropertyPlaceholderConfigurer();
		scspConfigurer.setIgnoreResourceNotFound(true);
		scspConfigurer.setIgnoreUnresolvablePlaceholders(true);
		// Usar el mateix key que usa @PropertySource a EjbContextConfig
		var locations = Stream.of(BaseConfig.APP_PROPERTIES, BaseConfig.APP_SYSTEM_PROPERTIES)
				.map(System::getProperty)
				.filter(p -> p != null && !p.isBlank())
				.map(p -> new FileSystemResource(new File(p)))
				.toArray(FileSystemResource[]::new);
		if (locations.length > 0) {
			scspConfigurer.setLocations(locations);
		}
		return scspConfigurer;
	}

}
