package es.caib.pinbal.logic.config;

import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.scsp.common.utils.ScspPropertyPlaceholderConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;

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
@ComponentScan({
		"es.scsp",
		BaseConfig.BASE_PACKAGE + ".scsp"
})
@ImportResource("classpath:application-context-scsp.xml")
public class ScspConfig {

	// Injectem la ruta del fitxer que abans tenies a ${es.caib.pinbal.properties.path}
	// Spring Boot la buscarà a les variables de sistema o d'entorn (-Des.caib.pinbal.properties.path)
	@Value("${es.caib.pinbal.properties.path:}")
	private String propertiesPath;

	/**
	 * Substitut del PropertyPlaceholderConfigurer estàndard.
	 * Configurem el prefix "${config:" per mantenir la retrocompatibilitat amb el teu XML.
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();

		// Mantenim exactament la lògica del XML:
		configurer.setPlaceholderPrefix("${config:");
		configurer.setIgnoreResourceNotFound(true);
		configurer.setIgnoreUnresolvablePlaceholders(true);

		// Nota: El SYSTEM_PROPERTIES_MODE_OVERRIDE és el comportament per defecte
		// a PropertySourcesPlaceholderConfigurer (revisa primer el sistema).

		return configurer;
	}

	/**
	 * Substitut / Instanciació del configurador específic de la llibreria SCSP.
	 * Alerta: Com que és una llibreria antiga de l'SCSP, li passem manualment el recurs
	 * externalitzat tal com feia l'XML.
	 */
	@Bean
	public ScspPropertyPlaceholderConfigurer sPropertyPlaceholderConfigurer() {
		ScspPropertyPlaceholderConfigurer scspConfigurer = new ScspPropertyPlaceholderConfigurer();

		scspConfigurer.setIgnoreResourceNotFound(true);
		scspConfigurer.setIgnoreUnresolvablePlaceholders(true);

		// Configurem la localització del fitxer si la variable s'ha informat
		if (propertiesPath != null && !propertiesPath.isEmpty()) {
			Resource resource = new FileSystemResource(new File(propertiesPath));
			scspConfigurer.setLocation(resource);
		}

		return scspConfigurer;
	}

}
